package unbosque.edu.co.aulasync.dto;

import unbosque.edu.co.aulasync.motor.TipoAula;

/** Aula registrada. */
public record AulaResponseDto(Long id, String nombre, int capacidad, TipoAula tipo) {
}
