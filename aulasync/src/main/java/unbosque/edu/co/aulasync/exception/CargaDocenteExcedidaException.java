package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando asignar un grupo haría que el docente supere el tope de
 * horas semanales de su dedicación.
 *
 * <p>
 * Solo la lanza {@code LanzadorException}. Como hereda de
 * {@link ConflictoException}, se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class CargaDocenteExcedidaException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con el detalle de la carga.
	 *
	 * @param docente nombre del docente.
	 * @param horas   horas semanales que tendría con el grupo nuevo.
	 * @param tope    tope de horas de su dedicación.
	 */
	public CargaDocenteExcedidaException(String docente, int horas, int tope) {
		super("El docente " + docente + " quedaría con " + horas + " horas semanales y su dedicación permite " + tope
				+ ".");
	}
}
