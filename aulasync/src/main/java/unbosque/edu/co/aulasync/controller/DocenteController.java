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

import unbosque.edu.co.aulasync.dto.DocenteDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadConGruposException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.UsuarioNoEsDocenteException;
import unbosque.edu.co.aulasync.exception.UsuarioYaVinculadoException;
import unbosque.edu.co.aulasync.service.DocenteService;

/**
 * Gestión de docentes.
 *
 * <ul>
 * <li>Consultar y buscar por prefijo: {@code ADMIN} y {@code COMITE}.</li>
 * <li>Crear, actualizar y eliminar: solo {@code ADMIN}.</li>
 * </ul>
 *
 * @version 1.2
 */
@RestController
@RequestMapping("/api/docentes")
public class DocenteController {

	private final DocenteService docenteService;

	/**
	 * Crea el controlador.
	 *
	 * @param docenteService servicio de docentes.
	 */
	public DocenteController(DocenteService docenteService) {
		this.docenteService = docenteService;
	}

	/**
	 * Lista los docentes en orden alfabético.
	 *
	 * @param especialidadId filtro opcional por especialidad.
	 * @return 200 con la lista.
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<List<DocenteDTO>> listar(@RequestParam(required = false) Long especialidadId) {
		return ResponseEntity.ok(docenteService.listar(especialidadId));
	}

	/**
	 * Busca docentes por prefijo del nombre (autocompletado).
	 *
	 * @param prefijo texto buscado.
	 * @return 200 con hasta 20 docentes en orden lexicográfico.
	 * @throws CampoObligatorioException si el prefijo está vacío (400).
	 */
	@GetMapping("/buscar")
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<List<DocenteDTO>> buscarPorPrefijo(@RequestParam(required = false) String prefijo)
			throws CampoObligatorioException {
		return ResponseEntity.ok(docenteService.buscarPorPrefijo(prefijo));
	}

	/**
	 * Trae un docente por id.
	 *
	 * @param id identificador.
	 * @return 200 con el docente.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<DocenteDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(docenteService.buscarPorId(id));
	}

	/**
	 * Crea un docente.
	 *
	 * @param dto {@code usuario.id}, {@code dedicacion} y
	 *            {@code especialidades[].id}.
	 * @return 201 con el docente creado.
	 * @throws CampoObligatorioException    si falta un campo (400).
	 * @throws EntidadNoEncontradaException si el usuario o una especialidad no
	 *                                      existen (404).
	 * @throws UsuarioNoEsDocenteException  si el usuario no es DOCENTE (400).
	 * @throws UsuarioYaVinculadoException  si el usuario ya es de otro docente
	 *                                      (409).
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<DocenteDTO> crear(@RequestBody DocenteDTO dto) throws CampoObligatorioException,
			EntidadNoEncontradaException, UsuarioNoEsDocenteException, UsuarioYaVinculadoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(docenteService.crear(dto));
	}

	/**
	 * Actualiza un docente.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return 200 con el docente actualizado.
	 * @throws EntidadNoEncontradaException si algo no existe (404).
	 * @throws CampoObligatorioException    si falta un campo (400).
	 * @throws UsuarioNoEsDocenteException  si el usuario no es DOCENTE (400).
	 * @throws UsuarioYaVinculadoException  si el usuario ya es de otro docente
	 *                                      (409).
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<DocenteDTO> actualizar(@PathVariable Long id, @RequestBody DocenteDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, UsuarioNoEsDocenteException,
			UsuarioYaVinculadoException {
		return ResponseEntity.ok(docenteService.actualizar(id, dto));
	}

	/**
	 * Elimina un docente que no dicte grupos (su usuario se conserva).
	 *
	 * @param id identificador.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws EntidadConGruposException    si el docente dicta grupos (409).
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, EntidadConGruposException {
		docenteService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
