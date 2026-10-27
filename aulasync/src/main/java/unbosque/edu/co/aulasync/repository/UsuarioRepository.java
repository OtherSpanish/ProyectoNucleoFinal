package unbosque.edu.co.aulasync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import unbosque.edu.co.aulasync.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByCorreoIgnoreCase(String correo);

	boolean existsByCorreoIgnoreCase(String correo);

	boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);

	long countByRol(String rol);
}
