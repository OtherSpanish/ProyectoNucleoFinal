package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta eliminar un semestre que todavía tiene cursos.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.1
 */
public class SemestreEnUsoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el semestre.
	 *
	 * @param numero número del semestre.
	 */
	public SemestreEnUsoException(int numero) {
		super("No se puede eliminar el semestre " + numero
				+ " porque tiene cursos asignados. Elimínalos o muévelos primero.");
	}
}
