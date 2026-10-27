package unbosque.edu.co.aulasync.dto;

import java.util.List;

/** Horario guardado: sesiones ubicadas y sesiones pendientes de ubicar. */
public record HorarioDto(List<SesionHorarioDto> ubicadas, List<SesionHorarioDto> sinUbicar) {
}
