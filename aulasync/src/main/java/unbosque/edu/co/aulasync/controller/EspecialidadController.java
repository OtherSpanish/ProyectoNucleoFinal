package unbosque.edu.co.aulasync.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.EspecialidadDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.EspecialidadEnUsoException;
import unbosque.edu.co.aulasync.exception.NombreDuplicadoException;
import unbosque.edu.co.aulasync.service.EspecialidadService;

/**
 * Gestión de especialidades.
 *
 * <ul>
 * <li>Consultar: {@code ADMIN} y {@code COMITE}.</li>
 * <li>Crear, actualizar y eliminar: solo {@code ADMIN}.</li>
 * </ul>
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

	private final EspecialidadService especialidadService;

	/**
	 * Crea el controlador.
	 *
	 * @param especialidadService servicio de especialidades.
	 */
	public EspecialidadController(EspecialidadService especialidadService) {
		this.especialidadService = especialidadService;
	}

	/**
	 * Lista las especialidades en orden alfabético.
	 *
	 * @return 200 con la lista.
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<List<EspecialidadDTO>> listar() {
		return ResponseEntity.ok(especialidadService.listar());
	}

	/**
	 * Trae una especialidad por id.
	 *
	 * @param id identificador.
	 * @return 200 con la especialidad.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<EspecialidadDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(especialidadService.buscarPorId(id));
	}

	/**
	 * Crea una especialidad.
	 *
	 * @param dto datos de la especialidad.
	 * @return 201 con la especialidad creada.
	 * @throws CampoObligatorioException si falta el nombre (400).
	 * @throws NombreDuplicadoException  si el nombre ya existe (409).
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EspecialidadDTO> crear(@RequestBody EspecialidadDTO dto)
			throws CampoObligatorioException, NombreDuplicadoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(especialidadService.crear(dto));
	}

	/**
	 * Actualiza una especialidad.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return 200 con la especialidad actualizada.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws CampoObligatorioException    si falta el nombre (400).
	 * @throws NombreDuplicadoException     si el nombre es de otra (409).
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EspecialidadDTO> actualizar(@PathVariable Long id, @RequestBody EspecialidadDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, NombreDuplicadoException {
		return ResponseEntity.ok(especialidadService.actualizar(id, dto));
	}

	/**
	 * Elimina una especialidad.
	 *
	 * @param id identificador.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws EspecialidadEnUsoException   si algún docente o curso la usa (409).
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, EspecialidadEnUsoException {
		especialidadService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
