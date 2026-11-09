package unbosque.edu.co.aulasync.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración del cifrado de contraseñas.
 *
 * @version 1.0
 */
@Configuration
public class ConfiguracionContrasena {

	/**
	 * Cifrador de contraseñas con BCrypt.
	 *
	 * @return el {@link PasswordEncoder} usado al crear usuarios y al validar el
	 *         login.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
