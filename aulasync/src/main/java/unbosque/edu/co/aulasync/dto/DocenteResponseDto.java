package unbosque.edu.co.aulasync.dto;

import java.util.List;

/** Docente con su disponibilidad. */
public record DocenteResponseDto(Long id, String nombre, String dedicacion, List<FranjaDocenteDto> franjas) {
}
