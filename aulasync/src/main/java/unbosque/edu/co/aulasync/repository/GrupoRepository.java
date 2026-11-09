package unbosque.edu.co.aulasync.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Grupo;

/**
 * Acceso a datos de la tabla {@code grupo} y de sus inscripciones.
 *
 * @version 1.1
 */
@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

	/**
	 * Indica si un curso ya tiene un grupo con ese número.
	 *
	 * @param cursoId id del curso.
	 * @param numero  número a verificar.
	 * @return {@code true} si el número ya está en uso en ese curso.
	 */
	boolean existsByCursoIdAndNumero(Long cursoId, int numero);

	/**
	 * Indica si el número pertenece, en el mismo curso, a un grupo distinto del
	 * indicado.
	 *
	 * @param cursoId id del curso.
	 * @param numero  número a verificar.
	 * @param id      id del grupo que se está actualizando.
	 * @return {@code true} si otro grupo del curso ya tiene ese número.
	 */
	boolean existsByCursoIdAndNumeroAndIdNot(Long cursoId, int numero, Long id);

	/**
	 * Indica si un curso tiene al menos un grupo.
	 *
	 * @param cursoId id del curso.
	 * @return {@code true} si el curso tiene grupos.
	 */
	boolean existsByCursoId(Long cursoId);

	/**
	 * Indica si un docente dicta al menos un grupo.
	 *
	 * @param docenteId id del docente.
	 * @return {@code true} si el docente tiene grupos.
	 */
	boolean existsByDocenteId(Long docenteId);

	/**
	 * Lista los grupos que dicta un docente.
	 *
	 * @param docenteId id del docente.
	 * @return grupos del docente.
	 */
	List<Grupo> findByDocenteId(Long docenteId);

	/**
	 * Lista todos los grupos ordenados por semestre, nombre del curso y número.
	 *
	 * @return grupos ordenados.
	 */
	List<Grupo> findAllByOrderByCursoSemestreNumeroAscCursoNombreAscNumeroAsc();

	/**
	 * Busca otro grupo del mismo curso en el que ya esté inscrito el
	 * estudiante.
	 *
	 * @param cursoId      id del curso.
	 * @param estudianteId id del estudiante.
	 * @param grupoId      id del grupo que se está editando (se excluye).
	 * @return el otro grupo, o vacío si no está en ninguno.
	 */
	Optional<Grupo> findFirstByCursoIdAndEstudiantesIdAndIdNot(Long cursoId, Long estudianteId, Long grupoId);

	/**
	 * Lista los grupos en los que está inscrito un estudiante.
	 *
	 * @param estudianteId id del estudiante.
	 * @return grupos del estudiante.
	 */
	List<Grupo> findByEstudiantesId(Long estudianteId);
}
