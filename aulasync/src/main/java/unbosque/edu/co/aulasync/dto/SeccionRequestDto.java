package unbosque.edu.co.aulasync.dto;

/**
 * Datos de una sección: qué docente dicta qué curso a qué grupo. Cambiar
 * {@code docenteId} es la forma de editar profesores respecto a materias.
 */
public record SeccionRequestDto(Integer numero, Long cursoId, Long docenteId, Long grupoId) {
}
