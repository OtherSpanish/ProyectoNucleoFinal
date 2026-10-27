package unbosque.edu.co.aulasync.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.AulaRequestDto;
import unbosque.edu.co.aulasync.dto.AulaResponseDto;
import unbosque.edu.co.aulasync.exception.ConflictoNegocioException;
import unbosque.edu.co.aulasync.exception.DatoInvalidoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.Aula;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * CRUD de aulas. Si al editar un aula las sesiones que ya tenía asignadas
 * dejan de caber (tipo o capacidad), esas sesiones vuelven a "sin ubicar".
 */
@Service
@Transactional(readOnly = true)
public class AulaService {

	private final AulaRepository aulaRepo;
	private final SesionHorarioRepository sesionRepo;
	private final HorarioService horarioService;

	public AulaService(AulaRepository aulaRepo, SesionHorarioRepository sesionRepo, HorarioService horarioService) {
		this.aulaRepo = aulaRepo;
		this.sesionRepo = sesionRepo;
		this.horarioService = horarioService;
	}

	public List<AulaResponseDto> listar() {
		return aulaRepo.findAll().stream().map(this::aDto).toList();
	}

	public AulaResponseDto obtener(Long id) throws EntidadNoEncontradaException {
		return aDto(buscar(id));
	}

	@Transactional(rollbackFor = Exception.class)
	public AulaResponseDto crear(AulaRequestDto dto) throws DatoInvalidoException {
		validar(dto);
		Aula aula = new Aula();
		aplicar(aula, dto);
		return aDto(aulaRepo.save(aula));
	}

	@Transactional(rollbackFor = Exception.class)
	public AulaResponseDto actualizar(Long id, AulaRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException {
		Aula aula = buscar(id);
		validar(dto);
		aplicar(aula, dto);
		Aula guardada = aulaRepo.save(aula);
		horarioService.revisarAula(guardada);
		return aDto(guardada);
	}

	@Transactional(rollbackFor = Exception.class)
	public void eliminar(Long id) throws EntidadNoEncontradaException, ConflictoNegocioException {
		Aula aula = buscar(id);
		LanzadorException.verificarSinConflicto(sesionRepo.existsByAulaId(id),
				"No se puede eliminar el aula porque tiene sesiones asignadas en el horario. Muévelas primero.");
		aulaRepo.delete(aula);
	}

	private Aula buscar(Long id) throws EntidadNoEncontradaException {
		Aula aula = aulaRepo.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(aula, "Aula", id);
		return aula;
	}

	private void validar(AulaRequestDto dto) throws DatoInvalidoException {
		LanzadorException.verificar(dto != null, "El cuerpo de la petición es obligatorio.");
		LanzadorException.verificar(dto.nombre() != null && !dto.nombre().isBlank(), "El nombre del aula es obligatorio.");
		LanzadorException.verificar(dto.capacidad() != null && dto.capacidad() > 0,
				"La capacidad debe ser mayor que cero.");
		LanzadorException.verificar(dto.tipo() != null, "El tipo de aula es obligatorio.");
	}

	private void aplicar(Aula aula, AulaRequestDto dto) {
		aula.setNombre(dto.nombre().trim());
		aula.setCapacidad(dto.capacidad());
		aula.setTipo(dto.tipo());
	}

	private AulaResponseDto aDto(Aula a) {
		return new AulaResponseDto(a.getId(), a.getNombre(), a.getCapacidad(), a.getTipo());
	}
}
