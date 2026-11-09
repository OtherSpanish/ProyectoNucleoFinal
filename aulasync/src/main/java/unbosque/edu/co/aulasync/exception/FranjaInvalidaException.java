package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando una franja de disponibilidad está fuera de la jornada
 * institucional, cae en domingo o tiene la hora de fin antes de la de inicio.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ValidacionException}, se responde con HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public class FranjaInvalidaException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el detalle del problema.
	 *
	 * @param detalle explicación (por ejemplo "SATURDAY 10-15: el sábado se
	 *                cierra a las 13").
	 */
	public FranjaInvalidaException(String detalle) {
		super("Franja inválida: " + detalle);
	}
}
