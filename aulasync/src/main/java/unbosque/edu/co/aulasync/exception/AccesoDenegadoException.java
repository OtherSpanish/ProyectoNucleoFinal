package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando el usuario está autenticado pero su rol no tiene permiso para
 * la operación solicitada (respuesta 403).
 */
public class AccesoDenegadoException extends Exception {

	private static final long serialVersionUID = 1L;

	public AccesoDenegadoException(String mensaje) {
		super(mensaje);
	}
}
