package unbosque.edu.co.aulasync.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.GrupoRequestDto;
import unbosque.edu.co.aulasync.dto.GrupoResponseDto;
import unbosque.edu.co.aulasync.exception.ConflictoNegocioException;
import unbosque.edu.co.aulasync.exception.DatoInvalidoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.Grupo;
import unbosque.edu.co.aulasync.repository.GrupoRepository;
import unbosque.edu.co.aulasync.repository.SeccionRepository;

/**
 * CRUD de grupos. Si al editar un grupo crece el número de estudiantes y ya no
 * cabe en el aula de alguna sesión, esa sesión vuelve a "sin ubicar".
 */
@Service
@Transactional(readOnly = true)
public class GrupoService {

	private final GrupoRepository grupoRepo;
	private final SeccionRepository seccionRepo;
	private final HorarioService horarioService;

	public GrupoService(GrupoRepository grupoRepo, SeccionRepository seccionRepo, HorarioService horarioService) {
		this.grupoRepo = grupoRepo;
		this.seccionRepo = seccionRepo;
		this.horarioService = horarioService;
	}

	public List<GrupoResponseDto> listar() {
		return grupoRepo.findAll().stream().map(this::aDto).toList();
	}

	public GrupoResponseDto obtener(Long id) throws EntidadNoEncontradaException {
		return aDto(buscar(id));
	}

	@Transactional(rollbackFor = Exception.class)
	public GrupoResponseDto crear(GrupoRequestDto dto) throws DatoInvalidoException {
		validar(dto);
		Grupo grupo = new Grupo();
		aplicar(grupo, dto);
		return aDto(grupoRepo.save(grupo));
	}

	@Transactional(rollbackFor = Exception.class)
	public GrupoResponseDto actualizar(Long id, GrupoRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException {
		Grupo grupo = buscar(id);
		validar(dto);
		aplicar(grupo, dto);
		Grupo guardado = grupoRepo.save(grupo);
		horarioService.revisarGrupo(guardado);
		return aDto(guardado);
	}

	@Transactional(rollbackFor = Exception.class)
	public void eliminar(Long id) throws EntidadNoEncontradaException, ConflictoNegocioException {
		Grupo grupo = buscar(id);
		LanzadorException.verificarSinConflicto(seccionRepo.existsByGrupoId(id),
				"No se puede eliminar el grupo porque tiene secciones asociadas. Elimina primero las secciones.");
		grupoRepo.delete(grupo);
	}

	private Grupo buscar(Long id) throws EntidadNoEncontradaException {
		Grupo grupo = grupoRepo.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(grupo, "Grupo", id);
		return grupo;
	}

	private void validar(GrupoRequestDto dto) throws DatoInvalidoException {
		LanzadorException.verificar(dto != null, "El cuerpo de la petición es obligatorio.");
		LanzadorException.verificar(dto.nombre() != null && !dto.nombre().isBlank(),
				"El nombre del grupo es obligatorio.");
		LanzadorException.verificar(dto.semestre() != null && dto.semestre() > 0, "El semestre debe ser mayor que cero.");
		LanzadorException.verificar(dto.estudiantes() != null && dto.estudiantes() > 0,
				"El número de estudiantes debe ser mayor que cero.");
	}

	private void aplicar(Grupo grupo, GrupoRequestDto dto) {
		grupo.setNombre(dto.nombre().trim());
		grupo.setSemestre(dto.semestre());
		grupo.setEstudiantes(dto.estudiantes());
	}

	private GrupoResponseDto aDto(Grupo g) {
		return new GrupoResponseDto(g.getId(), g.getNombre(), g.getSemestre(), g.getEstudiantes());
	}
}
