package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta crear un grupo con un número que ya existe en el
 * mismo curso.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class GrupoDuplicadoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el curso y el número repetido.
	 *
	 * @param codigoCurso código del curso.
	 * @param numero      número de grupo repetido.
	 */
	public GrupoDuplicadoException(String codigoCurso, int numero) {
		super("El curso " + codigoCurso + " ya tiene un grupo " + numero + ".");
	}
}
