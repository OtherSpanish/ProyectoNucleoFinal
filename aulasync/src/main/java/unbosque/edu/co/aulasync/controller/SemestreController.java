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

import unbosque.edu.co.aulasync.dto.SemestreDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.SemestreDuplicadoException;
import unbosque.edu.co.aulasync.exception.SemestreEnUsoException;
import unbosque.edu.co.aulasync.service.SemestreService;

/**
 * Gestión de semestres.
 *
 * <ul>
 * <li>Consultar: cualquier usuario autenticado (el horario se consulta por
 * semestre).</li>
 * <li>Crear, actualizar y eliminar: solo {@code ADMIN}.</li>
 * </ul>
 *
 * @version 1.1
 */
@RestController
@RequestMapping("/api/semestres")
public class SemestreController {

	private final SemestreService semestreService;

	/**
	 * Crea el controlador.
	 *
	 * @param semestreService servicio de semestres.
	 */
	public SemestreController(SemestreService semestreService) {
		this.semestreService = semestreService;
	}

	/**
	 * Lista los semestres en orden numérico.
	 *
	 * @return 200 con la lista.
	 */
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<SemestreDTO>> listar() {
		return ResponseEntity.ok(semestreService.listar());
	}

	/**
	 * Trae un semestre por id.
	 *
	 * @param id identificador.
	 * @return 200 con el semestre.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<SemestreDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(semestreService.buscarPorId(id));
	}

	/**
	 * Crea un semestre.
	 *
	 * @param dto número y nombre.
	 * @return 201 con el semestre creado.
	 * @throws CampoObligatorioException   si falta un dato (400).
	 * @throws DatoFueraDeRangoException   si el número está fuera de rango
	 *                                     (400).
	 * @throws SemestreDuplicadoException si el número ya existe (409).
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SemestreDTO> crear(@RequestBody SemestreDTO dto)
			throws CampoObligatorioException, DatoFueraDeRangoException, SemestreDuplicadoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(semestreService.crear(dto));
	}

	/**
	 * Actualiza un semestre.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return 200 con el semestre actualizado.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws DatoFueraDeRangoException    si el número está fuera de rango
	 *                                      (400).
	 * @throws SemestreDuplicadoException  si el número es de otro semestre
	 *                                      (409).
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SemestreDTO> actualizar(@PathVariable Long id, @RequestBody SemestreDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, DatoFueraDeRangoException,
			SemestreDuplicadoException {
		return ResponseEntity.ok(semestreService.actualizar(id, dto));
	}

	/**
	 * Elimina un semestre sin cursos.
	 *
	 * @param id identificador.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws SemestreEnUsoException       si tiene cursos (409).
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, SemestreEnUsoException {
		semestreService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
