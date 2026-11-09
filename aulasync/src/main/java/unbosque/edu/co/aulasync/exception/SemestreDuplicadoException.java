package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta registrar un número de semestre que ya existe.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class SemestreDuplicadoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el número repetido.
	 *
	 * @param numero número de semestre que ya existe.
	 */
	public SemestreDuplicadoException(int numero) {
		super("Ya existe el semestre " + numero + ".");
	}
}
