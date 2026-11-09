package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta vincular a un docente un usuario que no tiene
 * rol {@code DOCENTE}.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ValidacionException}, se responde con HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public class UsuarioNoEsDocenteException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el usuario.
	 *
	 * @param correo correo del usuario.
	 */
	public UsuarioNoEsDocenteException(String correo) {
		super("El usuario " + correo + " no tiene rol DOCENTE.");
	}
}
