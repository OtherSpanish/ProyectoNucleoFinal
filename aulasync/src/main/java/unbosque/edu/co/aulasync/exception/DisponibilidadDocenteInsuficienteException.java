package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando la disponibilidad registrada de un docente no alcanza para
 * dictar un grupo: le faltan horas en total o no tiene un bloque seguido tan
 * largo como una sesión del curso.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ValidacionException}, se responde con HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public class DisponibilidadDocenteInsuficienteException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el detalle del problema.
	 *
	 * @param docente nombre del docente.
	 * @param detalle explicación (por ejemplo "necesita 12 horas y tiene 8
	 *                disponibles").
	 */
	public DisponibilidadDocenteInsuficienteException(String docente, String detalle) {
		super("La disponibilidad del docente " + docente + " no alcanza: " + detalle + ".");
	}
}
