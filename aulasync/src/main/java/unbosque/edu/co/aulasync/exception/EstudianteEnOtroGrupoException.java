package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se intenta inscribir a un estudiante en un grupo de una
 * materia en la que ya está inscrito en otro grupo.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class EstudianteEnOtroGrupoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con los datos del conflicto.
	 *
	 * @param estudiante  nombre del estudiante.
	 * @param curso       nombre del curso.
	 * @param numeroGrupo número del grupo en el que ya está inscrito.
	 */
	public EstudianteEnOtroGrupoException(String estudiante, String curso, int numeroGrupo) {
		super("El estudiante " + estudiante + " ya está inscrito en el grupo " + numeroGrupo + " de " + curso + ".");
	}
}
