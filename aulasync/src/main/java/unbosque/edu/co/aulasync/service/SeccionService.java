package unbosque.edu.co.aulasync.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.SeccionRequestDto;
import unbosque.edu.co.aulasync.dto.SeccionResponseDto;
import unbosque.edu.co.aulasync.exception.DatoInvalidoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.Curso;
import unbosque.edu.co.aulasync.model.Docente;
import unbosque.edu.co.aulasync.model.Grupo;
import unbosque.edu.co.aulasync.model.Seccion;
import unbosque.edu.co.aulasync.repository.CursoRepository;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.GrupoRepository;
import unbosque.edu.co.aulasync.repository.SeccionRepository;

/**
 * CRUD de secciones: la relación entre un curso, el docente que lo dicta y el
 * grupo que lo recibe. Cambiar el docente de una sección es la operación de
 * "editar profesores respecto a materias". Al crear, editar o eliminar una
 * sección se mantiene sincronizado el horario guardado.
 */
@Service
@Transactional(readOnly = true)
public class SeccionService {

	private final SeccionRepository seccionRepo;
	private final CursoRepository cursoRepo;
	private final DocenteRepository docenteRepo;
	private final GrupoRepository grupoRepo;
	private final HorarioService horarioService;

	public SeccionService(SeccionRepository seccionRepo, CursoRepository cursoRepo, DocenteRepository docenteRepo,
			GrupoRepository grupoRepo, HorarioService horarioService) {
		this.seccionRepo = seccionRepo;
		this.cursoRepo = cursoRepo;
		this.docenteRepo = docenteRepo;
		this.grupoRepo = grupoRepo;
		this.horarioService = horarioService;
	}

	public List<SeccionResponseDto> listar() {
		return seccionRepo.findAll().stream().map(this::aDto).toList();
	}

	public SeccionResponseDto obtener(Long id) throws EntidadNoEncontradaException {
		return aDto(buscar(id));
	}

	/** Crea la sección y sus sesiones (sin ubicar) en el horario guardado. */
	@Transactional(rollbackFor = Exception.class)
	public SeccionResponseDto crear(SeccionRequestDto dto) throws EntidadNoEncontradaException, DatoInvalidoException {
		validar(dto);
		Seccion seccion = new Seccion();
		aplicar(seccion, dto);
		Seccion guardada = seccionRepo.save(seccion);
		horarioService.sincronizarSeccion(guardada, false);
		return aDto(guardada);
	}

	/**
	 * Edita la sección. Si cambia el docente, el curso o el grupo, sus sesiones
	 * vuelven a "sin ubicar" para que el Comité las acomode de nuevo.
	 */
	@Transactional(rollbackFor = Exception.class)
	public SeccionResponseDto actualizar(Long id, SeccionRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException {
		Seccion seccion = buscar(id);
		validar(dto);
		Long cursoAntes = seccion.getCurso().getId();
		Long docenteAntes = seccion.getDocente().getId();
		Long grupoAntes = seccion.getGrupo().getId();

		aplicar(seccion, dto);
		Seccion guardada = seccionRepo.save(seccion);

		boolean cambioRelevante = !cursoAntes.equals(dto.cursoId()) || !docenteAntes.equals(dto.docenteId())
				|| !grupoAntes.equals(dto.grupoId());
		horarioService.sincronizarSeccion(guardada, cambioRelevante);
		return aDto(guardada);
	}

	/** Elimina la sección y sus sesiones del horario guardado. */
	@Transactional(rollbackFor = Exception.class)
	public void eliminar(Long id) throws EntidadNoEncontradaException {
		Seccion seccion = buscar(id);
		horarioService.eliminarSesionesDeSeccion(id);
		seccionRepo.delete(seccion);
	}

	private Seccion buscar(Long id) throws EntidadNoEncontradaException {
		Seccion seccion = seccionRepo.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(seccion, "Sección", id);
		return seccion;
	}

	private void validar(SeccionRequestDto dto) throws DatoInvalidoException {
		LanzadorException.verificar(dto != null, "El cuerpo de la petición es obligatorio.");
		LanzadorException.verificar(dto.cursoId() != null, "El curso es obligatorio.");
		LanzadorException.verificar(dto.docenteId() != null, "El docente es obligatorio.");
		LanzadorException.verificar(dto.grupoId() != null, "El grupo es obligatorio.");
		LanzadorException.verificar(dto.numero() == null || dto.numero() >= 1,
				"El número de sección debe ser 1 o mayor.");
	}

	private void aplicar(Seccion seccion, SeccionRequestDto dto) throws EntidadNoEncontradaException {
		Curso curso = cursoRepo.findById(dto.cursoId()).orElse(null);
		LanzadorException.verificarEntidadExiste(curso, "Curso", dto.cursoId());
		Docente docente = docenteRepo.findById(dto.docenteId()).orElse(null);
		LanzadorException.verificarEntidadExiste(docente, "Docente", dto.docenteId());
		Grupo grupo = grupoRepo.findById(dto.grupoId()).orElse(null);
		LanzadorException.verificarEntidadExiste(grupo, "Grupo", dto.grupoId());

		seccion.setNumero(dto.numero() == null ? 1 : dto.numero());
		seccion.setCurso(curso);
		seccion.setDocente(docente);
		seccion.setGrupo(grupo);
	}

	private SeccionResponseDto aDto(Seccion s) {
		return new SeccionResponseDto(s.getId(), s.getNumero(), s.getCurso().getId(), s.getCurso().getNombre(),
				s.getDocente().getId(), s.getDocente().getNombre(), s.getGrupo().getId(), s.getGrupo().getNombre());
	}
}
