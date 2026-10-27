package unbosque.edu.co.aulasync.dto;

import java.time.DayOfWeek;

/**
 * Sesión del horario guardado, lista para pintarse en el calendario. Si
 * {@code dia} es nulo la sesión está sin ubicar y {@code causa} dice por qué.
 */
public record SesionHorarioDto(Long id, Long seccionId, String curso, Long docenteId, String docente, Long grupoId,
		String grupo, DayOfWeek dia, Integer horaInicio, int duracion, Long aulaId, String aula, String causa) {
}
