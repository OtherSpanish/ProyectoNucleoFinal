package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Especialidad;

/**
 * Acceso a datos de la tabla {@code especialidad}. Solo lo usa
 * {@code EspecialidadService}.
 *
 * @version 1.0
 */
@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

	/**
	 * Indica si ya existe una especialidad con ese nombre, sin distinguir
	 * mayúsculas.
	 *
	 * @param nombre nombre a verificar.
	 * @return {@code true} si el nombre ya está registrado.
	 */
	boolean existsByNombreIgnoreCase(String nombre);

	/**
	 * Indica si el nombre pertenece a una especialidad distinta de la indicada.
	 * Se usa al actualizar, para no chocar consigo misma.
	 *
	 * @param nombre nombre a verificar.
	 * @param id     id de la especialidad que se está actualizando.
	 * @return {@code true} si otra especialidad ya tiene ese nombre.
	 */
	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

	/**
	 * Lista todas las especialidades en orden alfabético.
	 *
	 * @return especialidades ordenadas por nombre.
	 */
	List<Especialidad> findAllByOrderByNombreAsc();
}
