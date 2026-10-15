package unbosque.edu.co.aulasync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unbosque.edu.co.aulasync.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}