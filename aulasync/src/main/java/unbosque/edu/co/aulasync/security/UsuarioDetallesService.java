package unbosque.edu.co.aulasync.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import unbosque.edu.co.aulasync.entity.Usuario;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.service.UsuarioService;

/**
 * Carga los usuarios para Spring Security a partir de su correo.
 *
 * <p>
 * No toca el repositorio: consulta a {@link UsuarioService}. El rol se lee de
 * la base en cada petición, así que si un usuario se borra o se le cambia el
 * rol, el cambio aplica de inmediato aunque su token siga vigente.
 * </p>
 *
 * @version 1.0
 */
@Service
public class UsuarioDetallesService implements UserDetailsService {

	private final UsuarioService usuarioService;

	/**
	 * Crea el servicio.
	 *
	 * @param usuarioService servicio de usuarios.
	 */
	public UsuarioDetallesService(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	/**
	 * Busca el usuario por correo y lo convierte al formato de Spring Security,
	 * con la autoridad {@code ROLE_<ROL>}.
	 *
	 * @param correo correo del usuario.
	 * @return detalles del usuario para Spring Security.
	 * @throws UsernameNotFoundException si no existe un usuario con ese correo.
	 */
	@Override
	public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
		Usuario usuario = usuarioService.buscarEntidadPorCorreo(correo);
		LanzadorException.verificarUsuarioSeguridadExiste(usuario, correo);
		return User.withUsername(usuario.getCorreo())
				.password(usuario.getPassword())
				.roles(usuario.getRol().name())
				.build();
	}
}
