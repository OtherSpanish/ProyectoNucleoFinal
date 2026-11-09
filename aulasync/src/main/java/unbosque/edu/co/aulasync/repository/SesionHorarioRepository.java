package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import unbosque.edu.co.aulasync.entity.SesionHorario;

/**
 * Acceso a datos de la tabla {@code sesion_horario}.
 *
 * @version 1.1
 */
@Repository
public interface SesionHorarioRepository extends JpaRepository<SesionHorario, Long> {

	/**
	 * Lista las sesiones de un grupo.
	 *
	 * @param grupoId id del grupo.
	 * @return sesiones del grupo.
	 */
	List<SesionHorario> findByGrupoId(Long grupoId);

	/**
	 * Lista las sesiones asignadas a un aula.
	 *
	 * @param aulaId id del aula.
	 * @return sesiones del aula.
	 */
	List<SesionHorario> findByAulaId(Long aulaId);

	/**
	 * Lista las sesiones de todos los grupos que dicta un docente.
	 *
	 * @param docenteId id del docente.
	 * @return sesiones del docente.
	 */
	List<SesionHorario> findByGrupoDocenteId(Long docenteId);
}
