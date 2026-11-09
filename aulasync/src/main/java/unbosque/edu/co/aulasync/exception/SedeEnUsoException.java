package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta eliminar una sede que todavía tiene aulas.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class SedeEnUsoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando la sede.
	 *
	 * @param nombre nombre de la sede.
	 */
	public SedeEnUsoException(String nombre) {
		super("No se puede eliminar la sede '" + nombre + "' porque tiene aulas. Elimina o mueve sus aulas primero.");
	}
}
