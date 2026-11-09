package unbosque.edu.co.aulasync.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import unbosque.edu.co.aulasync.dto.ErrorDto;

/**
 * Manejador central de errores de la API REST.
 *
 * <p>
 * Intercepta las excepciones de negocio lanzadas desde
 * {@link LanzadorException} y las convierte en respuestas JSON uniformes
 * ({@link ErrorDto}) con el código HTTP adecuado. Como las excepciones
 * específicas heredan de tres categorías, bastan tres métodos para cubrir
 * todas, incluidas las que se agreguen más adelante.
 * </p>
 *
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Maneja datos inválidos (cualquier subclase de {@link ValidacionException}).
	 *
	 * @param ex excepción de validación.
	 * @return respuesta 400 con el mensaje de la regla incumplida.
	 */
	@ExceptionHandler(ValidacionException.class)
	public ResponseEntity<ErrorDto> manejarValidacion(ValidacionException ex) {
		return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	/**
	 * Maneja registros inexistentes.
	 *
	 * @param ex excepción de entidad no encontrada.
	 * @return respuesta 404 con el mensaje correspondiente.
	 */
	@ExceptionHandler(EntidadNoEncontradaException.class)
	public ResponseEntity<ErrorDto> manejarNoEncontrada(EntidadNoEncontradaException ex) {
		return construir(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	/**
	 * Maneja operaciones que chocan con el estado actual de los datos (cualquier
	 * subclase de {@link ConflictoException}).
	 *
	 * @param ex excepción de conflicto.
	 * @return respuesta 409 con el mensaje correspondiente.
	 */
	@ExceptionHandler(ConflictoException.class)
	public ResponseEntity<ErrorDto> manejarConflicto(ConflictoException ex) {
		return construir(HttpStatus.CONFLICT, ex.getMessage());
	}

	/**
	 * Maneja cuerpos JSON mal formados o con valores que no se pueden convertir
	 * (por ejemplo un tipo de aula inexistente).
	 *
	 * @param ex excepción de lectura del cuerpo de la petición.
	 * @return respuesta 400 con un mensaje genérico.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorDto> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
		return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es inválido o tiene valores no permitidos.");
	}

	private ResponseEntity<ErrorDto> construir(HttpStatus estado, String mensaje) {
		return ResponseEntity.status(estado).body(new ErrorDto(estado.value(), mensaje));
	}
}