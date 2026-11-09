package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.FranjaDocente;

/**
 * Acceso a datos de la tabla {@code franja_docente}.
 *
 * @version 1.0
 */
@Repository
public interface FranjaDocenteRepository extends JpaRepository<FranjaDocente, Long> {

	/**
	 * Lista las franjas de un docente ordenadas por día y hora de inicio.
	 *
	 * @param docenteId id del docente.
	 * @return franjas del docente.
	 */
	List<FranjaDocente> findByDocenteIdOrderByDiaAscHoraInicioAsc(Long docenteId);

	/**
	 * Elimina todas las franjas de un docente. Se usa al reemplazar su semana
	 * completa.
	 *
	 * @param docenteId id del docente.
	 */
	@Modifying
	void deleteByDocenteId(Long docenteId);
}
