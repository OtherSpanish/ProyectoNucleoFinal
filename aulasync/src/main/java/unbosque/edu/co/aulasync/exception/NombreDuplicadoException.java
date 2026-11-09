package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta registrar un nombre que ya usa otro registro de
 * la misma entidad (especialidad, aula, curso, etc.).
 *
 * <p>
 * Solo la lanza
 * {@link LanzadorException#verificarNombreDisponible(boolean, String, String)}.
 * Como hereda de {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class NombreDuplicadoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando la entidad y el nombre repetido.
	 *
	 * @param entidad nombre legible de la entidad (por ejemplo "Especialidad").
	 * @param nombre  nombre que ya está en uso.
	 */
	public NombreDuplicadoException(String entidad, String nombre) {
		super("Ya existe un registro de " + entidad + " con el nombre '" + nombre + "'.");
	}
}
