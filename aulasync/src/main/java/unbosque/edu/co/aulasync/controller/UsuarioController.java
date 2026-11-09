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

import unbosque.edu.co.aulasync.dto.UsuarioDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CorreoDuplicadoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.PasswordInvalidaException;
import unbosque.edu.co.aulasync.exception.UltimoAdminException;
import unbosque.edu.co.aulasync.exception.UsuarioConDocenteException;
import unbosque.edu.co.aulasync.model.Rol;
import unbosque.edu.co.aulasync.service.UsuarioService;

/**
 * Gestión de usuarios del sistema. Todos los endpoints son solo para el rol
 * {@code ADMIN}; cualquier otro rol recibe 403.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

	private final UsuarioService usuarioService;

	/**
	 * Crea el controlador.
	 *
	 * @param usuarioService servicio de usuarios.
	 */
	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	/**
	 * Lista los usuarios ordenados por nombre.
	 *
	 * @param rol filtro opcional por rol ({@code ADMIN}, {@code COMITE},
	 *            {@code DOCENTE}).
	 * @return 200 con la lista de usuarios.
	 */
	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> listar(@RequestParam(required = false) Rol rol) {
		return ResponseEntity.ok(usuarioService.listar(rol));
	}

	/**
	 * Trae un usuario por id.
	 *
	 * @param id identificador del usuario.
	 * @return 200 con el usuario.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(usuarioService.buscarPorId(id));
	}

	/**
	 * Crea un usuario.
	 *
	 * @param dto datos del usuario, con contraseña.
	 * @return 201 con el usuario creado.
	 * @throws CampoObligatorioException si falta un campo (400).
	 * @throws PasswordInvalidaException si la contraseña es débil (400).
	 * @throws CorreoDuplicadoException  si el correo ya existe (409).
	 */
	@PostMapping
	public ResponseEntity<UsuarioDTO> crear(@RequestBody UsuarioDTO dto)
			throws CampoObligatorioException, PasswordInvalidaException, CorreoDuplicadoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(dto));
	}

	/**
	 * Actualiza un usuario.
	 *
	 * @param id  identificador del usuario.
	 * @param dto datos nuevos; {@code password} vacío conserva la actual.
	 * @return 200 con el usuario actualizado.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws CampoObligatorioException    si falta un campo (400).
	 * @throws PasswordInvalidaException    si la contraseña es débil (400).
	 * @throws CorreoDuplicadoException     si el correo es de otro usuario (409).
	 * @throws UltimoAdminException         si se quita el rol al último admin
	 *                                      (409).
	 * @throws UsuarioConDocenteException   si se quita el rol DOCENTE a un
	 *                                      usuario vinculado a un docente (409).
	 */
	@PutMapping("/{id}")
	public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, PasswordInvalidaException,
			CorreoDuplicadoException, UltimoAdminException, UsuarioConDocenteException {
		return ResponseEntity.ok(usuarioService.actualizar(id, dto));
	}

	/**
	 * Elimina un usuario.
	 *
	 * @param id identificador del usuario.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 * @throws UltimoAdminException         si es el último admin (409).
	 * @throws UsuarioConDocenteException   si está vinculado a un docente (409).
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, UltimoAdminException, UsuarioConDocenteException {
		usuarioService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
