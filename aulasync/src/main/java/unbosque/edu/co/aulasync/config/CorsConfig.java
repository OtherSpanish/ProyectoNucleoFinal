package unbosque.edu.co.aulasync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import unbosque.edu.co.aulasync.seguridad.AutenticacionInterceptor;

/**
 * Configuración web: CORS para el front en Angular y registro del interceptor
 * de autenticación. El login y la demostración del motor son públicos.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final AutenticacionInterceptor autenticacion;
	private final String[] origenes;

	public CorsConfig(AutenticacionInterceptor autenticacion,
			@Value("${aulasync.cors.origenes:http://localhost:4200}") String[] origenes) {
		this.autenticacion = autenticacion;
		this.origenes = origenes;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**").allowedOrigins(origenes)
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS").allowedHeaders("*");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(autenticacion).addPathPatterns("/api/**").excludePathPatterns("/api/auth/login",
				"/api/motor/demo");
	}
}
