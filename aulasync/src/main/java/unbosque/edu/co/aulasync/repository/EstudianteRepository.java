package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Estudiante;

/**
 * Acceso a datos de la tabla {@code estudiante}.
 *
 * @version 1.0
 */
@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

	/**
	 * Indica si ya existe un estudiante con ese código, sin distinguir
	 * mayúsculas.
	 *
	 * @param codigo código a verificar.
	 * @return {@code true} si el código ya está registrado.
	 */
	boolean existsByCodigoIgnoreCase(String codigo);

	/**
	 * Indica si el código pertenece a un estudiante distinto del indicado.
	 *
	 * @param codigo código a verificar.
	 * @param id     id del estudiante que se está actualizando.
	 * @return {@code true} si otro estudiante ya tiene ese código.
	 */
	boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

	/**
	 * Lista todos los estudiantes en orden alfabético.
	 *
	 * @return estudiantes ordenados por nombre.
	 */
	List<Estudiante> findAllByOrderByNombreAsc();
}
