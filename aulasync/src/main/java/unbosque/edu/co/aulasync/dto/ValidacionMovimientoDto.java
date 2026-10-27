package unbosque.edu.co.aulasync.dto;

import java.util.List;

/**
 * Resultado de comprobar un movimiento sin aplicarlo. {@code codigos} usa los
 * mismos nombres que el motor (CHOQUE_DOCENTE, SIN_AULA...) más FRANJA_INVALIDA,
 * AULA_INCOMPATIBLE y CHOQUE_AULA. {@code aulaId} y {@code aula} son el aula que
 * se asignaría.
 */
public record ValidacionMovimientoDto(boolean valido, List<String> codigos, List<String> motivos, Long aulaId,
		String aula) {
}
