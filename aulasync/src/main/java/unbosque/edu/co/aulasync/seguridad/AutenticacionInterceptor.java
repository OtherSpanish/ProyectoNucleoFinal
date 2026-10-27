package unbosque.edu.co.aulasync.seguridad;

import java.util.Arrays;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import unbosque.edu.co.aulasync.exception.AccesoDenegadoException;
import unbosque.edu.co.aulasync.exception.NoAutenticadoException;
import unbosque.edu.co.aulasync.model.Usuario;
import unbosque.edu.co.aulasync.repository.UsuarioRepository;

/**
 * Protege la API: exige un token válido y aplica {@link RolesPermitidos}. El
 * usuario y su rol se consultan en la base en cada petición, de modo que borrar
 * un usuario o cambiarle el rol surte efecto de inmediato, sin esperar a que
 * venza su token. Las excepciones que lanza las convierte en 401/403 el
 * {@code GlobalExceptionHandler}.
 */
@Component
public class AutenticacionInterceptor implements HandlerInterceptor {

	/** Nombre del atributo de petición donde queda la {@link SesionUsuario}. */
	public static final String ATRIBUTO_SESION = "aulasync.sesion";

	private final TokenService tokenService;
	private final UsuarioRepository usuarioRepo;

	public AutenticacionInterceptor(TokenService tokenService, UsuarioRepository usuarioRepo) {
		this.tokenService = tokenService;
		this.usuarioRepo = usuarioRepo;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || !(handler instanceof HandlerMethod)) {
			return true;
		}

		String cabecera = request.getHeader("Authorization");
		if (cabecera == null || !cabecera.startsWith("Bearer ")) {
			throw new NoAutenticadoException("Debes iniciar sesión para usar este recurso.");
		}

		SesionUsuario delToken = tokenService.validar(cabecera.substring(7).trim());
		Usuario usuario = usuarioRepo.findById(delToken.getId()).orElse(null);
		if (usuario == null) {
			throw new NoAutenticadoException("Tu sesión no es válida o expiró. Inicia sesión de nuevo.");
		}
		Rol rol = Rol.desde(usuario.getRol());
		if (rol == null) {
			throw new AccesoDenegadoException("Tu usuario no tiene un rol válido. Contacta al administrador.");
		}

		HandlerMethod metodo = (HandlerMethod) handler;
		RolesPermitidos permiso = metodo.getMethodAnnotation(RolesPermitidos.class);
		if (permiso == null) {
			permiso = metodo.getBeanType().getAnnotation(RolesPermitidos.class);
		}
		if (permiso != null && !Arrays.asList(permiso.value()).contains(rol)) {
			throw new AccesoDenegadoException("Tu rol (" + rol + ") no tiene permiso para esta operación.");
		}

		request.setAttribute(ATRIBUTO_SESION, new SesionUsuario(usuario.getId(), rol));
		return true;
	}
}
