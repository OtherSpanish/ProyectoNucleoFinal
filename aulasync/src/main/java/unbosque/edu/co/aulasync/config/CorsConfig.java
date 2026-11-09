package unbosque.edu.co.aulasync.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración de CORS para que el front en Angular pueda consumir la API.
 *
 * <p>
 * Los orígenes permitidos salen de {@code aulasync.cors.origenes} (separados
 * por coma). Spring Security toma este bean automáticamente.
 * </p>
 *
 * @version 1.0
 */
@Configuration
public class CorsConfig {

	private static final long MAX_AGE_SEGUNDOS = 3600L;

	/**
	 * Fuente de configuración CORS aplicada a todas las rutas.
	 *
	 * @param origenes orígenes permitidos (por ejemplo
	 *                 {@code http://localhost:4200}).
	 * @return la configuración CORS registrada para {@code /**}.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource(
			@Value("${aulasync.cors.origenes:http://localhost:4200}") String[] origenes) {
		List<String> limpios = new ArrayList<>();
		for (String origen : origenes) {
			if (origen != null && !origen.isBlank()) {
				limpios.add(origen.trim());
			}
		}

		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(limpios);
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		config.setMaxAge(MAX_AGE_SEGUNDOS);

		UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
		fuente.registerCorsConfiguration("/**", config);
		return fuente;
	}
}
