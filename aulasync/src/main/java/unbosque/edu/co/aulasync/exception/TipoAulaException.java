package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando no se indica el tipo de aula que requiere un curso.
 *
 * <p>
 * Solo la lanza
 * {@link LanzadorException#verificarTipoAula(unbosque.edu.co.aulasync.motor.TipoAula)}.
 * </p>
 *
 * @version 1.0
 */
public class TipoAulaException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/** Crea la excepción con el mensaje estándar del tipo de aula obligatorio. */
	public TipoAulaException() {
		super("El tipo de aula es obligatorio.");
	}
}