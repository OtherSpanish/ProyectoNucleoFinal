package unbosque.edu.co.aulasync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import unbosque.edu.co.aulasync.model.Usuario;
import unbosque.edu.co.aulasync.repository.UsuarioRepository;
import unbosque.edu.co.aulasync.seguridad.ContrasenaService;
import unbosque.edu.co.aulasync.seguridad.Rol;

/**
 * Crea el primer administrador si todavía no existe ninguno, usando las
 * variables {@code AULASYNC_ADMIN_CORREO} y {@code AULASYNC_ADMIN_PASSWORD}. Si
 * no están definidas o ya hay un administrador, no hace nada. Corre en todos
 * los perfiles; sirve para arrancar contra la base de Oracle Cloud sin el
 * {@code DataSeeder} (que es solo del perfil dev).
 */
@Configuration
public class AdminInicial {

	@Bean
	CommandLineRunner crearAdminInicial(UsuarioRepository usuarioRepo, ContrasenaService contrasenas,
			@Value("${aulasync.admin.correo:}") String correo, @Value("${aulasync.admin.password:}") String password) {
		return args -> {
			if (correo.isBlank() || password.isBlank() || usuarioRepo.countByRol(Rol.ADMIN.name()) > 0) {
				return;
			}
			usuarioRepo.save(new Usuario("Administrador", correo.trim().toLowerCase(), contrasenas.cifrar(password),
					Rol.ADMIN.name()));
			System.out.println(">> Administrador inicial creado: " + correo);
		};
	}
}
