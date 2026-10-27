package unbosque.edu.co.aulasync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import unbosque.edu.co.aulasync.model.SesionHorario;

public interface SesionHorarioRepository extends JpaRepository<SesionHorario, Long> {

	List<SesionHorario> findBySeccionId(Long seccionId);

	List<SesionHorario> findBySeccionGrupoId(Long grupoId);

	List<SesionHorario> findByAulaId(Long aulaId);

	boolean existsByAulaId(Long aulaId);

	void deleteBySeccionId(Long seccionId);
}
