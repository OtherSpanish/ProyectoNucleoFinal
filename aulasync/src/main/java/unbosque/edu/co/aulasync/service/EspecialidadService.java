package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.EspecialidadDTO;
import unbosque.edu.co.aulasync.entity.Especialidad;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.EspecialidadEnUsoException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.NombreDuplicadoException;
import unbosque.edu.co.aulasync.repository.CursoRepository;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.EspecialidadRepository;

/**
 * Lógica de negocio de las especialidades.
 *
 * <p>
 * Es la única clase que escribe en {@link EspecialidadRepository}. Consulta
 * {@link DocenteRepository} y {@link CursoRepository} para impedir eliminar
 * especialidades en uso. También hace las conversiones entre
 * {@link Especialidad} y {@link EspecialidadDTO}.
 * </p>
 *
 * @version 1.2
 */
@Service
public class EspecialidadService {

	private static final String ENTIDAD = "Especialidad";

	private final EspecialidadRepository especialidadRepository;
	private final DocenteRepository docenteRepository;
	private final CursoRepository cursoRepository;

	/**
	 * Crea el servicio.
	 *
	 * @param especialidadRepository acceso a la tabla de especialidades.
	 * @param docenteRepository      consulta de docentes que usan cada
	 *                               especialidad.
	 * @param cursoRepository        consulta de cursos que exigen cada
	 *                               especialidad.
	 */
	public EspecialidadService(EspecialidadRepository especialidadRepository, DocenteRepository docenteRepository,
			CursoRepository cursoRepository) {
		this.especialidadRepository = especialidadRepository;
		this.docenteRepository = docenteRepository;
		this.cursoRepository = cursoRepository;
	}

	/**
	 * Lista todas las especialidades en orden alfabético.
	 *
	 * @return lista de especialidades.
	 */
	@Transactional(readOnly = true)
	public List<EspecialidadDTO> listar() {
		List<EspecialidadDTO> resultado = new ArrayList<>();
		for (Especialidad especialidad : especialidadRepository.findAllByOrderByNombreAsc()) {
			resultado.add(convertirADTO(especialidad));
		}
		return resultado;
	}

	/**
	 * Busca una especialidad por id.
	 *
	 * @param id identificador.
	 * @return la especialidad.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public EspecialidadDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea una especialidad.
	 *
	 * @param dto datos de la especialidad.
	 * @return la especialidad creada.
	 * @throws CampoObligatorioException si falta el nombre.
	 * @throws NombreDuplicadoException  si el nombre ya existe.
	 */
	@Transactional
	public EspecialidadDTO crear(EspecialidadDTO dto) throws CampoObligatorioException, NombreDuplicadoException {
		LanzadorException.verificarCampoObligatorio(dto.getNombre(), "nombre");
		String nombre = dto.getNombre().trim();
		LanzadorException.verificarNombreDisponible(especialidadRepository.existsByNombreIgnoreCase(nombre),
				ENTIDAD, nombre);

		Especialidad guardada = especialidadRepository.save(convertirAEntidad(dto));
		return convertirADTO(guardada);
	}

	/**
	 * Actualiza nombre y descripción de una especialidad.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return la especialidad actualizada.
	 * @throws EntidadNoEncontradaException si no existe.
	 * @throws CampoObligatorioException    si falta el nombre.
	 * @throws NombreDuplicadoException     si el nombre pertenece a otra.
	 */
	@Transactional
	public EspecialidadDTO actualizar(Long id, EspecialidadDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, NombreDuplicadoException {
		Especialidad especialidad = obtenerEntidad(id);
		LanzadorException.verificarCampoObligatorio(dto.getNombre(), "nombre");
		String nombre = dto.getNombre().trim();
		LanzadorException.verificarNombreDisponible(
				especialidadRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id), ENTIDAD, nombre);

		especialidad.setNombre(nombre);
		especialidad.setDescripcion(limpiarDescripcion(dto.getDescripcion()));
		return convertirADTO(especialidadRepository.save(especialidad));
	}

	/**
	 * Elimina una especialidad que no esté asignada a ningún docente ni exigida
	 * por ningún curso.
	 *
	 * @param id identificador.
	 * @throws EntidadNoEncontradaException si no existe.
	 * @throws EspecialidadEnUsoException   si algún docente o curso la usa.
	 */
	@Transactional
	public void eliminar(Long id) throws EntidadNoEncontradaException, EspecialidadEnUsoException {
		Especialidad especialidad = obtenerEntidad(id);
		boolean enUso = docenteRepository.existsByEspecialidadesId(id) || cursoRepository.existsByEspecialidadId(id);
		LanzadorException.verificarEspecialidadSinUso(enUso, especialidad.getNombre());
		especialidadRepository.delete(especialidad);
	}

	/**
	 * Busca la entidad por id. La usan los servicios de docentes y cursos para
	 * asignar especialidades.
	 *
	 * @param id identificador.
	 * @return la entidad encontrada.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public Especialidad obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Especialidad especialidad = especialidadRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(especialidad, ENTIDAD, id);
		return especialidad;
	}

	/**
	 * Convierte una entidad en DTO.
	 *
	 * @param especialidad entidad a convertir.
	 * @return DTO equivalente.
	 */
	public EspecialidadDTO convertirADTO(Especialidad especialidad) {
		return new EspecialidadDTO(especialidad.getId(), especialidad.getNombre(), especialidad.getDescripcion());
	}

	/**
	 * Convierte un DTO en una entidad nueva, sin id.
	 *
	 * @param dto DTO a convertir.
	 * @return entidad lista para guardarse.
	 */
	public Especialidad convertirAEntidad(EspecialidadDTO dto) {
		String nombre = null;
		if (dto.getNombre() != null) {
			nombre = dto.getNombre().trim();
		}
		return new Especialidad(nombre, limpiarDescripcion(dto.getDescripcion()));
	}

	private String limpiarDescripcion(String descripcion) {
		if (descripcion == null || descripcion.isBlank()) {
			return null;
		}
		return descripcion.trim();
	}
}
