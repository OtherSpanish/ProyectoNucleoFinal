package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando el nombre de un curso es nulo o está vacío.
 *
 * <p>
 * Solo la lanza {@link LanzadorException#verificarNombreCurso(String)}.
 * </p>
 *
 * @version 1.0
 */
public class NombreCursoException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/** Crea la excepción con el mensaje estándar del nombre obligatorio. */
	public NombreCursoException() {
		super("El nombre del curso es obligatorio.");
	}
}