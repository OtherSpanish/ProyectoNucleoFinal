package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.EstudianteDTO;
import unbosque.edu.co.aulasync.entity.Estudiante;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.entity.Semestre;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CodigoDuplicadoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.repository.EstudianteRepository;
import unbosque.edu.co.aulasync.repository.GrupoRepository;

/**
 * Lógica de negocio de los estudiantes.
 *
 * <p>
 * Valida código único, nombre y semestre obligatorios. El código se guarda en mayúsculas
 * y sin espacios a los lados; el correo es opcional y se guarda en minúsculas.
 * Al eliminar un estudiante, primero se le quita de todos sus grupos y, si
 * estaba inscrito en alguno, se publica {@link CambioDatosMaestros}, porque un
 * grupo puede quedar con menos del mínimo de inscritos y cerrarse. Mantiene
 * actualizado el índice de búsqueda por prefijo.
 * </p>
 *
 * @version 1.4
 */
@Service
public class EstudianteService {

	private static final String ENTIDAD = "Estudiante";

	private final EstudianteRepository estudianteRepository;
	private final GrupoRepository grupoRepository;
	private final BusquedaService busquedaService;
	private final SemestreService semestreService;
	private final ApplicationEventPublisher eventos;

	/**
	 * Crea el servicio.
	 *
	 * @param estudianteRepository acceso a la tabla de estudiantes.
	 * @param grupoRepository      inscripciones del estudiante en grupos.
	 * @param busquedaService      índice de búsqueda por prefijo.
	 * @param semestreService      búsqueda del semestre que cursa.
	 * @param eventos              publicador de eventos de Spring.
	 */
	public EstudianteService(EstudianteRepository estudianteRepository, GrupoRepository grupoRepository,
			BusquedaService busquedaService, SemestreService semestreService, ApplicationEventPublisher eventos) {
		this.estudianteRepository = estudianteRepository;
		this.grupoRepository = grupoRepository;
		this.busquedaService = busquedaService;
		this.semestreService = semestreService;
		this.eventos = eventos;
	}

	// ------------------------------------------------------------------
	// CRUD
	// ------------------------------------------------------------------

	/**
	 * Lista los estudiantes en orden alfabético.
	 *
	 * @return lista de estudiantes.
	 */
	@Transactional(readOnly = true)
	public List<EstudianteDTO> listar() {
		List<EstudianteDTO> resultado = new ArrayList<>();
		for (Estudiante estudiante : estudianteRepository.findAllByOrderByNombreAsc()) {
			resultado.add(convertirADTO(estudiante));
		}
		return resultado;
	}

	/**
	 * Busca estudiantes cuyo código o alguna palabra del nombre empiece por el
	 * prefijo, usando el Trie.
	 *
	 * @param prefijo texto buscado.
	 * @return hasta {@value BusquedaService#LIMITE_RESULTADOS} estudiantes, en
	 *         orden lexicográfico.
	 * @throws CampoObligatorioException si el prefijo es nulo o vacío.
	 */
	@Transactional(readOnly = true)
	public List<EstudianteDTO> buscarPorPrefijo(String prefijo) throws CampoObligatorioException {
		LanzadorException.verificarCampoObligatorio(prefijo, "prefijo");
		List<EstudianteDTO> resultado = new ArrayList<>();
		for (Long id : busquedaService.buscarEstudiantes(prefijo)) {
			Estudiante estudiante = estudianteRepository.findById(id).orElse(null);
			if (estudiante != null) {
				resultado.add(convertirADTO(estudiante));
			}
		}
		return resultado;
	}

	/**
	 * Busca un estudiante por id.
	 *
	 * @param id identificador.
	 * @return el estudiante.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public EstudianteDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea un estudiante y lo indexa para la búsqueda.
	 *
	 * @param dto código, nombre, semestreId y correo opcional.
	 * @return el estudiante creado.
	 * @throws CampoObligatorioException    si falta el código, el nombre o el
	 *                                      semestre.
	 * @throws CodigoDuplicadoException     si el código ya existe.
	 * @throws EntidadNoEncontradaException si el semestre no existe.
	 */
	@Transactional
	public EstudianteDTO crear(EstudianteDTO dto)
			throws CampoObligatorioException, CodigoDuplicadoException, EntidadNoEncontradaException {
		validarDatos(dto);
		String codigo = normalizarCodigo(dto.getCodigo());
		LanzadorException.verificarCodigoDisponible(estudianteRepository.existsByCodigoIgnoreCase(codigo), ENTIDAD,
				codigo);
		Semestre semestre = semestreService.obtenerEntidad(dto.getSemestreId());

		Estudiante estudiante = new Estudiante(codigo, dto.getNombre().trim(), normalizarCorreo(dto.getCorreo()),
				semestre);
		Estudiante guardado = estudianteRepository.save(estudiante);
		busquedaService.indexarEstudiante(guardado);
		return convertirADTO(guardado);
	}

	/**
	 * Actualiza código, nombre, correo y semestre de un estudiante y lo
	 * reindexa. No afecta el horario porque sus grupos no cambian.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return el estudiante actualizado.
	 * @throws EntidadNoEncontradaException si el estudiante o el semestre no
	 *                                      existen.
	 * @throws CampoObligatorioException    si falta el código, el nombre o el
	 *                                      semestre.
	 * @throws CodigoDuplicadoException     si el código es de otro estudiante.
	 */
	@Transactional
	public EstudianteDTO actualizar(Long id, EstudianteDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, CodigoDuplicadoException {
		Estudiante estudiante = obtenerEntidad(id);
		validarDatos(dto);
		String codigo = normalizarCodigo(dto.getCodigo());
		LanzadorException.verificarCodigoDisponible(estudianteRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id),
				ENTIDAD, codigo);

		estudiante.setCodigo(codigo);
		estudiante.setNombre(dto.getNombre().trim());
		estudiante.setCorreo(normalizarCorreo(dto.getCorreo()));
		estudiante.setSemestre(semestreService.obtenerEntidad(dto.getSemestreId()));
		Estudiante guardado = estudianteRepository.save(estudiante);
		busquedaService.indexarEstudiante(guardado);
		return convertirADTO(guardado);
	}

	/**
	 * Quita al estudiante de todos sus grupos, lo elimina, lo saca del índice de
	 * búsqueda y, si estaba inscrito en algún grupo, sincroniza el horario.
	 *
	 * @param id identificador.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional
	public void eliminar(Long id) throws EntidadNoEncontradaException {
		Estudiante estudiante = obtenerEntidad(id);
		List<Grupo> grupos = grupoRepository.findByEstudiantesId(id);
		for (Grupo grupo : grupos) {
			Iterator<Estudiante> iterador = grupo.getEstudiantes().iterator();
			while (iterador.hasNext()) {
				if (iterador.next().getId().equals(id)) {
					iterador.remove();
				}
			}
			grupoRepository.save(grupo);
		}
		estudianteRepository.delete(estudiante);
		estudianteRepository.flush();
		busquedaService.quitarEstudiante(id);
		if (!grupos.isEmpty()) {
			eventos.publishEvent(new CambioDatosMaestros("Estudiante " + id + " eliminado"));
		}
	}

	// ------------------------------------------------------------------
	// Soporte para otros servicios
	// ------------------------------------------------------------------

	/**
	 * Busca la entidad por id. La usa la inscripción de estudiantes en grupos.
	 *
	 * @param id identificador.
	 * @return la entidad encontrada.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public Estudiante obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Estudiante estudiante = estudianteRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(estudiante, ENTIDAD, id);
		return estudiante;
	}

	/**
	 * Convierte una entidad en DTO.
	 *
	 * @param estudiante entidad a convertir.
	 * @return DTO equivalente.
	 */
	public EstudianteDTO convertirADTO(Estudiante estudiante) {
		Long semestreId = null;
		Integer semestreNumero = null;
		if (estudiante.getSemestre() != null) {
			semestreId = estudiante.getSemestre().getId();
			semestreNumero = estudiante.getSemestre().getNumero();
		}
		return new EstudianteDTO(estudiante.getId(), estudiante.getCodigo(), estudiante.getNombre(),
				estudiante.getCorreo(), semestreId, semestreNumero);
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private void validarDatos(EstudianteDTO dto) throws CampoObligatorioException {
		LanzadorException.verificarCampoObligatorio(dto.getCodigo(), "codigo");
		LanzadorException.verificarCampoObligatorio(dto.getNombre(), "nombre");
		LanzadorException.verificarObjetoObligatorio(dto.getSemestreId(), "semestreId");
	}

	private String normalizarCodigo(String codigo) {
		return codigo.trim().toUpperCase();
	}

	private String normalizarCorreo(String correo) {
		if (correo == null || correo.isBlank()) {
			return null;
		}
		return correo.trim().toLowerCase();
	}
}
