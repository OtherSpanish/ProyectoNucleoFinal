package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta registrar un código que ya usa otro registro de
 * la misma entidad (por ejemplo el código de un estudiante o de un curso).
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class CodigoDuplicadoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando la entidad y el código repetido.
	 *
	 * @param entidad nombre legible de la entidad (por ejemplo "Estudiante").
	 * @param codigo  código que ya está en uso.
	 */
	public CodigoDuplicadoException(String entidad, String codigo) {
		super("Ya existe un registro de " + entidad + " con el código '" + codigo + "'.");
	}
}
