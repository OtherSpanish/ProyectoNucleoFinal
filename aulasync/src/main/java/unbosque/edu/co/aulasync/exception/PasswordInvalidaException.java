package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando una contraseña no cumple la política mínima: al menos
 * {@value LanzadorException#MIN_LONGITUD_PASSWORD} caracteres, con al menos
 * una letra y un número.
 *
 * <p>
 * Solo la lanza {@link LanzadorException#verificarPassword(String)}. Como
 * hereda de {@link ValidacionException}, se responde con HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public class PasswordInvalidaException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el mensaje de la política.
	 *
	 * @param minLongitud longitud mínima exigida.
	 */
	public PasswordInvalidaException(int minLongitud) {
		super("La contraseña debe tener al menos " + minLongitud + " caracteres, con al menos una letra y un número.");
	}
}
