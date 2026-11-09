package unbosque.edu.co.aulasync.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import unbosque.edu.co.aulasync.dto.UsuarioDTO;
import unbosque.edu.co.aulasync.entity.Usuario;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CredencialesInvalidasException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.RespuestaLogin;
import unbosque.edu.co.aulasync.security.JwtService;

/**
 * Lógica de autenticación: inicio de sesión y consulta del usuario actual.
 *
 * <p>
 * No toca repositorios: consulta los usuarios a través de
 * {@link UsuarioService}.
 * </p>
 *
 * @version 1.0
 */
@Service
public class AuthService {

	private static final String TIPO_TOKEN = "Bearer";
	private static final long MILIS_POR_SEGUNDO = 1000L;

	private final UsuarioService usuarioService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param usuarioService  servicio de usuarios.
	 * @param passwordEncoder cifrador de contraseñas.
	 * @param jwtService      generador de tokens.
	 */
	public AuthService(UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.usuarioService = usuarioService;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	/**
	 * Valida correo y contraseña y, si son correctos, genera el token de sesión.
	 *
	 * @param dto datos de acceso; solo se usan {@code correo} y {@code password}.
	 * @return token, vigencia y datos del usuario sin contraseña.
	 * @throws CampoObligatorioException      si falta el correo o la contraseña.
	 * @throws CredencialesInvalidasException si el correo no existe o la
	 *                                        contraseña no coincide.
	 */
	public RespuestaLogin iniciarSesion(UsuarioDTO dto)
			throws CampoObligatorioException, CredencialesInvalidasException {
		LanzadorException.verificarCampoObligatorio(dto.getCorreo(), "correo");
		LanzadorException.verificarCampoObligatorio(dto.getPassword(), "password");

		Usuario usuario = usuarioService.buscarEntidadPorCorreo(dto.getCorreo());
		boolean validas = usuario != null && passwordEncoder.matches(dto.getPassword(), usuario.getPassword());
		LanzadorException.verificarCredenciales(validas);

		String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol());
		long segundos = jwtService.getDuracionMilis() / MILIS_POR_SEGUNDO;
		return new RespuestaLogin(token, TIPO_TOKEN, segundos, usuarioService.convertirADTO(usuario));
	}

	/**
	 * Devuelve los datos del usuario dueño del token.
	 *
	 * @param correo correo del usuario autenticado.
	 * @return datos del usuario sin contraseña.
	 */
	public UsuarioDTO obtenerUsuarioActual(String correo) {
		Usuario usuario = usuarioService.buscarEntidadPorCorreo(correo);
		LanzadorException.verificarUsuarioSeguridadExiste(usuario, correo);
		return usuarioService.convertirADTO(usuario);
	}
}
