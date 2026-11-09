package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta eliminar un registro del que dependen grupos
 * (por ejemplo un curso o un docente que dicta grupos).
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class EntidadConGruposException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando qué registro tiene grupos.
	 *
	 * @param descripcion descripción del registro (por ejemplo "el curso
	 *                    ISIS-1101").
	 */
	public EntidadConGruposException(String descripcion) {
		super("No se puede eliminar " + descripcion + " porque tiene grupos. Elimina o reasigna sus grupos primero.");
	}
}
