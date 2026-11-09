package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta eliminar una especialidad que está asignada a
 * docentes o cursos.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class EspecialidadEnUsoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando la especialidad.
	 *
	 * @param nombre nombre de la especialidad.
	 */
	public EspecialidadEnUsoException(String nombre) {
		super("No se puede eliminar la especialidad '" + nombre
				+ "' porque está asignada a docentes o cursos. Quítala de ellos primero.");
	}
}
