package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.entity.Curso;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.entity.SesionHorario;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.model.ResultadoSincronizacion;
import unbosque.edu.co.aulasync.model.ValidacionMovimiento;
import unbosque.edu.co.aulasync.repository.GrupoRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * Mantiene el horario guardado coherente con los datos maestros.
 *
 * <ol>
 * <li>Ajusta la cantidad de sesiones de cada grupo: sin sesiones si no tiene
 * el mínimo de inscritos; tantas como la intensidad de su curso si está
 * abierto (las nuevas quedan pendientes de ubicar).</li>
 * <li>Pasa a "sin ubicar" las sesiones que perdieron su aula.</li>
 * <li>Revalida una por una las sesiones ubicadas con las reglas del editor y
 * desubica las que ya no cumplen.</li>
 * </ol>
 *
 * <p>
 * Se ejecuta automáticamente al recibir un {@link CambioDatosMaestros}, o a
 * mano desde el comité. Si no hay horario guardado, no hace nada.
 * </p>
 *
 * @version 1.0
 */
@Service
public class SincronizacionHorarioService {

	/** Causa de una sesión nueva que el comité debe ubicar. */
	public static final String CAUSA_PENDIENTE = "PENDIENTE_UBICACION_MANUAL";

	/** Causa de una sesión que dejó de cumplir alguna regla. */
	public static final String CAUSA_INVALIDADA = "INVALIDADA_POR_CAMBIO";

	/** Causa de una sesión cuya aula fue eliminada. */
	public static final String CAUSA_AULA_ELIMINADA = "AULA_ELIMINADA";

	private static final int MAX_DETALLE = 500;

	private static final Logger LOG = LoggerFactory.getLogger(SincronizacionHorarioService.class);

	private final SesionHorarioRepository sesionHorarioRepository;
	private final GrupoRepository grupoRepository;
	private final EdicionHorarioService edicionHorarioService;
	private final BusquedaService busquedaService;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param sesionHorarioRepository acceso a las sesiones guardadas.
	 * @param grupoRepository         lectura de grupos.
	 * @param edicionHorarioService   evaluación de reglas del editor.
	 * @param busquedaService         índice del horario por prefijo.
	 */
	public SincronizacionHorarioService(SesionHorarioRepository sesionHorarioRepository,
			GrupoRepository grupoRepository, EdicionHorarioService edicionHorarioService,
			BusquedaService busquedaService) {
		this.sesionHorarioRepository = sesionHorarioRepository;
		this.grupoRepository = grupoRepository;
		this.edicionHorarioService = edicionHorarioService;
		this.busquedaService = busquedaService;
	}

	/**
	 * Escucha los cambios de datos maestros y sincroniza el horario dentro de la
	 * misma transacción del cambio.
	 *
	 * @param evento cambio publicado por algún servicio.
	 */
	@EventListener
	public void alCambiarDatosMaestros(CambioDatosMaestros evento) {
		ResultadoSincronizacion resultado = sincronizarTodo();
		if (resultado.isHayHorario() && !resultado.getCambios().isEmpty()) {
			LOG.info("Horario sincronizado tras '{}': {} creadas, {} eliminadas, {} desubicadas.",
					evento.getOrigen(), resultado.getSesionesCreadas(), resultado.getSesionesEliminadas(),
					resultado.getSesionesDesubicadas());
		}
	}

	/**
	 * Sincroniza todo el horario guardado con los datos actuales.
	 *
	 * @return cuántas sesiones se crearon, eliminaron o desubicaron, y por qué.
	 */
	@Transactional
	public ResultadoSincronizacion sincronizarTodo() {
		List<String> cambios = new ArrayList<>();
		if (sesionHorarioRepository.count() == 0) {
			return new ResultadoSincronizacion(false, 0, 0, 0, cambios);
		}

		Map<Long, List<SesionHorario>> porGrupo = new HashMap<>();
		for (SesionHorario sesion : sesionHorarioRepository.findAll()) {
			List<SesionHorario> lista = porGrupo.get(sesion.getGrupo().getId());
			if (lista == null) {
				lista = new ArrayList<>();
				porGrupo.put(sesion.getGrupo().getId(), lista);
			}
			lista.add(sesion);
		}

		int creadas = 0;
		int eliminadas = 0;
		for (Grupo grupo : grupoRepository.findAll()) {
			List<SesionHorario> actuales = porGrupo.get(grupo.getId());
			if (actuales == null) {
				actuales = new ArrayList<>();
			}
			Curso curso = grupo.getCurso();
			boolean abierto = grupo.getEstudiantes().size() >= GrupoService.MIN_INSCRITOS;
			int requeridas = 0;
			if (abierto) {
				requeridas = curso.getIntensidad().getSesionesSemana();
			}
			String nombre = curso.getCodigo() + " G" + grupo.getNumero();

			if (actuales.size() > requeridas) {
				int quitadas = quitarSobrantes(actuales, requeridas);
				eliminadas = eliminadas + quitadas;
				if (abierto) {
					cambios.add(nombre + ": se quitaron " + quitadas + " sesiones porque cambió la intensidad.");
				} else {
					cambios.add(nombre + ": se quitaron sus sesiones porque ya no tiene el mínimo de inscritos.");
				}
			} else if (actuales.size() < requeridas) {
				int agregadas = agregarFaltantes(grupo, actuales, requeridas);
				creadas = creadas + agregadas;
				cambios.add(nombre + ": se agregaron " + agregadas + " sesiones pendientes de ubicar.");
			}
		}
		sesionHorarioRepository.flush();

		int desubicadas = 0;
		for (SesionHorario sesion : sesionHorarioRepository.findAll()) {
			String nombre = sesion.getGrupo().getCurso().getCodigo() + " G" + sesion.getGrupo().getNumero()
					+ " sesión " + sesion.getNumeroSesion();
			boolean perdioAula = sesion.getDia() != null && sesion.getAula() == null;
			if (perdioAula) {
				desubicar(sesion, CAUSA_AULA_ELIMINADA, "El aula donde estaba la sesión fue eliminada.");
				desubicadas++;
				cambios.add(nombre + ": quedó sin ubicar porque su aula fue eliminada.");
			} else if (estaUbicada(sesion)) {
				ValidacionMovimiento validacion = edicionHorarioService.evaluarUbicacionActual(sesion);
				if (!validacion.isValido()) {
					String detalle = String.join("; ", validacion.getMensajes());
					desubicar(sesion, CAUSA_INVALIDADA, detalle);
					desubicadas++;
					cambios.add(nombre + ": quedó sin ubicar. " + detalle);
				}
			}
		}

		busquedaService.reconstruirIndiceHorario(sesionHorarioRepository.findAll());
		return new ResultadoSincronizacion(true, creadas, eliminadas, desubicadas, cambios);
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private int quitarSobrantes(List<SesionHorario> actuales, int requeridas) {
		List<SesionHorario> aQuitar = new ArrayList<>();
		for (SesionHorario sesion : actuales) {
			if (sesion.getNumeroSesion() > requeridas) {
				aQuitar.add(sesion);
			}
		}
		int sobrantes = actuales.size() - requeridas;
		for (int i = 0; aQuitar.size() < sobrantes && i < actuales.size(); i++) {
			if (!aQuitar.contains(actuales.get(i))) {
				aQuitar.add(actuales.get(i));
			}
		}
		sesionHorarioRepository.deleteAll(aQuitar);
		return aQuitar.size();
	}

	private int agregarFaltantes(Grupo grupo, List<SesionHorario> actuales, int requeridas) {
		Set<Integer> usados = new HashSet<>();
		for (SesionHorario sesion : actuales) {
			usados.add(sesion.getNumeroSesion());
		}
		int agregadas = 0;
		for (int numero = 1; numero <= requeridas && actuales.size() + agregadas < requeridas; numero++) {
			if (!usados.contains(numero)) {
				sesionHorarioRepository.save(new SesionHorario(grupo, numero, null, null, null, CAUSA_PENDIENTE,
						"Sesión nueva por un cambio en el grupo o en el curso. Ubícala en el editor o vuelve a generar el horario."));
				agregadas++;
			}
		}
		return agregadas;
	}

	private void desubicar(SesionHorario sesion, String causa, String detalle) {
		sesion.setDia(null);
		sesion.setHoraInicio(null);
		sesion.setAula(null);
		sesion.setCausa(causa);
		if (detalle != null && detalle.length() > MAX_DETALLE) {
			sesion.setDetalleCausa(detalle.substring(0, MAX_DETALLE));
		} else {
			sesion.setDetalleCausa(detalle);
		}
		sesionHorarioRepository.saveAndFlush(sesion);
	}

	private boolean estaUbicada(SesionHorario sesion) {
		return sesion.getDia() != null && sesion.getHoraInicio() != null && sesion.getAula() != null;
	}
}
