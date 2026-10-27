package unbosque.edu.co.aulasync.dto;

import java.time.DayOfWeek;

/** Franja de disponibilidad de un docente: día y horas [horaInicio, horaFin). */
public record FranjaDocenteDto(DayOfWeek dia, Integer horaInicio, Integer horaFin) {
}
