package unbosque.edu.co.aulasync.exception;

/**
 * Clase base de las excepciones por operaciones válidas que chocan con el
 * estado actual de los datos (por ejemplo, un correo repetido o eliminar un
 * registro del que otros dependen).
 *
 * <p>
 * No se lanza directamente: cada conflicto tiene su propia subclase. Todas se
 * lanzan y se manejan únicamente en {@link LanzadorException}, que las
 * traduce en una respuesta HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public abstract class ConflictoException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el mensaje que verá el usuario.
	 *
	 * @param mensaje descripción del conflicto.
	 */
	protected ConflictoException(String mensaje) {
		super(mensaje);
	}
}
