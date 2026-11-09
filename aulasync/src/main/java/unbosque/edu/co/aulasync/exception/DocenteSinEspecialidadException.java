package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se asigna a un grupo un docente que no tiene la
 * especialidad que exige el curso.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ValidacionException}, se responde con HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public class DocenteSinEspecialidadException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con los datos del problema.
	 *
	 * @param docente      nombre del docente.
	 * @param curso        nombre del curso.
	 * @param especialidad especialidad que exige el curso.
	 */
	public DocenteSinEspecialidadException(String docente, String curso, String especialidad) {
		super("El docente " + docente + " no puede dictar " + curso + ": el curso exige la especialidad '"
				+ especialidad + "'.");
	}
}
