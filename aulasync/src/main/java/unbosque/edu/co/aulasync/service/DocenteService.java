package unbosque.edu.co.aulasync.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.DocenteRequestDto;
import unbosque.edu.co.aulasync.dto.DocenteResponseDto;
import unbosque.edu.co.aulasync.dto.FranjaDocenteDto;
import unbosque.edu.co.aulasync.exception.ConflictoNegocioException;
import unbosque.edu.co.aulasync.exception.DatoInvalidoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.Docente;
import unbosque.edu.co.aulasync.model.FranjaDocente;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.SeccionRepository;

/**
 * CRUD de docentes y de su disponibilidad. La disponibilidad y el tope de horas
 * nuevos se aplican a los próximos movimientos y regeneraciones del horario.
 */
@Service
@Transactional(readOnly = true)
public class DocenteService {

	private static final Set<String> DEDICACIONES = Set.of("TIEMPO_COMPLETO", "TRES_CUARTOS", "MEDIO_TIEMPO",
			"UN_CUARTO");

	private final DocenteRepository docenteRepo;
	private final SeccionRepository seccionRepo;

	public DocenteService(DocenteRepository docenteRepo, SeccionRepository seccionRepo) {
		this.docenteRepo = docenteRepo;
		this.seccionRepo = seccionRepo;
	}

	public List<DocenteResponseDto> listar() {
		return docenteRepo.findAll().stream().map(this::aDto).toList();
	}

	public DocenteResponseDto obtener(Long id) throws EntidadNoEncontradaException {
		return aDto(buscar(id));
	}

	@Transactional(rollbackFor = Exception.class)
	public DocenteResponseDto crear(DocenteRequestDto dto) throws DatoInvalidoException {
		validar(dto);
		Docente docente = new Docente();
		aplicar(docente, dto);
		return aDto(docenteRepo.save(docente));
	}

	@Transactional(rollbackFor = Exception.class)
	public DocenteResponseDto actualizar(Long id, DocenteRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException {
		Docente docente = buscar(id);
		validar(dto);
		aplicar(docente, dto);
		return aDto(docenteRepo.save(docente));
	}

	@Transactional(rollbackFor = Exception.class)
	public void eliminar(Long id) throws EntidadNoEncontradaException, ConflictoNegocioException {
		Docente docente = buscar(id);
		LanzadorException.verificarSinConflicto(seccionRepo.existsByDocenteId(id),
				"No se puede eliminar el docente porque tiene secciones asignadas. Reasígnalas primero.");
		docenteRepo.delete(docente);
	}

	private Docente buscar(Long id) throws EntidadNoEncontradaException {
		Docente docente = docenteRepo.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(docente, "Docente", id);
		return docente;
	}

	private void validar(DocenteRequestDto dto) throws DatoInvalidoException {
		LanzadorException.verificar(dto != null, "El cuerpo de la petición es obligatorio.");
		LanzadorException.verificar(dto.nombre() != null && !dto.nombre().isBlank(),
				"El nombre del docente es obligatorio.");
		LanzadorException.verificar(dto.dedicacion() != null && DEDICACIONES.contains(dto.dedicacion()),
				"La dedicación debe ser TIEMPO_COMPLETO, TRES_CUARTOS, MEDIO_TIEMPO o UN_CUARTO.");
		if (dto.franjas() != null) {
			for (FranjaDocenteDto f : dto.franjas()) {
				LanzadorException.verificar(
						f != null && f.dia() != null && f.horaInicio() != null && f.horaFin() != null
								&& f.horaInicio() >= 0 && f.horaInicio() < f.horaFin() && f.horaFin() <= 24,
						"Cada franja necesita día, horaInicio y horaFin válidos (0 a 24, con inicio menor que fin).");
			}
		}
	}

	private void aplicar(Docente docente, DocenteRequestDto dto) {
		docente.setNombre(dto.nombre().trim());
		docente.setDedicacion(dto.dedicacion());
		if (dto.franjas() != null) {
			docente.getFranjas().clear();
			for (FranjaDocenteDto f : dto.franjas()) {
				FranjaDocente franja = new FranjaDocente();
				franja.setDocente(docente);
				franja.setDia(f.dia());
				franja.setHoraInicio(f.horaInicio());
				franja.setHoraFin(f.horaFin());
				docente.getFranjas().add(franja);
			}
		}
	}

	private DocenteResponseDto aDto(Docente d) {
		List<FranjaDocenteDto> franjas = d.getFranjas().stream()
				.map(f -> new FranjaDocenteDto(f.getDia(), f.getHoraInicio(), f.getHoraFin())).toList();
		return new DocenteResponseDto(d.getId(), d.getNombre(), d.getDedicacion(), franjas);
	}
}
