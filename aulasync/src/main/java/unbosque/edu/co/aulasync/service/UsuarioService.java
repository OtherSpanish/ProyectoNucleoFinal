package unbosque.edu.co.aulasync.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.UsuarioRequestDto;
import unbosque.edu.co.aulasync.dto.UsuarioResponseDto;
import unbosque.edu.co.aulasync.exception.ConflictoNegocioException;
import unbosque.edu.co.aulasync.exception.DatoInvalidoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.Usuario;
import unbosque.edu.co.aulasync.repository.UsuarioRepository;
import unbosque.edu.co.aulasync.seguridad.ContrasenaService;
import unbosque.edu.co.aulasync.seguridad.Rol;

/**
 * CRUD de usuarios (rol Admin). Protege al sistema de quedarse sin
 * administradores y evita que un admin se elimine a sí mismo.
 */
@Service
@Transactional(readOnly = true)
public class UsuarioService {

	private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final int MIN_PASSWORD = 6;

	private final UsuarioRepository usuarioRepo;
	private final ContrasenaService contrasenas;

	public UsuarioService(UsuarioRepository usuarioRepo, ContrasenaService contrasenas) {
		this.usuarioRepo = usuarioRepo;
		this.contrasenas = contrasenas;
	}

	public List<UsuarioResponseDto> listar() {
		return usuarioRepo.findAll().stream().map(this::aDto).toList();
	}

	public UsuarioResponseDto obtener(Long id) throws EntidadNoEncontradaException {
		return aDto(buscar(id));
	}

	@Transactional(rollbackFor = Exception.class)
	public UsuarioResponseDto crear(UsuarioRequestDto dto) throws DatoInvalidoException, ConflictoNegocioException {
		String correo = validarBasicos(dto);
		LanzadorException.verificar(passwordValida(dto.password()),
				"La contraseña es obligatoria y debe tener al menos " + MIN_PASSWORD + " caracteres.");
		LanzadorException.verificarSinConflicto(usuarioRepo.existsByCorreoIgnoreCase(correo),
				"Ya existe un usuario con ese correo.");

		Usuario usuario = new Usuario(dto.nombre().trim(), correo, contrasenas.cifrar(dto.password()),
				Rol.desde(dto.rol()).name());
		return aDto(usuarioRepo.save(usuario));
	}

	@Transactional(rollbackFor = Exception.class)
	public UsuarioResponseDto actualizar(Long id, UsuarioRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException, ConflictoNegocioException {
		Usuario usuario = buscar(id);
		String correo = validarBasicos(dto);
		LanzadorException.verificarSinConflicto(usuarioRepo.existsByCorreoIgnoreCaseAndIdNot(correo, id),
				"Ya existe otro usuario con ese correo.");

		Rol nuevoRol = Rol.desde(dto.rol());
		if (esAdmin(usuario) && nuevoRol != Rol.ADMIN) {
			LanzadorException.verificarSinConflicto(esUnicoAdmin(), "No puedes quitarle el rol al único administrador.");
		}
		if (dto.password() != null && !dto.password().isBlank()) {
			LanzadorException.verificar(passwordValida(dto.password()),
					"La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres.");
			usuario.setPassword(contrasenas.cifrar(dto.password()));
		}
		usuario.setNombre(dto.nombre().trim());
		usuario.setCorreo(correo);
		usuario.setRol(nuevoRol.name());
		return aDto(usuarioRepo.save(usuario));
	}

	/**
	 * Elimina un usuario.
	 *
	 * @param id      usuario a eliminar
	 * @param actorId usuario que hace la petición (no puede eliminarse a sí mismo)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void eliminar(Long id, Long actorId) throws EntidadNoEncontradaException, ConflictoNegocioException {
		Usuario usuario = buscar(id);
		LanzadorException.verificarSinConflicto(id.equals(actorId), "No puedes eliminar tu propio usuario.");
		if (esAdmin(usuario)) {
			LanzadorException.verificarSinConflicto(esUnicoAdmin(), "No puedes eliminar al único administrador.");
		}
		usuarioRepo.delete(usuario);
	}

	// ---------- Utilidades ----------

	private Usuario buscar(Long id) throws EntidadNoEncontradaException {
		Usuario usuario = usuarioRepo.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(usuario, "Usuario", id);
		return usuario;
	}

	private String validarBasicos(UsuarioRequestDto dto) throws DatoInvalidoException {
		LanzadorException.verificar(dto != null, "El cuerpo de la petición es obligatorio.");
		LanzadorException.verificar(dto.nombre() != null && !dto.nombre().isBlank(), "El nombre es obligatorio.");
		String correo = dto.correo() == null ? "" : dto.correo().trim().toLowerCase();
		LanzadorException.verificar(CORREO.matcher(correo).matches(), "El correo no tiene un formato válido.");
		LanzadorException.verificar(Rol.desde(dto.rol()) != null, "El rol debe ser ADMIN, DIRECCION o COMITE.");
		return correo;
	}

	private boolean passwordValida(String password) {
		return password != null && password.length() >= MIN_PASSWORD;
	}

	private boolean esAdmin(Usuario usuario) {
		return Rol.desde(usuario.getRol()) == Rol.ADMIN;
	}

	private boolean esUnicoAdmin() {
		return usuarioRepo.countByRol(Rol.ADMIN.name()) <= 1;
	}

	private UsuarioResponseDto aDto(Usuario u) {
		return new UsuarioResponseDto(u.getId(), u.getNombre(), u.getCorreo(), u.getRol());
	}
}
