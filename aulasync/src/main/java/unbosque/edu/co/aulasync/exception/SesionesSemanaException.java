package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando las sesiones por semana de un curso son nulas o están fuera
 * del rango permitido (restricción RD8).
 *
 * <p>
 * Solo la lanza {@link LanzadorException#verificarSesionesSemana(Integer)}.
 * </p>
 *
 * @version 1.0
 */
public class SesionesSemanaException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el rango válido.
	 *
	 * @param minimo mínimo de sesiones por semana permitido.
	 * @param maximo máximo de sesiones por semana permitido.
	 */
	public SesionesSemanaException(int minimo, int maximo) {
		super("Las sesiones por semana deben estar entre " + minimo + " y " + maximo + ".");
	}
}