package unbosque.edu.co.aulasync.exception;

import java.util.List;

/**
 * Se lanza cuando una sesión no puede ubicarse en la franja o el aula
 * solicitadas al arrastrarla en el calendario (respuesta 409). El mensaje
 * incluye todos los motivos del rechazo.
 */
public class MovimientoInvalidoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	public MovimientoInvalidoException(List<String> motivos) {
		super("No se puede ubicar la sesión ahí. " + String.join(" ", motivos));
	}
}
