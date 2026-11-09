package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Sede;

/**
 * Acceso a datos de la tabla {@code sede}. Sus franjas se guardan en cascada.
 *
 * @version 1.0
 */
@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

	/**
	 * Indica si ya existe una sede con ese nombre, sin distinguir mayúsculas.
	 *
	 * @param nombre nombre a verificar.
	 * @return {@code true} si el nombre ya está registrado.
	 */
	boolean existsByNombreIgnoreCase(String nombre);

	/**
	 * Indica si el nombre pertenece a una sede distinta de la indicada.
	 *
	 * @param nombre nombre a verificar.
	 * @param id     id de la sede que se está actualizando.
	 * @return {@code true} si otra sede ya tiene ese nombre.
	 */
	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

	/**
	 * Lista todas las sedes en orden alfabético.
	 *
	 * @return sedes ordenadas por nombre.
	 */
	List<Sede> findAllByOrderByNombreAsc();
}
