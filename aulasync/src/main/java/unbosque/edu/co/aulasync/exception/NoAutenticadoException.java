package unbosque.edu.co.aulasync.exception;

/**
 * Se lanza cuando la petición no trae una sesión válida: credenciales
 * incorrectas, token ausente, vencido o alterado (respuesta 401).
 */
public class NoAutenticadoException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoAutenticadoException(String mensaje) {
		super(mensaje);
	}
}
