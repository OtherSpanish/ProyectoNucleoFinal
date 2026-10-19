package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando se busca o se elimina un registro que no existe.
 *
 * <p>
 * Solo la lanza
 * {@link LanzadorException#verificarEntidadExiste(Object, String, Long)}. El
 * {@link GlobalExceptionHandler} la traduce en una respuesta HTTP 404.
 * </p>
 *
 * @version 1.0
 */
public class EntidadNoEncontradaException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando qué entidad y qué identificador no se
	 * encontraron.
	 *
	 * @param entidad nombre legible de la entidad (por ejemplo "Curso").
	 * @param id      identificador buscado.
	 */
	public EntidadNoEncontradaException(String entidad, Long id) {
		super(entidad + " con id " + id + " no existe.");
	}
}