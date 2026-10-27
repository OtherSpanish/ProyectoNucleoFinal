package unbosque.edu.co.aulasync.dto;

import java.util.List;

/**
 * Datos de un docente. {@code dedicacion}: TIEMPO_COMPLETO, TRES_CUARTOS,
 * MEDIO_TIEMPO o UN_CUARTO. En la edición, {@code franjas} nulo conserva las
 * actuales; una lista vacía deja al docente siempre disponible.
 */
public record DocenteRequestDto(String nombre, String dedicacion, List<FranjaDocenteDto> franjas) {
}
