package unbosque.edu.co.aulasync.dto;

/** Sección con los nombres legibles de su curso, docente y grupo. */
public record SeccionResponseDto(Long id, int numero, Long cursoId, String curso, Long docenteId, String docente,
		Long grupoId, String grupo) {
}
