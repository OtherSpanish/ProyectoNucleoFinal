package unbosque.edu.co.aulasync.exception;

/**
 * Clase base de las excepciones por datos inválidos (campo obligatorio vacío,
 * valor fuera de rango, etc.).
 *
 * <p>
 * No se lanza directamente: cada regla incumplida tiene su propia subclase, y
 * todas se lanzan únicamente desde {@link LanzadorException}. El
 * {@link GlobalExceptionHandler} las traduce en una respuesta HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public abstract class ValidacionException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el mensaje que verá el usuario.
	 *
	 * @param mensaje descripción de la regla incumplida.
	 */
	protected ValidacionException(String mensaje) {
		super(mensaje);
	}
}