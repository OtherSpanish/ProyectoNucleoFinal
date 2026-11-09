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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.EstudianteDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CodigoDuplicadoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.service.EstudianteService;

/**
 * Gestión de estudiantes.
 *
 * <ul>
 * <li>Consultar y buscar por prefijo: {@code ADMIN} y {@code COMITE}.</li>
 * <li>Crear, actualizar y eliminar: solo {@code ADMIN}.</li>
 * </ul>
 *
 * @version 1.1
 */
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

	private final EstudianteService estudianteService;

	/**
	 * Crea el controlador.
	 *
	 * @param estudianteService servicio de estudiantes.
	 */
	public EstudianteController(EstudianteService estudianteService) {
		this.estudianteService = estudianteService;
	}

	/**
	 * Lista los estudiantes en orden alfabético.
	 *
	 * @return 200 con la lista.
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<List<EstudianteDTO>> listar() {
		return ResponseEntity.ok(estudianteService.listar());
	}

	/**
	 * Busca estudiantes por prefijo de código o nombre (autocompletado).
	 *
	 * @param prefijo texto buscado.
	 * @return 200 con hasta 20 estudiantes en orden lexicográfico.
	 * @throws CampoObligatorioException si el prefijo está vacío (400).
	 */
	@GetMapping("/buscar")
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<List<EstudianteDTO>> buscarPorPrefijo(@RequestParam(required = false) String prefijo)
			throws CampoObligatorioException {
		return ResponseEntity.ok(estudianteService.buscarPorPrefijo(prefijo));
	}

	/**
	 * Trae un estudiante por id.
	 *
	 * @param id identificador.
	 * @return 200 con el estudiante.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<EstudianteDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(estudianteService.buscarPorId(id));
	}

	/**
	 * Crea un estudiante.
	 *
	 * @param dto código, nombre, semestreId y correo opcional.
	 * @return 201 con el estudiante creado.
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws CodigoDuplicadoException     si el código ya existe (409).
	 * @throws EntidadNoEncontradaException si el semestre no existe (404).
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EstudianteDTO> crear(@RequestBody EstudianteDTO dto)
			throws CampoObligatorioException, CodigoDuplicadoException, EntidadNoEncontradaException {
		return ResponseEntity.status(HttpStatus.CREATED).body(estudianteService.crear(dto));
	}

	/**
	 * Actualiza un estudiante.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return 200 con el estudiante actualizado.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws CodigoDuplicadoException     si el código es de otro (409).
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EstudianteDTO> actualizar(@PathVariable Long id, @RequestBody EstudianteDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, CodigoDuplicadoException {
		return ResponseEntity.ok(estudianteService.actualizar(id, dto));
	}

	/**
	 * Elimina un estudiante.
	 *
	 * @param id identificador.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) throws EntidadNoEncontradaException {
		estudianteService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
