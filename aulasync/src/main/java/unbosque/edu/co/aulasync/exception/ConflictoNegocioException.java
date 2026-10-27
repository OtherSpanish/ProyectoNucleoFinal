package unbosque.edu.co.aulasync.exception;

/**
 * Operación que choca con el estado actual de los datos: correo repetido,
 * entidad con dependencias, último administrador, etc. (respuesta 409).
 */
public class ConflictoNegocioException extends ConflictoException {

	private static final long serialVersionUID = 1L;

	public ConflictoNegocioException(String mensaje) {
		super(mensaje);
	}
}
