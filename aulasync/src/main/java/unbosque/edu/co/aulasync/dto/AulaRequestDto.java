package unbosque.edu.co.aulasync.dto;

import unbosque.edu.co.aulasync.motor.TipoAula;

/** Datos de un aula. */
public record AulaRequestDto(String nombre, Integer capacidad, TipoAula tipo) {
}
