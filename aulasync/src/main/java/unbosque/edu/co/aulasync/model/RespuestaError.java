package unbosque.edu.co.aulasync.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Objeto de valor con la forma uniforme de todos los errores de la API.
 *
 * <p>
 * No corresponde a ninguna tabla, por eso vive en {@code model} y no en
 * {@code dto}. Lo construye únicamente {@code LanzadorException}. El front
 * lee el campo {@code mensaje} para mostrárselo al usuario.
 * </p>
 *
 * @version 1.0
 */
public class RespuestaError {

	private final int estado;
	private final String error;
	private final String mensaje;
	private final String fecha;

	/**
	 * Crea la respuesta de error con la fecha y hora actuales.
	 *
	 * @param estado  código HTTP (por ejemplo 404).
	 * @param error   nombre del código HTTP (por ejemplo "Not Found").
	 * @param mensaje descripción del error para el usuario.
	 */
	public RespuestaError(int estado, String error, String mensaje) {
		this.estado = estado;
		this.error = error;
		this.mensaje = mensaje;
		this.fecha = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	/**
	 * @return código HTTP de la respuesta.
	 */
	public int getEstado() {
		return estado;
	}

	/**
	 * @return nombre del código HTTP.
	 */
	public String getError() {
		return error;
	}

	/**
	 * @return descripción del error para el usuario.
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * @return fecha y hora del error en formato ISO.
	 */
	public String getFecha() {
		return fecha;
	}
}
