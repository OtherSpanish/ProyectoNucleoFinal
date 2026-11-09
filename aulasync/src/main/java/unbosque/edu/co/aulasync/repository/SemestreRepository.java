package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.Semestre;

/**
 * Acceso a datos de la tabla {@code semestre}.
 *
 * @version 1.0
 */
@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {

	/**
	 * Indica si ya existe un semestre con ese número.
	 *
	 * @param numero número a verificar.
	 * @return {@code true} si el número ya está registrado.
	 */
	boolean existsByNumero(int numero);

	/**
	 * Indica si el número pertenece a un semestre distinto del indicado.
	 *
	 * @param numero número a verificar.
	 * @param id     id del semestre que se está actualizando.
	 * @return {@code true} si otro semestre ya tiene ese número.
	 */
	boolean existsByNumeroAndIdNot(int numero, Long id);

	/**
	 * Lista todos los semestres en orden numérico.
	 *
	 * @return semestres ordenados por número.
	 */
	List<Semestre> findAllByOrderByNumeroAsc();
}
