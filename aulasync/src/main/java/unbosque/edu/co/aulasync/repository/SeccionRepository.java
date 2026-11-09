package unbosque.edu.co.aulasync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import unbosque.edu.co.aulasync.model.Seccion;

public interface SeccionRepository extends JpaRepository<Seccion, Long> {

	/**
	 * Indica si existe alguna sección asociada al curso dado.
	 *
	 * @param cursoId identificador del curso
	 * @return {@code true} si el curso tiene al menos una sección
	 */
	boolean existsByCursoId(Long cursoId);
}