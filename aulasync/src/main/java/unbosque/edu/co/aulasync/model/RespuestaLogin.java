package unbosque.edu.co.aulasync.model;

import unbosque.edu.co.aulasync.dto.UsuarioDTO;

/**
 * Objeto de valor con la respuesta de un inicio de sesión exitoso.
 *
 * <p>
 * No corresponde a ninguna tabla, por eso vive en {@code model}. El front
 * guarda el {@code token} y lo envía en cada petición en la cabecera
 * {@code Authorization: Bearer <token>}.
 * </p>
 *
 * @version 1.0
 */
public class RespuestaLogin {

	private final String token;
	private final String tipo;
	private final long expiraEnSegundos;
	private final UsuarioDTO usuario;

	/**
	 * Crea la respuesta de login.
	 *
	 * @param token            token JWT firmado.
	 * @param tipo             tipo de token (siempre "Bearer").
	 * @param expiraEnSegundos segundos de validez del token.
	 * @param usuario          datos del usuario autenticado, sin contraseña.
	 */
	public RespuestaLogin(String token, String tipo, long expiraEnSegundos, UsuarioDTO usuario) {
		this.token = token;
		this.tipo = tipo;
		this.expiraEnSegundos = expiraEnSegundos;
		this.usuario = usuario;
	}

	/**
	 * @return token JWT firmado.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @return tipo de token.
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @return segundos de validez del token.
	 */
	public long getExpiraEnSegundos() {
		return expiraEnSegundos;
	}

	/**
	 * @return datos del usuario autenticado.
	 */
	public UsuarioDTO getUsuario() {
		return usuario;
	}
}
