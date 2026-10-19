package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta eliminar un curso que todavía tiene secciones
 * asociadas.
 *
 * <p>
 * Solo la lanza {@link LanzadorException#verificarCursoSinSecciones(boolean)}.
 * </p>
 *
 * @version 1.0
 */
public class CursoConSeccionesException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/** Crea la excepción con el mensaje estándar del conflicto. */
	public CursoConSeccionesException() {
		super("No se puede eliminar el curso porque tiene secciones asociadas. Elimina primero las secciones.");
	}
}