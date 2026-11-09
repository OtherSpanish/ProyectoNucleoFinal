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

import unbosque.edu.co.aulasync.dto.AulaDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.NombreDuplicadoException;
import unbosque.edu.co.aulasync.model.TipoAula;
import unbosque.edu.co.aulasync.service.AulaService;

/**
 * Gestión de aulas (espacios físicos).
 *
 * <ul>
 * <li>Consultar: {@code ADMIN} y {@code COMITE}.</li>
 * <li>Crear, actualizar y eliminar: solo {@code ADMIN}.</li>
 * </ul>
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/aulas")
public class AulaController {

	private final AulaService aulaService;

	/**
	 * Crea el controlador.
	 *
	 * @param aulaService servicio de aulas.
	 */
	public AulaController(AulaService aulaService) {
		this.aulaService = aulaService;
	}

	/**
	 * Lista las aulas ordenadas por sede y nombre.
	 *
	 * @param sedeId          filtro opcional por sede.
	 * @param tipo            filtro opcional por tipo.
	 * @param capacidadMinima filtro opcional por capacidad mínima.
	 * @param soloDisponibles si es {@code true}, excluye las no disponibles.
	 * @return 200 con la lista.
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<List<AulaDTO>> listar(@RequestParam(required = false) Long sedeId,
			@RequestParam(required = false) TipoAula tipo, @RequestParam(required = false) Integer capacidadMinima,
			@RequestParam(required = false) Boolean soloDisponibles) {
		return ResponseEntity.ok(aulaService.listar(sedeId, tipo, capacidadMinima, soloDisponibles));
	}

	/**
	 * Trae un aula por id.
	 *
	 * @param id identificador.
	 * @return 200 con el aula.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<AulaDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(aulaService.buscarPorId(id));
	}

	/**
	 * Crea un aula.
	 *
	 * @param dto datos del aula.
	 * @return 201 con el aula creada.
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws DatoFueraDeRangoException    si la capacidad está fuera de rango
	 *                                      (400).
	 * @throws EntidadNoEncontradaException si la sede no existe (404).
	 * @throws NombreDuplicadoException     si el nombre ya existe en la sede
	 *                                      (409).
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AulaDTO> crear(@RequestBody AulaDTO dto) throws CampoObligatorioException,
			DatoFueraDeRangoException, EntidadNoEncontradaException, NombreDuplicadoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(aulaService.crear(dto));
	}

	/**
	 * Actualiza un aula.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return 200 con el aula actualizada.
	 * @throws EntidadNoEncontradaException si el aula o la sede no existen
	 *                                      (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws DatoFueraDeRangoException    si la capacidad está fuera de rango
	 *                                      (400).
	 * @throws NombreDuplicadoException     si el nombre ya existe en la sede
	 *                                      (409).
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AulaDTO> actualizar(@PathVariable Long id, @RequestBody AulaDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, DatoFueraDeRangoException,
			NombreDuplicadoException {
		return ResponseEntity.ok(aulaService.actualizar(id, dto));
	}

	/**
	 * Elimina un aula.
	 *
	 * @param id identificador.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) throws EntidadNoEncontradaException {
		aulaService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
