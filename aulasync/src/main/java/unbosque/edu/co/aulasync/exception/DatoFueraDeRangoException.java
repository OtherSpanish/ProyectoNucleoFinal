package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando un número está fuera del rango permitido para su campo
 * (por ejemplo días por semana, capacidad de un aula o número de semestre).
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ValidacionException}, se responde con HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public class DatoFueraDeRangoException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el campo y su rango.
	 *
	 * @param campo  nombre del campo.
	 * @param minimo valor mínimo permitido (incluido).
	 * @param maximo valor máximo permitido (incluido).
	 */
	public DatoFueraDeRangoException(String campo, int minimo, int maximo) {
		super("El campo '" + campo + "' debe estar entre " + minimo + " y " + maximo + ".");
	}
}
