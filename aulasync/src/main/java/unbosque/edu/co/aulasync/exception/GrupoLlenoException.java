package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando una inscripción haría que un grupo supere el máximo de
 * estudiantes permitido.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class GrupoLlenoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el detalle del cupo.
	 *
	 * @param grupo    descripción del grupo (por ejemplo "ISIS-1101 G1").
	 * @param maximo   máximo de inscritos permitido.
	 * @param cantidad cantidad de inscritos que tendría.
	 */
	public GrupoLlenoException(String grupo, int maximo, int cantidad) {
		super("El grupo " + grupo + " admite máximo " + maximo + " inscritos (quedaría con " + cantidad + ").");
	}
}
