package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.AulaDTO;
import unbosque.edu.co.aulasync.entity.Aula;
import unbosque.edu.co.aulasync.entity.Sede;
import unbosque.edu.co.aulasync.entity.SesionHorario;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.NombreDuplicadoException;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.model.TipoAula;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * Lógica de negocio de las aulas.
 *
 * <p>
 * Valida nombre único por sede, tipo y capacidad entre
 * {@value #MIN_CAPACIDAD} y {@value #MAX_CAPACIDAD}. También hace las
 * conversiones entre {@link Aula} y {@link AulaDTO}.
 * </p>
 *
 * <p>
 * Al actualizar o eliminar un aula se publica {@link CambioDatosMaestros}.
 * Antes de eliminarla, sus sesiones quedan sin ubicar con causa
 * {@value SincronizacionHorarioService#CAUSA_AULA_ELIMINADA}.
 * </p>
 *
 * @version 1.1
 */
@Service
public class AulaService {

	/** Capacidad mínima de un aula. */
	public static final int MIN_CAPACIDAD = 1;

	/** Capacidad máxima de un aula. */
	public static final int MAX_CAPACIDAD = 300;

	private static final String ENTIDAD = "Aula";

	private final AulaRepository aulaRepository;
	private final SesionHorarioRepository sesionHorarioRepository;
	private final SedeService sedeService;
	private final ApplicationEventPublisher eventos;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param aulaRepository          acceso a la tabla de aulas.
	 * @param sesionHorarioRepository sesiones del horario ubicadas en cada aula.
	 * @param sedeService             búsqueda de sedes.
	 * @param eventos                 publicador de eventos de Spring.
	 */
	public AulaService(AulaRepository aulaRepository, SesionHorarioRepository sesionHorarioRepository,
			SedeService sedeService, ApplicationEventPublisher eventos) {
		this.aulaRepository = aulaRepository;
		this.sesionHorarioRepository = sesionHorarioRepository;
		this.sedeService = sedeService;
		this.eventos = eventos;
	}

	// ------------------------------------------------------------------
	// CRUD
	// ------------------------------------------------------------------

	/**
	 * Lista las aulas ordenadas por sede y nombre, con filtros opcionales.
	 *
	 * @param sedeId          solo las de esta sede, o {@code null}.
	 * @param tipo            solo las de este tipo, o {@code null}.
	 * @param capacidadMinima solo las de al menos esta capacidad, o
	 *                        {@code null}.
	 * @param soloDisponibles si es {@code true}, excluye las no disponibles.
	 * @return lista de aulas filtrada.
	 */
	@Transactional(readOnly = true)
	public List<AulaDTO> listar(Long sedeId, TipoAula tipo, Integer capacidadMinima, Boolean soloDisponibles) {
		List<AulaDTO> resultado = new ArrayList<>();
		for (Aula aula : aulaRepository.findAllByOrderBySedeNombreAscNombreAsc()) {
			boolean pasaSede = sedeId == null || aula.getSede().getId().equals(sedeId);
			boolean pasaTipo = tipo == null || aula.getTipo() == tipo;
			boolean pasaCapacidad = capacidadMinima == null || aula.getCapacidad() >= capacidadMinima;
			boolean pasaDisponible = soloDisponibles == null || !soloDisponibles || aula.isDisponible();
			if (pasaSede && pasaTipo && pasaCapacidad && pasaDisponible) {
				resultado.add(convertirADTO(aula));
			}
		}
		return resultado;
	}

	/**
	 * Busca un aula por id.
	 *
	 * @param id identificador.
	 * @return el aula.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public AulaDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea un aula. Si no se envía {@code disponible}, queda disponible. No
	 * afecta el horario guardado: el aula nueva solo se usa al generar o editar.
	 *
	 * @param dto datos del aula.
	 * @return el aula creada.
	 * @throws CampoObligatorioException    si falta nombre, capacidad, tipo o
	 *                                      sede.
	 * @throws DatoFueraDeRangoException    si la capacidad está fuera de rango.
	 * @throws EntidadNoEncontradaException si la sede no existe.
	 * @throws NombreDuplicadoException     si el nombre ya existe en la sede.
	 */
	@Transactional
	public AulaDTO crear(AulaDTO dto) throws CampoObligatorioException, DatoFueraDeRangoException,
			EntidadNoEncontradaException, NombreDuplicadoException {
		validarDatos(dto);
		Sede sede = sedeService.obtenerEntidad(dto.getSedeId());
		String nombre = dto.getNombre().trim();
		boolean repetido = aulaRepository.existsBySedeIdAndNombreIgnoreCase(sede.getId(), nombre);
		LanzadorException.verificarNombreDisponible(repetido, ENTIDAD + " en " + sede.getNombre(), nombre);

		Aula aula = new Aula(nombre, dto.getCapacidad(), dto.getTipo(), leerDisponible(dto), sede);
		return convertirADTO(aulaRepository.save(aula));
	}

	/**
	 * Actualiza todos los datos de un aula, incluida su sede, y sincroniza el
	 * horario.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return el aula actualizada.
	 * @throws EntidadNoEncontradaException si el aula o la sede no existen.
	 * @throws CampoObligatorioException    si falta un dato.
	 * @throws DatoFueraDeRangoException    si la capacidad está fuera de rango.
	 * @throws NombreDuplicadoException     si el nombre ya existe en la sede.
	 */
	@Transactional
	public AulaDTO actualizar(Long id, AulaDTO dto) throws EntidadNoEncontradaException, CampoObligatorioException,
			DatoFueraDeRangoException, NombreDuplicadoException {
		Aula aula = obtenerEntidad(id);
		validarDatos(dto);
		Sede sede = sedeService.obtenerEntidad(dto.getSedeId());
		String nombre = dto.getNombre().trim();
		boolean repetido = aulaRepository.existsBySedeIdAndNombreIgnoreCaseAndIdNot(sede.getId(), nombre, id);
		LanzadorException.verificarNombreDisponible(repetido, ENTIDAD + " en " + sede.getNombre(), nombre);

		aula.setNombre(nombre);
		aula.setCapacidad(dto.getCapacidad());
		aula.setTipo(dto.getTipo());
		aula.setDisponible(leerDisponible(dto));
		aula.setSede(sede);
		Aula guardada = aulaRepository.saveAndFlush(aula);
		eventos.publishEvent(new CambioDatosMaestros("Aula " + id + " actualizada"));
		return convertirADTO(guardada);
	}

	/**
	 * Elimina un aula. Antes deja sin ubicar las sesiones que estaban en ella.
	 *
	 * @param id identificador.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional
	public void eliminar(Long id) throws EntidadNoEncontradaException {
		Aula aula = obtenerEntidad(id);
		for (SesionHorario sesion : sesionHorarioRepository.findByAulaId(id)) {
			sesion.setDia(null);
			sesion.setHoraInicio(null);
			sesion.setAula(null);
			sesion.setCausa(SincronizacionHorarioService.CAUSA_AULA_ELIMINADA);
			sesion.setDetalleCausa("El aula " + aula.getNombre() + " de " + aula.getSede().getNombre()
					+ " fue eliminada. Ubica la sesión en el editor o vuelve a generar el horario.");
			sesionHorarioRepository.save(sesion);
		}
		sesionHorarioRepository.flush();
		aulaRepository.delete(aula);
		aulaRepository.flush();
		eventos.publishEvent(new CambioDatosMaestros("Aula " + id + " eliminada"));
	}

	// ------------------------------------------------------------------
	// Soporte para otros servicios
	// ------------------------------------------------------------------

	/**
	 * Busca la entidad por id. La usan el motor y la edición del horario.
	 *
	 * @param id identificador.
	 * @return la entidad encontrada.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public Aula obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Aula aula = aulaRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(aula, ENTIDAD, id);
		return aula;
	}

	/**
	 * Convierte una entidad en DTO.
	 *
	 * @param aula entidad a convertir.
	 * @return DTO equivalente.
	 */
	public AulaDTO convertirADTO(Aula aula) {
		return new AulaDTO(aula.getId(), aula.getNombre(), aula.getCapacidad(), aula.getTipo(), aula.isDisponible(),
				aula.getSede().getId(), aula.getSede().getNombre());
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private void validarDatos(AulaDTO dto) throws CampoObligatorioException, DatoFueraDeRangoException {
		LanzadorException.verificarCampoObligatorio(dto.getNombre(), "nombre");
		LanzadorException.verificarObjetoObligatorio(dto.getCapacidad(), "capacidad");
		LanzadorException.verificarRango(dto.getCapacidad(), "capacidad", MIN_CAPACIDAD, MAX_CAPACIDAD);
		LanzadorException.verificarObjetoObligatorio(dto.getTipo(), "tipo");
		LanzadorException.verificarObjetoObligatorio(dto.getSedeId(), "sedeId");
	}

	private boolean leerDisponible(AulaDTO dto) {
		if (dto.getDisponible() == null) {
			return true;
		}
		return dto.getDisponible();
	}
}
