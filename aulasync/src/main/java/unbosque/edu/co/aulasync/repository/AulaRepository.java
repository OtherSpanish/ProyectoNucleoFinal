package unbosque.edu.co.aulasync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unbosque.edu.co.aulasync.model.Aula;

public interface AulaRepository extends JpaRepository<Aula, Long> {
}