package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta quitar el rol DOCENTE o eliminar un usuario que
 * está vinculado a un docente.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class UsuarioConDocenteException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el usuario.
	 *
	 * @param correo correo del usuario.
	 */
	public UsuarioConDocenteException(String correo) {
		super("El usuario " + correo
				+ " está vinculado a un docente. Elimina primero el docente para cambiarle el rol o borrarlo.");
	}
}
