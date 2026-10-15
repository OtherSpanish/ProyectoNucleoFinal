package unbosque.edu.co.aulasync.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import unbosque.edu.co.aulasync.model.FranjaDocente;

public interface FranjaDocenteRepository extends JpaRepository<FranjaDocente, Long> {
	List<FranjaDocente> findByDocenteId(Long docenteId);
}