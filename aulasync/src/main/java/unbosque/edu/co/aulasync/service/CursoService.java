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
import unbosque.edu.co.aulasync.model.Seccion;
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
	private final HorarioService horarioService;

	/**
	 * Crea el servicio con los repositorios que necesita.
	 *
	 * @param cursoRepo   repositorio de cursos
	 * @param seccionRepo repositorio de secciones, usado para verificar
	 *                    dependencias al eliminar
	 * @param horarioService servicio del horario guardado, que se sincroniza al
	 *                    editar un curso
	 */
	public CursoService(CursoRepository cursoRepo, SeccionRepository seccionRepo, HorarioService horarioService) {
		this.cursoRepo = cursoRepo;
		this.seccionRepo = seccionRepo;
		this.horarioService = horarioService;
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
	 * Edita un curso. Si cambian las sesiones por semana, la duración o el tipo
	 * de aula, se ajustan las sesiones guardadas de sus secciones: se crean o
	 * descartan las que correspondan y, si cambió la duración o el tipo de aula,
	 * todas vuelven a "sin ubicar" para que el Comité las acomode de nuevo.
	 *
	 * @param id  identificador del curso
	 * @param dto datos nuevos del curso
	 * @return el curso actualizado
	 * @throws EntidadNoEncontradaException si el curso no existe
	 * @throws ValidacionException          si los datos son inválidos
	 */
	@Transactional(rollbackFor = Exception.class)
	public CursoResponseDto actualizar(Long id, CursoRequestDto dto)
			throws EntidadNoEncontradaException, ValidacionException {
		Curso curso = buscar(id);
		validar(dto);

		boolean cambioFormato = curso.getDuracionHoras() != dto.getDuracionHoras()
				|| curso.getTipoAula() != dto.getTipoAula();
		boolean cambioSesiones = curso.getSesionesSemana() != dto.getSesionesSemana();

		curso.setNombre(dto.getNombre().trim());
		curso.setSesionesSemana(dto.getSesionesSemana());
		curso.setDuracionHoras(dto.getDuracionHoras());
		curso.setTipoAula(dto.getTipoAula());
		Curso guardado = cursoRepo.save(curso);

		if (cambioFormato || cambioSesiones) {
			for (Seccion seccion : seccionRepo.findByCursoId(id)) {
				horarioService.sincronizarSeccion(seccion, cambioFormato);
			}
		}
		return aDto(guardado);
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