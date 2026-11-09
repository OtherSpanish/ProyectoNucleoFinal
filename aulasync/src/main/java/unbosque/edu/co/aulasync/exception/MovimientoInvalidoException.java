package unbosque.edu.co.aulasync.exception;

import java.util.List;

/**
 * Se lanza cuando el comité intenta mover una sesión a un día, hora o aula que
 * incumple alguna regla del horario.
 *
 * <p>
 * El mensaje incluye todos los motivos separados por punto y coma. Solo la
 * lanza {@code LanzadorException}. Como hereda de {@link ConflictoException},
 * se responde con HTTP 409.
 * </p>
 *
 * @version 1.0
 */
public class MovimientoInvalidoException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crea la excepción con todos los motivos.
	 *
	 * @param mensajes explicación de cada regla incumplida.
	 */
	public MovimientoInvalidoException(List<String> mensajes) {
		super("El movimiento no es válido: " + String.join("; ", mensajes));
	}
}
