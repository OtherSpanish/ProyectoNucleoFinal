package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.GrupoDTO;
import unbosque.edu.co.aulasync.entity.Curso;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.Especialidad;
import unbosque.edu.co.aulasync.entity.FranjaDocente;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CargaDocenteExcedidaException;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.DisponibilidadDocenteInsuficienteException;
import unbosque.edu.co.aulasync.exception.DocenteSinEspecialidadException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.GrupoDuplicadoException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.repository.GrupoRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * Lógica de negocio de los grupos de cada curso.
 *
 * <p>
 * Al crear o editar un grupo valida:
 * </p>
 * <ul>
 * <li>Número entre {@value #MIN_NUMERO} y {@value #MAX_NUMERO}, único dentro
 * del curso.</li>
 * <li>Que el docente tenga la especialidad que exige el curso.</li>
 * <li>Que la suma de horas semanales de todos sus grupos no supere el tope de
 * su dedicación ni sus horas disponibles.</li>
 * <li>Que tenga al menos un bloque de disponibilidad continuo tan largo como
 * una sesión del curso.</li>
 * </ul>
 *
 * <p>
 * Un grupo admite entre {@value #MIN_INSCRITOS} y {@value #MAX_INSCRITOS}
 * inscritos: con menos del mínimo no se abre y el motor no lo programa. Al
 * actualizar o eliminar un grupo se publica {@link CambioDatosMaestros} para
 * sincronizar el horario guardado.
 * </p>
 *
 * @version 1.2
 */
@Service
public class GrupoService {

	/** Mínimo de estudiantes inscritos para abrir un grupo. */
	public static final int MIN_INSCRITOS = 10;

	/** Máximo de estudiantes inscritos en un grupo. */
	public static final int MAX_INSCRITOS = 30;

	/** Número mínimo de grupo. */
	public static final int MIN_NUMERO = 1;

	/** Número máximo de grupo. */
	public static final int MAX_NUMERO = 20;

	private static final String ENTIDAD = "Grupo";

	private final GrupoRepository grupoRepository;
	private final SesionHorarioRepository sesionHorarioRepository;
	private final CursoService cursoService;
	private final DocenteService docenteService;
	private final DisponibilidadService disponibilidadService;
	private final ApplicationEventPublisher eventos;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param grupoRepository         acceso a la tabla de grupos.
	 * @param sesionHorarioRepository sesiones del horario de cada grupo.
	 * @param cursoService            búsqueda de cursos.
	 * @param docenteService          búsqueda de docentes.
	 * @param disponibilidadService   franjas de disponibilidad de los docentes.
	 * @param eventos                 publicador de eventos de Spring.
	 */
	public GrupoService(GrupoRepository grupoRepository, SesionHorarioRepository sesionHorarioRepository,
			CursoService cursoService, DocenteService docenteService, DisponibilidadService disponibilidadService,
			ApplicationEventPublisher eventos) {
		this.grupoRepository = grupoRepository;
		this.sesionHorarioRepository = sesionHorarioRepository;
		this.cursoService = cursoService;
		this.docenteService = docenteService;
		this.disponibilidadService = disponibilidadService;
		this.eventos = eventos;
	}

	// ------------------------------------------------------------------
	// CRUD
	// ------------------------------------------------------------------

	/**
	 * Lista los grupos ordenados por semestre, curso y número, con filtros
	 * opcionales.
	 *
	 * @param cursoId    solo los de este curso, o {@code null}.
	 * @param docenteId  solo los de este docente, o {@code null}.
	 * @param semestreId solo los de cursos de este semestre, o {@code null}.
	 * @return lista de grupos filtrada.
	 */
	@Transactional(readOnly = true)
	public List<GrupoDTO> listar(Long cursoId, Long docenteId, Long semestreId) {
		List<GrupoDTO> resultado = new ArrayList<>();
		for (Grupo grupo : grupoRepository.findAllByOrderByCursoSemestreNumeroAscCursoNombreAscNumeroAsc()) {
			boolean pasaCurso = cursoId == null || grupo.getCurso().getId().equals(cursoId);
			boolean pasaDocente = docenteId == null || grupo.getDocente().getId().equals(docenteId);
			boolean pasaSemestre = semestreId == null || grupo.getCurso().getSemestre().getId().equals(semestreId);
			if (pasaCurso && pasaDocente && pasaSemestre) {
				resultado.add(convertirADTO(grupo));
			}
		}
		return resultado;
	}

	/**
	 * Busca un grupo por id.
	 *
	 * @param id identificador.
	 * @return el grupo.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public GrupoDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea un grupo sin estudiantes. No afecta el horario guardado porque un
	 * grupo nuevo no está abierto.
	 *
	 * @param dto número, cursoId y docenteId.
	 * @return el grupo creado.
	 * @throws CampoObligatorioException                   si falta algún dato.
	 * @throws DatoFueraDeRangoException                   si el número está
	 *                                                     fuera de rango.
	 * @throws EntidadNoEncontradaException                si el curso o el
	 *                                                     docente no existen.
	 * @throws GrupoDuplicadoException                     si el número ya existe
	 *                                                     en el curso.
	 * @throws DocenteSinEspecialidadException             si el docente no tiene
	 *                                                     la especialidad del
	 *                                                     curso.
	 * @throws CargaDocenteExcedidaException               si el docente supera
	 *                                                     su tope de horas.
	 * @throws DisponibilidadDocenteInsuficienteException si la disponibilidad
	 *                                                     del docente no
	 *                                                     alcanza.
	 */
	@Transactional
	public GrupoDTO crear(GrupoDTO dto) throws CampoObligatorioException, DatoFueraDeRangoException,
			EntidadNoEncontradaException, GrupoDuplicadoException, DocenteSinEspecialidadException,
			CargaDocenteExcedidaException, DisponibilidadDocenteInsuficienteException {
		validarDatos(dto);
		Curso curso = cursoService.obtenerEntidad(dto.getCursoId());
		Docente docente = docenteService.obtenerEntidad(dto.getDocenteId());
		LanzadorException.verificarGrupoDisponible(
				grupoRepository.existsByCursoIdAndNumero(curso.getId(), dto.getNumero()), curso.getCodigo(),
				dto.getNumero());
		verificarDocenteApto(docente, curso, null);

		Grupo grupo = new Grupo(dto.getNumero(), curso, docente);
		return convertirADTO(grupoRepository.save(grupo));
	}

	/**
	 * Actualiza número, curso y docente de un grupo y sincroniza el horario.
	 * Sus estudiantes se conservan.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return el grupo actualizado.
	 * @throws EntidadNoEncontradaException                si el grupo, el curso
	 *                                                     o el docente no
	 *                                                     existen.
	 * @throws CampoObligatorioException                   si falta algún dato.
	 * @throws DatoFueraDeRangoException                   si el número está
	 *                                                     fuera de rango.
	 * @throws GrupoDuplicadoException                     si el número ya existe
	 *                                                     en el curso.
	 * @throws DocenteSinEspecialidadException             si el docente no tiene
	 *                                                     la especialidad del
	 *                                                     curso.
	 * @throws CargaDocenteExcedidaException               si el docente supera
	 *                                                     su tope de horas.
	 * @throws DisponibilidadDocenteInsuficienteException si la disponibilidad
	 *                                                     del docente no
	 *                                                     alcanza.
	 */
	@Transactional
	public GrupoDTO actualizar(Long id, GrupoDTO dto) throws EntidadNoEncontradaException, CampoObligatorioException,
			DatoFueraDeRangoException, GrupoDuplicadoException, DocenteSinEspecialidadException,
			CargaDocenteExcedidaException, DisponibilidadDocenteInsuficienteException {
		Grupo grupo = obtenerEntidad(id);
		validarDatos(dto);
		Curso curso = cursoService.obtenerEntidad(dto.getCursoId());
		Docente docente = docenteService.obtenerEntidad(dto.getDocenteId());
		LanzadorException.verificarGrupoDisponible(
				grupoRepository.existsByCursoIdAndNumeroAndIdNot(curso.getId(), dto.getNumero(), id),
				curso.getCodigo(), dto.getNumero());
		verificarDocenteApto(docente, curso, id);

		grupo.setNumero(dto.getNumero());
		grupo.setCurso(curso);
		grupo.setDocente(docente);
		Grupo guardado = grupoRepository.save(grupo);
		eventos.publishEvent(new CambioDatosMaestros("Grupo " + id + " actualizado"));
		return convertirADTO(guardado);
	}

	/**
	 * Elimina un grupo junto con sus inscripciones y sus sesiones del horario.
	 *
	 * @param id identificador.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional
	public void eliminar(Long id) throws EntidadNoEncontradaException {
		Grupo grupo = obtenerEntidad(id);
		sesionHorarioRepository.deleteAll(sesionHorarioRepository.findByGrupoId(id));
		grupoRepository.delete(grupo);
		grupoRepository.flush();
		eventos.publishEvent(new CambioDatosMaestros("Grupo " + id + " eliminado"));
	}

	// ------------------------------------------------------------------
	// Soporte para otros servicios
	// ------------------------------------------------------------------

	/**
	 * Busca la entidad por id. La usan la inscripción de estudiantes y el
	 * portal docente.
	 *
	 * @param id identificador.
	 * @return la entidad encontrada.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public Grupo obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Grupo grupo = grupoRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(grupo, ENTIDAD, id);
		return grupo;
	}

	/**
	 * Convierte una entidad en DTO, con datos del curso y del docente, la
	 * cantidad de inscritos y si está abierto.
	 *
	 * @param grupo entidad a convertir.
	 * @return DTO equivalente.
	 */
	public GrupoDTO convertirADTO(Grupo grupo) {
		Curso curso = grupo.getCurso();
		Docente docente = grupo.getDocente();
		int inscritos = grupo.getEstudiantes().size();
		return new GrupoDTO(grupo.getId(), grupo.getNumero(), curso.getId(), curso.getCodigo(), curso.getNombre(),
				curso.getSemestre().getNumero(), docente.getId(), docente.getUsuario().getNombre(), inscritos,
				inscritos >= MIN_INSCRITOS);
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private void validarDatos(GrupoDTO dto) throws CampoObligatorioException, DatoFueraDeRangoException {
		LanzadorException.verificarObjetoObligatorio(dto.getNumero(), "numero");
		LanzadorException.verificarRango(dto.getNumero(), "numero", MIN_NUMERO, MAX_NUMERO);
		LanzadorException.verificarObjetoObligatorio(dto.getCursoId(), "cursoId");
		LanzadorException.verificarObjetoObligatorio(dto.getDocenteId(), "docenteId");
	}

	private void verificarDocenteApto(Docente docente, Curso curso, Long grupoExcluido)
			throws DocenteSinEspecialidadException, CargaDocenteExcedidaException,
			DisponibilidadDocenteInsuficienteException {
		String nombreDocente = docente.getUsuario().getNombre();

		boolean tieneEspecialidad = false;
		for (Especialidad especialidad : docente.getEspecialidades()) {
			if (especialidad.getId().equals(curso.getEspecialidad().getId())) {
				tieneEspecialidad = true;
			}
		}
		LanzadorException.verificarDocenteTieneEspecialidad(tieneEspecialidad, nombreDocente, curso.getNombre(),
				curso.getEspecialidad().getNombre());

		int horas = curso.getIntensidad().getHorasSemanales();
		for (Grupo otro : grupoRepository.findByDocenteId(docente.getId())) {
			boolean esElMismo = grupoExcluido != null && otro.getId().equals(grupoExcluido);
			if (!esElMismo) {
				horas = horas + otro.getCurso().getIntensidad().getHorasSemanales();
			}
		}
		LanzadorException.verificarCargaDocente(horas, docente.getDedicacion().getHorasMaximas(), nombreDocente);

		List<FranjaDocente> franjas = disponibilidadService.listarEntidades(docente.getId());
		int disponibles = 0;
		int bloqueMasLargo = 0;
		int bloqueActual = 0;
		FranjaDocente anterior = null;
		for (FranjaDocente franja : franjas) {
			int largo = franja.getHoraFin() - franja.getHoraInicio();
			disponibles = disponibles + largo;
			boolean continua = anterior != null && anterior.getDia() == franja.getDia()
					&& anterior.getHoraFin() == franja.getHoraInicio();
			if (continua) {
				bloqueActual = bloqueActual + largo;
			} else {
				bloqueActual = largo;
			}
			if (bloqueActual > bloqueMasLargo) {
				bloqueMasLargo = bloqueActual;
			}
			anterior = franja;
		}

		LanzadorException.verificarDisponibilidadDocente(horas <= disponibles, nombreDocente,
				"necesita " + horas + " horas semanales y tiene " + disponibles + " disponibles");
		int duracion = curso.getIntensidad().getDuracionHoras();
		LanzadorException.verificarDisponibilidadDocente(bloqueMasLargo >= duracion, nombreDocente,
				"las sesiones de " + curso.getNombre() + " duran " + duracion
						+ " horas y su bloque continuo más largo es de " + bloqueMasLargo);
	}
}
