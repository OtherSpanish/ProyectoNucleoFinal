package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta vincular a un docente un usuario que ya está
 * vinculado a otro.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class UsuarioYaVinculadoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el usuario.
	 *
	 * @param correo correo del usuario.
	 */
	public UsuarioYaVinculadoException(String correo) {
		super("El usuario " + correo + " ya está vinculado a otro docente.");
	}
}
