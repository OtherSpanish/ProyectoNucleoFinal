package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta registrar o cambiar un usuario a un correo que ya
 * pertenece a otro.
 *
 * <p>
 * Solo la lanza
 * {@link LanzadorException#verificarCorreoDisponible(boolean, String)}. Como
 * hereda de {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class CorreoDuplicadoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el correo repetido.
	 *
	 * @param correo correo que ya está en uso.
	 */
	public CorreoDuplicadoException(String correo) {
		super("Ya existe un usuario con el correo " + correo + ".");
	}
}
