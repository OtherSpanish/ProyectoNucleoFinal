package unbosque.edu.co.aulasync.dto;

import java.time.DayOfWeek;

/**
 * Destino de una sesión arrastrada. {@code aulaId} es opcional: si no viene, el
 * servidor elige la primera aula compatible y libre. Con los tres campos nulos
 * en {@code PATCH}, la sesión vuelve a la lista de sin ubicar.
 */
public record MoverSesionRequestDto(DayOfWeek dia, Integer horaInicio, Long aulaId) {
}
