package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Aula;

/**
 * Acceso a datos de la tabla {@code aula}.
 *
 * @version 1.0
 */
@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

	/**
	 * Indica si ya existe un aula con ese nombre en la sede, sin distinguir
	 * mayúsculas.
	 *
	 * @param sedeId id de la sede.
	 * @param nombre nombre a verificar.
	 * @return {@code true} si el nombre ya está en uso en esa sede.
	 */
	boolean existsBySedeIdAndNombreIgnoreCase(Long sedeId, String nombre);

	/**
	 * Indica si el nombre pertenece, en la misma sede, a un aula distinta de la
	 * indicada.
	 *
	 * @param sedeId id de la sede.
	 * @param nombre nombre a verificar.
	 * @param id     id del aula que se está actualizando.
	 * @return {@code true} si otra aula de la sede ya tiene ese nombre.
	 */
	boolean existsBySedeIdAndNombreIgnoreCaseAndIdNot(Long sedeId, String nombre, Long id);

	/**
	 * Indica si una sede tiene al menos un aula.
	 *
	 * @param sedeId id de la sede.
	 * @return {@code true} si la sede tiene aulas.
	 */
	boolean existsBySedeId(Long sedeId);

	/**
	 * Lista todas las aulas ordenadas por sede y por nombre.
	 *
	 * @return aulas ordenadas.
	 */
	List<Aula> findAllByOrderBySedeNombreAscNombreAsc();
}
