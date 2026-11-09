package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando dos franjas de disponibilidad del mismo docente se cruzan
 * el mismo día (por ejemplo lunes 7-10 y lunes 9-12).
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ValidacionException}, se responde con HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public class FranjasTraslapadasException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando las dos franjas que se cruzan.
	 *
	 * @param primera descripción de la primera franja (por ejemplo "MONDAY
	 *                7-10").
	 * @param segunda descripción de la segunda franja.
	 */
	public FranjasTraslapadasException(String primera, String segunda) {
		super("Las franjas " + primera + " y " + segunda + " se traslapan.");
	}
}
