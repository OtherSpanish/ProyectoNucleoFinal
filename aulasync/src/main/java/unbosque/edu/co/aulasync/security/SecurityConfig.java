package unbosque.edu.co.aulasync.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security.
 *
 * <ul>
 * <li>API sin estado (stateless): cada petición se autentica con su JWT.</li>
 * <li>Públicos: {@code POST /api/auth/login}, Swagger y las peticiones
 * {@code OPTIONS} del CORS. Todo lo demás exige token.</li>
 * <li>Los permisos por rol se ponen en cada controlador con
 * {@code @PreAuthorize}, gracias a {@link EnableMethodSecurity}.</li>
 * <li>Los 401 y 403 se responden en JSON a través de
 * {@link ManejadorErroresSeguridad}.</li>
 * </ul>
 *
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtService jwtService;
	private final UsuarioDetallesService usuarioDetallesService;
	private final ManejadorErroresSeguridad manejadorErrores;

	/**
	 * Crea la configuración con sus dependencias.
	 *
	 * @param jwtService             validador de tokens.
	 * @param usuarioDetallesService cargador de usuarios.
	 * @param manejadorErrores       manejador de 401 y 403.
	 */
	public SecurityConfig(JwtService jwtService, UsuarioDetallesService usuarioDetallesService,
			ManejadorErroresSeguridad manejadorErrores) {
		this.jwtService = jwtService;
		this.usuarioDetallesService = usuarioDetallesService;
		this.manejadorErrores = manejadorErrores;
	}

	/**
	 * Cadena de filtros de seguridad de la API.
	 *
	 * @param http constructor de la configuración HTTP.
	 * @return la cadena de filtros construida.
	 * @throws Exception si la configuración es inválida.
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(new Customizer<CsrfConfigurer<HttpSecurity>>() {
			@Override
			public void customize(CsrfConfigurer<HttpSecurity> csrf) {
				csrf.disable();
			}
		});

		// Usa el bean CorsConfigurationSource de config/CorsConfig.
		http.cors(Customizer.withDefaults());

		http.sessionManagement(new Customizer<SessionManagementConfigurer<HttpSecurity>>() {
			@Override
			public void customize(SessionManagementConfigurer<HttpSecurity> sesion) {
				sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			}
		});

		http.httpBasic(new Customizer<HttpBasicConfigurer<HttpSecurity>>() {
			@Override
			public void customize(HttpBasicConfigurer<HttpSecurity> basic) {
				basic.disable();
			}
		});

		http.formLogin(new Customizer<FormLoginConfigurer<HttpSecurity>>() {
			@Override
			public void customize(FormLoginConfigurer<HttpSecurity> form) {
				form.disable();
			}
		});

		http.exceptionHandling(new Customizer<ExceptionHandlingConfigurer<HttpSecurity>>() {
			@Override
			public void customize(ExceptionHandlingConfigurer<HttpSecurity> errores) {
				errores.authenticationEntryPoint(manejadorErrores);
				errores.accessDeniedHandler(manejadorErrores);
			}
		});

		http.authorizeHttpRequests(
				new Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
					@Override
					public void customize(
							AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry rutas) {
						rutas.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
						rutas.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
						rutas.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll();
						rutas.requestMatchers("/error").permitAll();
						rutas.anyRequest().authenticated();
					}
				});

		http.addFilterBefore(new JwtFiltro(jwtService, usuarioDetallesService),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
