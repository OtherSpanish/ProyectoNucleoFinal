package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando un docente intenta ver información que no le corresponde:
 * su usuario no está vinculado a ningún docente, o pide un grupo que dicta
 * otro docente.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}, y la misma clase la traduce en una
 * respuesta HTTP 403.
 * </p>
 *
 * @version 1.0
 */
public class AccesoDocenteException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el mensaje que verá el docente.
	 *
	 * @param mensaje explicación del acceso denegado.
	 */
	public AccesoDocenteException(String mensaje) {
		super(mensaje);
	}
}
