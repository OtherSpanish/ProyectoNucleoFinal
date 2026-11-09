package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando una operación dejaría el sistema sin ningún administrador
 * (eliminar al último admin o quitarle ese rol).
 *
 * <p>
 * Solo la lanza {@link LanzadorException#verificarNoEsUltimoAdmin(boolean)}.
 * Como hereda de {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class UltimoAdminException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el mensaje estándar.
	 */
	public UltimoAdminException() {
		super("No se puede eliminar ni quitar el rol al último administrador del sistema.");
	}
}
