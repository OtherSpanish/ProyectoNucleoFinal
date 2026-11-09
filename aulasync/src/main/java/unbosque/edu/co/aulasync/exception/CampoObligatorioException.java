package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando un campo obligatorio llega nulo o vacío.
 *
 * <p>
 * Solo la lanza
 * {@link LanzadorException#verificarCampoObligatorio(String, String)}. Como
 * hereda de {@link ValidacionException}, se responde con HTTP 400.
 * </p>
 *
 * @version 1.0
 */
public class CampoObligatorioException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción indicando el campo faltante.
	 *
	 * @param campo nombre del campo (por ejemplo "correo").
	 */
	public CampoObligatorioException(String campo) {
		super("El campo '" + campo + "' es obligatorio.");
	}
}
