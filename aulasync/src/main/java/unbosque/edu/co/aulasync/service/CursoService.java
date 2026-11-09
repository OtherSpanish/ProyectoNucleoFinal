package unbosque.edu.co.aulasync.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.CursoRequestDto;
import unbosque.edu.co.aulasync.dto.CursoResponseDto;
import unbosque.edu.co.aulasync.exception.ConflictoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.ValidacionException;
import unbosque.edu.co.aulasync.model.Curso;
import unbosque.edu.co.aulasync.repository.CursoRepository;
import unbosque.edu.co.aulasync.repository.SeccionRepository;

/**
 * Servicio con la lógica de negocio de los cursos (RF1).
 *
 * <p>
 * Accede al repositorio, convierte entre la entidad {@link Curso} y los DTO y
 * delega las validaciones en {@link LanzadorException}, que lanza las
 * excepciones de negocio. El {@code GlobalExceptionHandler} las traduce después
 * a respuestas HTTP; este servicio nunca crea excepciones por su cuenta.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class CursoService {

	private final CursoRepository cursoRepo;
	private final SeccionRepository seccionRepo;

	/**
	 * Crea el servicio con los repositorios que necesita.
	 *
	 * @param cursoRepo   repositorio de cursos
	 * @param seccionRepo repositorio de secciones, usado para verificar
	 *                    dependencias al eliminar
	 */
	public CursoService(CursoRepository cursoRepo, SeccionRepository seccionRepo) {
		this.cursoRepo = cursoRepo;
		this.seccionRepo = seccionRepo;
	}

	/**
	 * Lista todos los cursos registrados.
	 *
	 * @return cursos como DTO de salida
	 */
	public List<CursoResponseDto> listar() {
		return cursoRepo.findAll().stream().map(this::aDto).toList();
	}

	/**
	 * Busca un curso por su identificador.
	 *
	 * @param id identificador del curso
	 * @return el curso encontrado
	 * @throws EntidadNoEncontradaException si no existe un curso con ese id
	 */
	public CursoResponseDto obtener(Long id) throws EntidadNoEncontradaException {
		return aDto(buscar(id));
	}

	/**
	 * Valida y guarda un curso nuevo.
	 *
	 * @param dto datos del curso a crear
	 * @return el curso guardado, con su identificador
	 * @throws ValidacionException si algún dato falta o incumple las reglas
	 */
	@Transactional(rollbackFor = Exception.class)
	public CursoResponseDto crear(CursoRequestDto dto) throws ValidacionException {
		validar(dto);

		Curso curso = new Curso();
		curso.setNombre(dto.getNombre().trim());
		curso.setSesionesSemana(dto.getSesionesSemana());
		curso.setDuracionHoras(dto.getDuracionHoras());
		curso.setTipoAula(dto.getTipoAula());

		return aDto(cursoRepo.save(curso));
	}

	/**
	 * Elimina un curso, siempre que no tenga secciones asociadas.
	 *
	 * @param id identificador del curso
	 * @throws EntidadNoEncontradaException si el curso no existe
	 * @throws ConflictoException           si el curso tiene secciones asociadas
	 */
	@Transactional(rollbackFor = Exception.class)
	public void eliminar(Long id) throws EntidadNoEncontradaException, ConflictoException {
		Curso curso = buscar(id);
		LanzadorException.verificarCursoSinSecciones(seccionRepo.existsByCursoId(id));
		cursoRepo.delete(curso);
	}

	// ---------- Utilidades ----------

	/**
	 * Busca un curso y verifica que exista.
	 *
	 * @param id identificador del curso
	 * @return el curso encontrado
	 * @throws EntidadNoEncontradaException si no existe
	 */
	private Curso buscar(Long id) throws EntidadNoEncontradaException {
		Curso curso = cursoRepo.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(curso, "Curso", id);
		return curso;
	}

	/**
	 * Comprueba las reglas del curso en orden: nombre, sesiones por semana,
	 * duración y tipo de aula.
	 *
	 * @param dto datos del curso a validar
	 * @throws ValidacionException si alguna regla se incumple
	 */
	private void validar(CursoRequestDto dto) throws ValidacionException {
		LanzadorException.verificarNombreCurso(dto.getNombre());
		LanzadorException.verificarSesionesSemana(dto.getSesionesSemana());
		LanzadorException.verificarDuracionSesion(dto.getDuracionHoras());
		LanzadorException.verificarTipoAula(dto.getTipoAula());
	}

	private CursoResponseDto aDto(Curso c) {
		return new CursoResponseDto(c.getId(), c.getNombre(), c.getSesionesSemana(), c.getDuracionHoras(),
				c.getTipoAula());
	}
}