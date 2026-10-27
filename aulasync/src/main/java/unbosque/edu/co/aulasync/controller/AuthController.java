package unbosque.edu.co.aulasync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.LoginRequestDto;
import unbosque.edu.co.aulasync.dto.LoginResponseDto;
import unbosque.edu.co.aulasync.dto.UsuarioResponseDto;
import unbosque.edu.co.aulasync.exception.AccesoDenegadoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.NoAutenticadoException;
import unbosque.edu.co.aulasync.seguridad.AutenticacionInterceptor;
import unbosque.edu.co.aulasync.seguridad.SesionUsuario;
import unbosque.edu.co.aulasync.service.AuthService;

/**
 * Inicio de sesión y datos del usuario autenticado, bajo {@code /api/auth}.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	/**
	 * {@code POST /api/auth/login} (público). Devuelve el token que el front debe
	 * enviar como {@code Authorization: Bearer <token>}.
	 */
	@PostMapping("/login")
	public LoginResponseDto login(@RequestBody LoginRequestDto dto)
			throws NoAutenticadoException, AccesoDenegadoException {
		return authService.login(dto);
	}

	/** {@code GET /api/auth/yo}: datos y rol del usuario autenticado. */
	@GetMapping("/yo")
	public UsuarioResponseDto yo(@RequestAttribute(AutenticacionInterceptor.ATRIBUTO_SESION) SesionUsuario sesion)
			throws EntidadNoEncontradaException {
		return authService.perfil(sesion.getId());
	}
}
