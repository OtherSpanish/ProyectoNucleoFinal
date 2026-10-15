package unbosque.edu.co.aulasync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unbosque.edu.co.aulasync.model.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}