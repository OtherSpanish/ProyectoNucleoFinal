package unbosque.edu.co.aulasync.dto;

/**
 * DTO de salida con la forma uniforme de todos los errores de la API.
 *
 * <p>
 * El front lee el campo {@code mensaje} para mostrarlo al usuario (por ejemplo
 * "El nombre del curso es obligatorio.").
 * </p>
 */
public class ErrorDto {

	private final int estado;
	private final String mensaje;

	/**
	 * Crea el DTO de error.
	 *
	 * @param estado  código HTTP de la respuesta
	 * @param mensaje descripción del error para el usuario
	 */
	public ErrorDto(int estado, String mensaje) {
		this.estado = estado;
		this.mensaje = mensaje;
	}

	/** @return código HTTP de la respuesta */
	public int getEstado() {
		return estado;
	}

	/** @return descripción del error */
	public String getMensaje() {
		return mensaje;
	}
}