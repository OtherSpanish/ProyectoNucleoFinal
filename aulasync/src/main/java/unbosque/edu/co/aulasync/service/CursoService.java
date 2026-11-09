package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.CursoDTO;
import unbosque.edu.co.aulasync.entity.Curso;
import unbosque.edu.co.aulasync.entity.Especialidad;
import unbosque.edu.co.aulasync.entity.Semestre;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CodigoDuplicadoException;
import unbosque.edu.co.aulasync.exception.EntidadConGruposException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.repository.CursoRepository;
import unbosque.edu.co.aulasync.repository.GrupoRepository;

/**
 * Lógica de negocio de los cursos (asignaturas).
 *
 * <p>
 * Valida código único y que existan el semestre y la especialidad. El código
 * se guarda en mayúsculas y {@code nucleoComun} es {@code false} si no se
 * envía. Un curso con grupos no se puede eliminar. Mantiene actualizado el
 * índice de búsqueda por prefijo y hace las conversiones entre {@link Curso} y
 * {@link CursoDTO}.
 * </p>
 *
 * <p>
 * Al actualizar un curso se publica {@link CambioDatosMaestros}, porque un
 * cambio de intensidad, tipo de aula, semestre, especialidad o núcleo común
 * puede invalidar las sesiones ya ubicadas de sus grupos.
 * </p>
 *
 * @version 1.4
 */
@Service
public class CursoService {

	private static final String ENTIDAD = "Curso";

	private final CursoRepository cursoRepository;
	private final GrupoRepository grupoRepository;
	private final SemestreService semestreService;
	private final EspecialidadService especialidadService;
	private final BusquedaService busquedaService;
	private final ApplicationEventPublisher eventos;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param cursoRepository     acceso a la tabla de cursos.
	 * @param grupoRepository     consulta de grupos por curso.
	 * @param semestreService     búsqueda de semestres.
	 * @param especialidadService búsqueda de especialidades.
	 * @param busquedaService     índice de búsqueda por prefijo.
	 * @param eventos             publicador de eventos de Spring.
	 */
	public CursoService(CursoRepository cursoRepository, GrupoRepository grupoRepository,
			SemestreService semestreService, EspecialidadService especialidadService,
			BusquedaService busquedaService, ApplicationEventPublisher eventos) {
		this.cursoRepository = cursoRepository;
		this.grupoRepository = grupoRepository;
		this.semestreService = semestreService;
		this.especialidadService = especialidadService;
		this.busquedaService = busquedaService;
		this.eventos = eventos;
	}

	// ------------------------------------------------------------------
	// CRUD
	// ------------------------------------------------------------------

	/**
	 * Lista los cursos ordenados por semestre y nombre, con filtros opcionales.
	 *
	 * @param semestreId     solo los de este semestre, o {@code null}.
	 * @param especialidadId solo los de esta especialidad, o {@code null}.
	 * @return lista de cursos filtrada.
	 */
	@Transactional(readOnly = true)
	public List<CursoDTO> listar(Long semestreId, Long especialidadId) {
		List<CursoDTO> resultado = new ArrayList<>();
		for (Curso curso : cursoRepository.findAllByOrderBySemestreNumeroAscNombreAsc()) {
			boolean pasaSemestre = semestreId == null || curso.getSemestre().getId().equals(semestreId);
			boolean pasaEspecialidad = especialidadId == null
					|| curso.getEspecialidad().getId().equals(especialidadId);
			if (pasaSemestre && pasaEspecialidad) {
				resultado.add(convertirADTO(curso));
			}
		}
		return resultado;
	}

	/**
	 * Busca cursos cuyo código o alguna palabra del nombre empiece por el
	 * prefijo, usando el Trie.
	 *
	 * @param prefijo texto buscado.
	 * @return hasta {@value BusquedaService#LIMITE_RESULTADOS} cursos, en orden
	 *         lexicográfico.
	 * @throws CampoObligatorioException si el prefijo es nulo o vacío.
	 */
	@Transactional(readOnly = true)
	public List<CursoDTO> buscarPorPrefijo(String prefijo) throws CampoObligatorioException {
		LanzadorException.verificarCampoObligatorio(prefijo, "prefijo");
		List<CursoDTO> resultado = new ArrayList<>();
		for (Long id : busquedaService.buscarCursos(prefijo)) {
			Curso curso = cursoRepository.findById(id).orElse(null);
			if (curso != null) {
				resultado.add(convertirADTO(curso));
			}
		}
		return resultado;
	}

	/**
	 * Busca un curso por id.
	 *
	 * @param id identificador.
	 * @return el curso.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public CursoDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea un curso y lo indexa para la búsqueda. No afecta el horario guardado
	 * porque un curso nuevo no tiene grupos.
	 *
	 * @param dto código, nombre, semestreId, especialidadId, tipoAula,
	 *            intensidad y nucleoComun (opcional).
	 * @return el curso creado.
	 * @throws CampoObligatorioException    si falta algún dato.
	 * @throws EntidadNoEncontradaException si el semestre o la especialidad no
	 *                                      existen.
	 * @throws CodigoDuplicadoException     si el código ya existe.
	 */
	@Transactional
	public CursoDTO crear(CursoDTO dto)
			throws CampoObligatorioException, EntidadNoEncontradaException, CodigoDuplicadoException {
		validarDatos(dto);
		String codigo = normalizarCodigo(dto.getCodigo());
		LanzadorException.verificarCodigoDisponible(cursoRepository.existsByCodigoIgnoreCase(codigo), ENTIDAD,
				codigo);
		Semestre semestre = semestreService.obtenerEntidad(dto.getSemestreId());
		Especialidad especialidad = especialidadService.obtenerEntidad(dto.getEspecialidadId());

		Curso curso = new Curso(codigo, dto.getNombre().trim(), semestre, especialidad, dto.getTipoAula(),
				dto.getIntensidad(), leerNucleoComun(dto));
		Curso guardado = cursoRepository.save(curso);
		busquedaService.indexarCurso(guardado);
		return convertirADTO(guardado);
	}

	/**
	 * Actualiza todos los datos de un curso, lo reindexa y sincroniza el
	 * horario.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return el curso actualizado.
	 * @throws EntidadNoEncontradaException si el curso, el semestre o la
	 *                                      especialidad no existen.
	 * @throws CampoObligatorioException    si falta algún dato.
	 * @throws CodigoDuplicadoException     si el código es de otro curso.
	 */
	@Transactional
	public CursoDTO actualizar(Long id, CursoDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, CodigoDuplicadoException {
		Curso curso = obtenerEntidad(id);
		validarDatos(dto);
		String codigo = normalizarCodigo(dto.getCodigo());
		LanzadorException.verificarCodigoDisponible(cursoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id),
				ENTIDAD, codigo);
		Semestre semestre = semestreService.obtenerEntidad(dto.getSemestreId());
		Especialidad especialidad = especialidadService.obtenerEntidad(dto.getEspecialidadId());

		curso.setCodigo(codigo);
		curso.setNombre(dto.getNombre().trim());
		curso.setSemestre(semestre);
		curso.setEspecialidad(especialidad);
		curso.setTipoAula(dto.getTipoAula());
		curso.setIntensidad(dto.getIntensidad());
		curso.setNucleoComun(leerNucleoComun(dto));
		Curso guardado = cursoRepository.save(curso);
		busquedaService.indexarCurso(guardado);
		eventos.publishEvent(new CambioDatosMaestros("Curso " + id + " actualizado"));
		return convertirADTO(guardado);
	}

	/**
	 * Elimina un curso sin grupos y lo quita del índice de búsqueda. No afecta
	 * el horario guardado porque un curso sin grupos no tiene sesiones.
	 *
	 * @param id identificador.
	 * @throws EntidadNoEncontradaException si no existe.
	 * @throws EntidadConGruposException    si el curso tiene grupos.
	 */
	@Transactional
	public void eliminar(Long id) throws EntidadNoEncontradaException, EntidadConGruposException {
		Curso curso = obtenerEntidad(id);
		LanzadorException.verificarSinGrupos(grupoRepository.existsByCursoId(id), "el curso " + curso.getCodigo());
		cursoRepository.delete(curso);
		busquedaService.quitarCurso(id);
	}

	// ------------------------------------------------------------------
	// Soporte para otros servicios
	// ------------------------------------------------------------------

	/**
	 * Busca la entidad por id. La usan los grupos y el motor.
	 *
	 * @param id identificador.
	 * @return la entidad encontrada.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public Curso obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Curso curso = cursoRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(curso, ENTIDAD, id);
		return curso;
	}

	/**
	 * Convierte una entidad en DTO, con los datos informativos de semestre,
	 * especialidad e intensidad.
	 *
	 * @param curso entidad a convertir.
	 * @return DTO equivalente.
	 */
	public CursoDTO convertirADTO(Curso curso) {
		return new CursoDTO(curso.getId(), curso.getCodigo(), curso.getNombre(), curso.getSemestre().getId(),
				curso.getSemestre().getNumero(), curso.getEspecialidad().getId(), curso.getEspecialidad().getNombre(),
				curso.getTipoAula(), curso.getIntensidad(), curso.isNucleoComun());
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private void validarDatos(CursoDTO dto) throws CampoObligatorioException {
		LanzadorException.verificarCampoObligatorio(dto.getCodigo(), "codigo");
		LanzadorException.verificarCampoObligatorio(dto.getNombre(), "nombre");
		LanzadorException.verificarObjetoObligatorio(dto.getSemestreId(), "semestreId");
		LanzadorException.verificarObjetoObligatorio(dto.getEspecialidadId(), "especialidadId");
		LanzadorException.verificarObjetoObligatorio(dto.getTipoAula(), "tipoAula");
		LanzadorException.verificarObjetoObligatorio(dto.getIntensidad(), "intensidad");
	}

	private boolean leerNucleoComun(CursoDTO dto) {
		if (dto.getNucleoComun() == null) {
			return false;
		}
		return dto.getNucleoComun();
	}

	private String normalizarCodigo(String codigo) {
		return codigo.trim().toUpperCase();
	}
}
