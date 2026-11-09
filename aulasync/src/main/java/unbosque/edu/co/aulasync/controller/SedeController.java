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

import unbosque.edu.co.aulasync.dto.FranjaSedeDTO;
import unbosque.edu.co.aulasync.dto.SedeDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.FranjaInvalidaException;
import unbosque.edu.co.aulasync.exception.FranjasTraslapadasException;
import unbosque.edu.co.aulasync.exception.NombreDuplicadoException;
import unbosque.edu.co.aulasync.exception.SedeEnUsoException;
import unbosque.edu.co.aulasync.service.SedeService;

/**
 * Gestión de sedes y de sus franjas de clase.
 *
 * <ul>
 * <li>Consultar: {@code ADMIN} y {@code COMITE}.</li>
 * <li>Crear, actualizar, cambiar franjas y eliminar: solo {@code ADMIN}.</li>
 * </ul>
 *
 * @version 1.1
 */
@RestController
@RequestMapping("/api/sedes")
public class SedeController {

	private final SedeService sedeService;

	/**
	 * Crea el controlador.
	 *
	 * @param sedeService servicio de sedes.
	 */
	public SedeController(SedeService sedeService) {
		this.sedeService = sedeService;
	}

	/**
	 * Lista las sedes con sus franjas.
	 *
	 * @return 200 con la lista.
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<List<SedeDTO>> listar() {
		return ResponseEntity.ok(sedeService.listar());
	}

	/**
	 * Trae una sede por id.
	 *
	 * @param id identificador.
	 * @return 200 con la sede.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<SedeDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(sedeService.buscarPorId(id));
	}

	/**
	 * Crea una sede sin franjas.
	 *
	 * @param dto nombre y regla de primer semestre.
	 * @return 201 con la sede creada.
	 * @throws CampoObligatorioException si falta un dato (400).
	 * @throws DatoFueraDeRangoException si los días no están entre 0 y 6 (400).
	 * @throws NombreDuplicadoException  si el nombre ya existe (409).
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SedeDTO> crear(@RequestBody SedeDTO dto)
			throws CampoObligatorioException, DatoFueraDeRangoException, NombreDuplicadoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(sedeService.crear(dto));
	}

	/**
	 * Actualiza nombre y regla de primer semestre.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return 200 con la sede actualizada.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws DatoFueraDeRangoException    si los días no están entre 0 y 6
	 *                                      (400).
	 * @throws NombreDuplicadoException     si el nombre es de otra sede (409).
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SedeDTO> actualizar(@PathVariable Long id, @RequestBody SedeDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, DatoFueraDeRangoException,
			NombreDuplicadoException {
		return ResponseEntity.ok(sedeService.actualizar(id, dto));
	}

	/**
	 * Reemplaza todas las franjas de clase de una sede.
	 *
	 * @param id      identificador de la sede.
	 * @param franjas lista de franjas.
	 * @return 200 con la sede y sus franjas nuevas.
	 * @throws EntidadNoEncontradaException si la sede no existe (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws FranjaInvalidaException      si una franja es inválida (400).
	 * @throws FranjasTraslapadasException  si dos franjas se cruzan (400).
	 */
	@PutMapping("/{id}/franjas")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SedeDTO> reemplazarFranjas(@PathVariable Long id, @RequestBody List<FranjaSedeDTO> franjas)
			throws EntidadNoEncontradaException, CampoObligatorioException, FranjaInvalidaException,
			FranjasTraslapadasException {
		return ResponseEntity.ok(sedeService.reemplazarFranjas(id, franjas));
	}

	/**
	 * Elimina una sede sin aulas, junto con sus franjas.
	 *
	 * @param id identificador.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws SedeEnUsoException           si la sede tiene aulas (409).
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, SedeEnUsoException {
		sedeService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
