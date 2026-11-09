package unbosque.edu.co.aulasync.service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.entity.Aula;
import unbosque.edu.co.aulasync.entity.Curso;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.FranjaDocente;
import unbosque.edu.co.aulasync.entity.FranjaSede;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.entity.Sede;
import unbosque.edu.co.aulasync.model.CausaDescartada;
import unbosque.edu.co.aulasync.model.ResultadoGeneracion;
import unbosque.edu.co.aulasync.model.SesionGenerada;
import unbosque.edu.co.aulasync.motor.AulaMotor;
import unbosque.edu.co.aulasync.motor.DocenteMotor;
import unbosque.edu.co.aulasync.motor.MotivoRechazo;
import unbosque.edu.co.aulasync.motor.MotorHorario;
import unbosque.edu.co.aulasync.motor.SedeMotor;
import unbosque.edu.co.aulasync.motor.Sesion;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.GrupoRepository;
import unbosque.edu.co.aulasync.repository.SedeRepository;

/**
 * Puente entre la base de datos y el {@link MotorHorario}.
 *
 * <p>
 * Lee los datos maestros, los convierte a los objetos planos del motor (que no
 * conoce JPA), lo ejecuta y traduce el resultado a {@link ResultadoGeneracion}
 * para la API. Solo entran al motor los grupos abiertos (al menos
 * {@value GrupoService#MIN_INSCRITOS} inscritos) y las aulas disponibles.
 * </p>
 *
 * @version 1.0
 */
@Service
public class MotorHorarioService {

	private static final Locale ESPANOL = Locale.of("es", "CO");

	private final GrupoRepository grupoRepository;
	private final SedeRepository sedeRepository;
	private final AulaRepository aulaRepository;
	private final DisponibilidadService disponibilidadService;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param grupoRepository       lectura de grupos.
	 * @param sedeRepository        lectura de sedes y sus franjas.
	 * @param aulaRepository        lectura de aulas.
	 * @param disponibilidadService franjas de disponibilidad de los docentes.
	 */
	public MotorHorarioService(GrupoRepository grupoRepository, SedeRepository sedeRepository,
			AulaRepository aulaRepository, DisponibilidadService disponibilidadService) {
		this.grupoRepository = grupoRepository;
		this.sedeRepository = sedeRepository;
		this.aulaRepository = aulaRepository;
		this.disponibilidadService = disponibilidadService;
	}

	/**
	 * Ejecuta el motor con los datos actuales y devuelve el resultado sin
	 * guardar nada.
	 *
	 * @return sesiones ubicadas y sin ubicar, grupos no abiertos y avisos.
	 */
	@Transactional(readOnly = true)
	public ResultadoGeneracion generar() {
		Map<Long, Grupo> gruposPorId = new HashMap<>();
		Map<Long, DocenteMotor> docentes = new HashMap<>();
		List<String> noAbiertos = new ArrayList<>();
		List<Sesion> sesiones = new ArrayList<>();

		for (Grupo grupo : grupoRepository.findAllByOrderByCursoSemestreNumeroAscCursoNombreAscNumeroAsc()) {
			int inscritos = grupo.getEstudiantes().size();
			Curso curso = grupo.getCurso();
			if (inscritos < GrupoService.MIN_INSCRITOS) {
				noAbiertos.add(curso.getCodigo() + " " + curso.getNombre() + " G" + grupo.getNumero() + ": "
						+ inscritos + " inscritos (mínimo " + GrupoService.MIN_INSCRITOS + ").");
				continue;
			}
			gruposPorId.put(grupo.getId(), grupo);
			agregarDocente(grupo.getDocente(), docentes);
			int cantidad = curso.getIntensidad().getSesionesSemana();
			for (int i = 1; i <= cantidad; i++) {
				sesiones.add(new Sesion(grupo.getId(), i, curso.getId(), curso.getCodigo(), grupo.getNumero(),
						curso.getSemestre().getNumero(), curso.isNucleoComun(), grupo.getDocente().getId(),
						curso.getIntensidad().getDuracionHoras(), curso.getTipoAula(), inscritos));
			}
		}

		Map<Long, String> nombresSedes = new HashMap<>();
		List<SedeMotor> sedes = construirSedes(nombresSedes);
		List<AulaMotor> aulas = construirAulas();

		MotorHorario motor = new MotorHorario(sesiones, new ArrayList<>(docentes.values()), sedes, aulas);
		MotorHorario.Resultado resultado = motor.generar();

		List<SesionGenerada> ubicadas = new ArrayList<>();
		for (Sesion sesion : resultado.getUbicadas()) {
			ubicadas.add(convertir(sesion, gruposPorId, nombresSedes));
		}
		List<SesionGenerada> sinUbicar = new ArrayList<>();
		for (Sesion sesion : resultado.getSinUbicar()) {
			sinUbicar.add(convertir(sesion, gruposPorId, nombresSedes));
		}
		ordenarUbicadas(ubicadas);
		ordenarSinUbicar(sinUbicar);

		return new ResultadoGeneracion(gruposPorId.size(), noAbiertos, ubicadas, sinUbicar, resultado.getAvisos());
	}

	/**
	 * Nombre en español de un día, con la primera letra en mayúscula.
	 *
	 * @param dia día de la semana.
	 * @return por ejemplo "Lunes".
	 */
	public static String nombreDia(DayOfWeek dia) {
		String nombre = dia.getDisplayName(TextStyle.FULL, ESPANOL);
		return nombre.substring(0, 1).toUpperCase(ESPANOL) + nombre.substring(1);
	}

	// ------------------------------------------------------------------
	// Construcción de la entrada del motor
	// ------------------------------------------------------------------

	private void agregarDocente(Docente docente, Map<Long, DocenteMotor> docentes) {
		if (docentes.containsKey(docente.getId())) {
			return;
		}
		DocenteMotor motor = new DocenteMotor(docente.getId(), docente.getDedicacion().getHorasMaximas());
		for (FranjaDocente franja : disponibilidadService.listarEntidades(docente.getId())) {
			motor.habilitar(franja.getDia(), franja.getHoraInicio(), franja.getHoraFin());
		}
		docentes.put(docente.getId(), motor);
	}

	private List<SedeMotor> construirSedes(Map<Long, String> nombres) {
		List<SedeMotor> sedes = new ArrayList<>();
		for (Sede sede : sedeRepository.findAllByOrderByNombreAsc()) {
			SedeMotor motor = new SedeMotor(sede.getId(), sede.getNombre(), sede.isExclusivaPrimerSemestre(),
					sede.getDiasPrimerSemestre());
			for (FranjaSede franja : sede.getFranjas()) {
				motor.habilitar(franja.getDia(), franja.getHoraInicio(), franja.getHoraFin());
			}
			sedes.add(motor);
			nombres.put(sede.getId(), sede.getNombre());
		}
		return sedes;
	}

	private List<AulaMotor> construirAulas() {
		List<AulaMotor> aulas = new ArrayList<>();
		for (Aula aula : aulaRepository.findAllByOrderBySedeNombreAscNombreAsc()) {
			if (aula.isDisponible()) {
				aulas.add(new AulaMotor(aula.getId(), aula.getNombre(), aula.getCapacidad(), aula.getTipo(),
						aula.getSede().getId()));
			}
		}
		return aulas;
	}

	// ------------------------------------------------------------------
	// Conversión del resultado
	// ------------------------------------------------------------------

	private SesionGenerada convertir(Sesion sesion, Map<Long, Grupo> grupos, Map<Long, String> nombresSedes) {
		Grupo grupo = grupos.get(sesion.getGrupoId());
		SesionGenerada salida = new SesionGenerada();
		salida.setGrupoId(sesion.getGrupoId());
		salida.setNumeroSesion(sesion.getNumeroSesion());
		salida.setCursoId(sesion.getCursoId());
		salida.setCursoCodigo(sesion.getCursoCodigo());
		salida.setCursoNombre(grupo.getCurso().getNombre());
		salida.setSemestre(sesion.getSemestre());
		salida.setNumeroGrupo(sesion.getNumeroGrupo());
		salida.setNucleoComun(sesion.isNucleoComun());
		salida.setDocenteId(sesion.getDocenteId());
		salida.setDocenteNombre(grupo.getDocente().getUsuario().getNombre());
		salida.setEstudiantes(sesion.getEstudiantes());
		salida.setDuracionHoras(sesion.getDuracionHoras());

		if (sesion.getFranja() != null) {
			salida.setDia(sesion.getFranja().getDia());
			salida.setDiaNombre(nombreDia(sesion.getFranja().getDia()));
			salida.setHoraInicio(sesion.getFranja().getHoraInicio());
			salida.setHoraFin(sesion.getFranja().getHoraInicio() + sesion.getDuracionHoras());
			salida.setAulaId(sesion.getAula().getId());
			salida.setAulaNombre(sesion.getAula().getNombre());
			salida.setSedeId(sesion.getFranja().getSedeId());
			salida.setSedeNombre(nombresSedes.get(sesion.getFranja().getSedeId()));
		} else {
			MotivoRechazo causa = sesion.getCausaSinUbicar();
			salida.setCausa(causa);
			if (causa != null) {
				salida.setCausaDescripcion(causa.getDescripcion());
			}
			salida.setDesglose(construirDesglose(sesion.getDesgloseCausas()));
		}
		return salida;
	}

	private List<CausaDescartada> construirDesglose(Map<MotivoRechazo, Integer> causas) {
		List<CausaDescartada> desglose = new ArrayList<>();
		for (Map.Entry<MotivoRechazo, Integer> entrada : causas.entrySet()) {
			desglose.add(new CausaDescartada(entrada.getKey(), entrada.getKey().getDescripcion(),
					entrada.getValue()));
		}
		Collections.sort(desglose, new Comparator<CausaDescartada>() {
			@Override
			public int compare(CausaDescartada a, CausaDescartada b) {
				return Integer.compare(b.getFranjas(), a.getFranjas());
			}
		});
		return desglose;
	}

	private void ordenarUbicadas(List<SesionGenerada> sesiones) {
		Collections.sort(sesiones, new Comparator<SesionGenerada>() {
			@Override
			public int compare(SesionGenerada a, SesionGenerada b) {
				int porSemestre = Integer.compare(a.getSemestre(), b.getSemestre());
				if (porSemestre != 0) {
					return porSemestre;
				}
				int porDia = a.getDia().compareTo(b.getDia());
				if (porDia != 0) {
					return porDia;
				}
				return Integer.compare(a.getHoraInicio(), b.getHoraInicio());
			}
		});
	}

	private void ordenarSinUbicar(List<SesionGenerada> sesiones) {
		Collections.sort(sesiones, new Comparator<SesionGenerada>() {
			@Override
			public int compare(SesionGenerada a, SesionGenerada b) {
				int porSemestre = Integer.compare(a.getSemestre(), b.getSemestre());
				if (porSemestre != 0) {
					return porSemestre;
				}
				int porCurso = a.getCursoCodigo().compareTo(b.getCursoCodigo());
				if (porCurso != 0) {
					return porCurso;
				}
				return Integer.compare(a.getNumeroGrupo(), b.getNumeroGrupo());
			}
		});
	}
}
