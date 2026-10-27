package unbosque.edu.co.aulasync.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.LoginRequestDto;
import unbosque.edu.co.aulasync.dto.LoginResponseDto;
import unbosque.edu.co.aulasync.dto.UsuarioResponseDto;
import unbosque.edu.co.aulasync.exception.AccesoDenegadoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.NoAutenticadoException;
import unbosque.edu.co.aulasync.model.Usuario;
import unbosque.edu.co.aulasync.repository.UsuarioRepository;
import unbosque.edu.co.aulasync.seguridad.ContrasenaService;
import unbosque.edu.co.aulasync.seguridad.Rol;
import unbosque.edu.co.aulasync.seguridad.TokenService;

/** Inicio de sesión y consulta del usuario autenticado. */
@Service
@Transactional(readOnly = true)
public class AuthService {

	private static final String CREDENCIALES = "Correo o contraseña incorrectos.";

	private final UsuarioRepository usuarioRepo;
	private final ContrasenaService contrasenas;
	private final TokenService tokens;

	public AuthService(UsuarioRepository usuarioRepo, ContrasenaService contrasenas, TokenService tokens) {
		this.usuarioRepo = usuarioRepo;
		this.contrasenas = contrasenas;
		this.tokens = tokens;
	}

	/**
	 * Verifica las credenciales y emite un token. Si la contraseña guardada aún
	 * estaba en texto plano (usuarios antiguos), la cifra en este momento.
	 *
	 * @param dto correo y contraseña
	 * @return token y datos del usuario
	 * @throws NoAutenticadoException si las credenciales son incorrectas
	 * @throws AccesoDenegadoException si el usuario no tiene un rol válido
	 */
	@Transactional(rollbackFor = Exception.class)
	public LoginResponseDto login(LoginRequestDto dto) throws NoAutenticadoException, AccesoDenegadoException {
		if (dto == null || dto.correo() == null || dto.password() == null) {
			throw new NoAutenticadoException(CREDENCIALES);
		}
		Usuario usuario = usuarioRepo.findByCorreoIgnoreCase(dto.correo().trim()).orElse(null);
		if (usuario == null || !contrasenas.coincide(dto.password(), usuario.getPassword())) {
			throw new NoAutenticadoException(CREDENCIALES);
		}
		Rol rol = Rol.desde(usuario.getRol());
		if (rol == null) {
			throw new AccesoDenegadoException("Tu usuario no tiene un rol válido. Contacta al administrador.");
		}
		if (!contrasenas.estaCifrada(usuario.getPassword())) {
			usuario.setPassword(contrasenas.cifrar(dto.password()));
			usuarioRepo.save(usuario);
		}
		return new LoginResponseDto(tokens.emitir(usuario.getId(), rol), usuario.getId(), usuario.getNombre(),
				usuario.getCorreo(), rol.name());
	}

	/**
	 * Devuelve los datos del usuario autenticado.
	 *
	 * @param id identificador del usuario
	 * @return usuario sin contraseña
	 * @throws EntidadNoEncontradaException si ya no existe
	 */
	public UsuarioResponseDto perfil(Long id) throws EntidadNoEncontradaException {
		Usuario usuario = usuarioRepo.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(usuario, "Usuario", id);
		return new UsuarioResponseDto(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol());
	}
}
