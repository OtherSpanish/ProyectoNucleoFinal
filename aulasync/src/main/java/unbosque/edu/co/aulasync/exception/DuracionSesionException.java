package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando la duración de una sesión es nula o no es una de las
 * duraciones permitidas (restricción RD8: 2 horas por defecto o 4 horas en
 * cursos específicos).
 *
 * <p>
 * Solo la lanza {@link LanzadorException#verificarDuracionSesion(Integer)}.
 * </p>
 *
 * @version 1.0
 */
public class DuracionSesionException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando las duraciones válidas.
	 *
	 * @param estandar  duración estándar en horas.
	 * @param extendida duración extendida en horas.
	 */
	public DuracionSesionException(int estandar, int extendida) {
		super("La duración de la sesión debe ser de " + estandar + " o " + extendida + " horas.");
	}
}