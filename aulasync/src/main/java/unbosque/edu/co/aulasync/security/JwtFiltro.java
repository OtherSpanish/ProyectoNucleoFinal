package unbosque.edu.co.aulasync.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que autentica cada petición a partir de la cabecera
 * {@code Authorization: Bearer <token>}.
 *
 * <p>
 * Si el token es válido y el usuario existe, lo deja autenticado en el
 * {@link SecurityContextHolder} con su rol. Si no hay token, es inválido o
 * venció, la petición sigue como anónima y Spring Security responde 401 en
 * las rutas protegidas.
 * </p>
 *
 * @version 1.0
 */
public class JwtFiltro extends OncePerRequestFilter {

	private static final String PREFIJO = "Bearer ";

	private final JwtService jwtService;
	private final UsuarioDetallesService usuarioDetallesService;

	/**
	 * Crea el filtro.
	 *
	 * @param jwtService             validador de tokens.
	 * @param usuarioDetallesService cargador de usuarios.
	 */
	public JwtFiltro(JwtService jwtService, UsuarioDetallesService usuarioDetallesService) {
		this.jwtService = jwtService;
		this.usuarioDetallesService = usuarioDetallesService;
	}

	/**
	 * Lee el token, lo valida y autentica al usuario si corresponde.
	 *
	 * @param request  petición HTTP.
	 * @param response respuesta HTTP.
	 * @param chain    cadena de filtros.
	 * @throws ServletException si falla un filtro posterior.
	 * @throws IOException      si falla la lectura o escritura.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String cabecera = request.getHeader("Authorization");
		boolean sinAutenticar = SecurityContextHolder.getContext().getAuthentication() == null;

		if (cabecera != null && cabecera.startsWith(PREFIJO) && sinAutenticar) {
			String token = cabecera.substring(PREFIJO.length()).trim();
			String correo = jwtService.extraerCorreo(token);
			if (correo != null) {
				autenticar(correo, request);
			}
		}
		chain.doFilter(request, response);
	}

	private void autenticar(String correo, HttpServletRequest request) {
		try {
			UserDetails detalles = usuarioDetallesService.loadUserByUsername(correo);
			UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(detalles,
					null, detalles.getAuthorities());
			autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(autenticacion);
		} catch (UsernameNotFoundException e) {
			// El usuario del token ya no existe: la petición sigue como anónima (401).
		}
	}
}
