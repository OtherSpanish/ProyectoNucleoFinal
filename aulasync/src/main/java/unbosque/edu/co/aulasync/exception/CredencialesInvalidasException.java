package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando el correo no existe o la contraseña no coincide al iniciar
 * sesión.
 *
 * <p>
 * El mensaje no dice cuál de los dos falló, para no revelar qué correos están
 * registrados. Solo la lanza
 * {@link LanzadorException#verificarCredenciales(boolean)}, y la misma clase
 * la traduce en una respuesta HTTP 401.
 * </p>
 *
 * @version 1.0
 */
public class CredencialesInvalidasException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el mensaje estándar.
	 */
	public CredencialesInvalidasException() {
		super("Correo o contraseña incorrectos.");
	}
}
