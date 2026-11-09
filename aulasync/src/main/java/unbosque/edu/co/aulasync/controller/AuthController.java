package unbosque.edu.co.aulasync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.UsuarioDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CredencialesInvalidasException;
import unbosque.edu.co.aulasync.model.RespuestaLogin;
import unbosque.edu.co.aulasync.service.AuthService;

/**
 * Endpoints de autenticación.
 *
 * <ul>
 * <li>{@code POST /api/auth/login}: público.</li>
 * <li>{@code GET /api/auth/yo}: cualquier usuario autenticado.</li>
 * </ul>
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	/**
	 * Crea el controlador.
	 *
	 * @param authService servicio de autenticación.
	 */
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	/**
	 * Inicia sesión con correo y contraseña.
	 *
	 * @param dto cuerpo con {@code correo} y {@code password}.
	 * @return 200 con el token y los datos del usuario.
	 * @throws CampoObligatorioException      si falta el correo o la contraseña
	 *                                        (400).
	 * @throws CredencialesInvalidasException si los datos no coinciden (401).
	 */
	@PostMapping("/login")
	public ResponseEntity<RespuestaLogin> login(@RequestBody UsuarioDTO dto)
			throws CampoObligatorioException, CredencialesInvalidasException {
		return ResponseEntity.ok(authService.iniciarSesion(dto));
	}

	/**
	 * Devuelve los datos del usuario dueño del token.
	 *
	 * @param autenticacion autenticación que dejó el filtro JWT.
	 * @return 200 con los datos del usuario sin contraseña.
	 */
	@GetMapping("/yo")
	public ResponseEntity<UsuarioDTO> yo(Authentication autenticacion) {
		return ResponseEntity.ok(authService.obtenerUsuarioActual(autenticacion.getName()));
	}
}
