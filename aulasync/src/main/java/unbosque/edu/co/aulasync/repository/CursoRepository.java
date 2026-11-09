package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Curso;

/**
 * Acceso a datos de la tabla {@code curso}.
 *
 * @version 1.0
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

	/**
	 * Indica si ya existe un curso con ese código, sin distinguir mayúsculas.
	 *
	 * @param codigo código a verificar.
	 * @return {@code true} si el código ya está registrado.
	 */
	boolean existsByCodigoIgnoreCase(String codigo);

	/**
	 * Indica si el código pertenece a un curso distinto del indicado.
	 *
	 * @param codigo código a verificar.
	 * @param id     id del curso que se está actualizando.
	 * @return {@code true} si otro curso ya tiene ese código.
	 */
	boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

	/**
	 * Indica si un semestre tiene al menos un curso.
	 *
	 * @param semestreId id del semestre.
	 * @return {@code true} si el semestre tiene cursos.
	 */
	boolean existsBySemestreId(Long semestreId);

	/**
	 * Indica si una especialidad es exigida por al menos un curso.
	 *
	 * @param especialidadId id de la especialidad.
	 * @return {@code true} si algún curso la exige.
	 */
	boolean existsByEspecialidadId(Long especialidadId);

	/**
	 * Lista todos los cursos ordenados por número de semestre y por nombre.
	 *
	 * @return cursos ordenados.
	 */
	List<Curso> findAllByOrderBySemestreNumeroAscNombreAsc();
}
