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

import unbosque.edu.co.aulasync.dto.CursoDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CodigoDuplicadoException;
import unbosque.edu.co.aulasync.exception.EntidadConGruposException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.service.CursoService;

/**
 * Gestión de cursos (asignaturas).
 *
 * <ul>
 * <li>Consultar y buscar por prefijo: cualquier usuario autenticado.</li>
 * <li>Crear, actualizar y eliminar: solo {@code COMITE}.</li>
 * </ul>
 *
 * @version 1.2
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

	private final CursoService cursoService;

	/**
	 * Crea el controlador.
	 *
	 * @param cursoService servicio de cursos.
	 */
	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	/**
	 * Lista los cursos ordenados por semestre y nombre.
	 *
	 * @param semestreId     filtro opcional por semestre.
	 * @param especialidadId filtro opcional por especialidad.
	 * @return 200 con la lista.
	 */
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<CursoDTO>> listar(@RequestParam(required = false) Long semestreId,
			@RequestParam(required = false) Long especialidadId) {
		return ResponseEntity.ok(cursoService.listar(semestreId, especialidadId));
	}

	/**
	 * Busca cursos por prefijo de código o nombre (autocompletado).
	 *
	 * @param prefijo texto buscado.
	 * @return 200 con hasta 20 cursos en orden lexicográfico.
	 * @throws CampoObligatorioException si el prefijo está vacío (400).
	 */
	@GetMapping("/buscar")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<CursoDTO>> buscarPorPrefijo(@RequestParam(required = false) String prefijo)
			throws CampoObligatorioException {
		return ResponseEntity.ok(cursoService.buscarPorPrefijo(prefijo));
	}

	/**
	 * Trae un curso por id.
	 *
	 * @param id identificador.
	 * @return 200 con el curso.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<CursoDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(cursoService.buscarPorId(id));
	}

	/**
	 * Crea un curso.
	 *
	 * @param dto datos del curso.
	 * @return 201 con el curso creado.
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws EntidadNoEncontradaException si el semestre o la especialidad no
	 *                                      existen (404).
	 * @throws CodigoDuplicadoException     si el código ya existe (409).
	 */
	@PostMapping
	@PreAuthorize("hasRole('COMITE')")
	public ResponseEntity<CursoDTO> crear(@RequestBody CursoDTO dto)
			throws CampoObligatorioException, EntidadNoEncontradaException, CodigoDuplicadoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.crear(dto));
	}

	/**
	 * Actualiza un curso.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return 200 con el curso actualizado.
	 * @throws EntidadNoEncontradaException si algo no existe (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws CodigoDuplicadoException     si el código es de otro curso (409).
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('COMITE')")
	public ResponseEntity<CursoDTO> actualizar(@PathVariable Long id, @RequestBody CursoDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, CodigoDuplicadoException {
		return ResponseEntity.ok(cursoService.actualizar(id, dto));
	}

	/**
	 * Elimina un curso sin grupos.
	 *
	 * @param id identificador.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws EntidadConGruposException    si el curso tiene grupos (409).
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('COMITE')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, EntidadConGruposException {
		cursoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
