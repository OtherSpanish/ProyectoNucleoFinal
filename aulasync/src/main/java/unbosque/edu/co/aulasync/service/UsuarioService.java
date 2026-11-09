package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.UsuarioDTO;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.Usuario;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CorreoDuplicadoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.PasswordInvalidaException;
import unbosque.edu.co.aulasync.exception.UltimoAdminException;
import unbosque.edu.co.aulasync.exception.UsuarioConDocenteException;
import unbosque.edu.co.aulasync.model.Rol;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.UsuarioRepository;

/**
 * Lógica de negocio de los usuarios del sistema.
 *
 * <p>
 * Es la única clase que escribe en {@link UsuarioRepository}. Consulta
 * {@link DocenteRepository} para no romper el vínculo usuario-docente y, si
 * cambia el nombre de un usuario docente, reindexa su docente en la búsqueda
 * por prefijo. También hace las conversiones entre {@link Usuario} y
 * {@link UsuarioDTO}.
 * </p>
 *
 * @version 1.3
 */
@Service
public class UsuarioService {

	private static final String ENTIDAD = "Usuario";

	private final UsuarioRepository usuarioRepository;
	private final DocenteRepository docenteRepository;
	private final PasswordEncoder passwordEncoder;
	private final BusquedaService busquedaService;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param usuarioRepository acceso a la tabla de usuarios.
	 * @param docenteRepository consulta de vínculos con docentes.
	 * @param passwordEncoder   cifrador de contraseñas.
	 * @param busquedaService   índice de búsqueda por prefijo.
	 */
	public UsuarioService(UsuarioRepository usuarioRepository, DocenteRepository docenteRepository,
			PasswordEncoder passwordEncoder, BusquedaService busquedaService) {
		this.usuarioRepository = usuarioRepository;
		this.docenteRepository = docenteRepository;
		this.passwordEncoder = passwordEncoder;
		this.busquedaService = busquedaService;
	}

	// ------------------------------------------------------------------
	// CRUD
	// ------------------------------------------------------------------

	/**
	 * Lista los usuarios ordenados por nombre, opcionalmente filtrados por rol.
	 *
	 * @param rol rol a filtrar, o {@code null} para todos.
	 * @return lista de usuarios sin contraseña.
	 */
	@Transactional(readOnly = true)
	public List<UsuarioDTO> listar(Rol rol) {
		List<Usuario> usuarios;
		if (rol == null) {
			usuarios = usuarioRepository.findAllByOrderByNombreAsc();
		} else {
			usuarios = usuarioRepository.findByRolOrderByNombreAsc(rol);
		}
		List<UsuarioDTO> resultado = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			resultado.add(convertirADTO(usuario));
		}
		return resultado;
	}

	/**
	 * Busca un usuario por id.
	 *
	 * @param id identificador del usuario.
	 * @return el usuario sin contraseña.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public UsuarioDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea un usuario nuevo.
	 *
	 * @param dto datos del usuario; la contraseña es obligatoria.
	 * @return el usuario creado, sin contraseña.
	 * @throws CampoObligatorioException si falta nombre, correo, contraseña o
	 *                                   rol.
	 * @throws PasswordInvalidaException si la contraseña no cumple la política.
	 * @throws CorreoDuplicadoException  si el correo ya está registrado.
	 */
	@Transactional
	public UsuarioDTO crear(UsuarioDTO dto)
			throws CampoObligatorioException, PasswordInvalidaException, CorreoDuplicadoException {
		validarDatosBasicos(dto);
		LanzadorException.verificarCampoObligatorio(dto.getPassword(), "password");
		LanzadorException.verificarPassword(dto.getPassword());

		String correo = normalizarCorreo(dto.getCorreo());
		LanzadorException.verificarCorreoDisponible(usuarioRepository.existsByCorreo(correo), correo);

		Usuario guardado = usuarioRepository.save(convertirAEntidad(dto));
		return convertirADTO(guardado);
	}

	/**
	 * Actualiza nombre, correo, rol y, si se envía, la contraseña. Si el
	 * usuario tiene un docente vinculado, lo reindexa con el nombre nuevo.
	 *
	 * @param id  identificador del usuario.
	 * @param dto datos nuevos; si {@code password} es nulo o vacío se conserva
	 *            la actual.
	 * @return el usuario actualizado, sin contraseña.
	 * @throws EntidadNoEncontradaException si el usuario no existe.
	 * @throws CampoObligatorioException    si falta nombre, correo o rol.
	 * @throws PasswordInvalidaException    si la nueva contraseña no cumple la
	 *                                      política.
	 * @throws CorreoDuplicadoException     si el correo pertenece a otro usuario.
	 * @throws UltimoAdminException         si se le quita el rol al último
	 *                                      administrador.
	 * @throws UsuarioConDocenteException   si se le quita el rol DOCENTE a un
	 *                                      usuario vinculado a un docente.
	 */
	@Transactional
	public UsuarioDTO actualizar(Long id, UsuarioDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, PasswordInvalidaException,
			CorreoDuplicadoException, UltimoAdminException, UsuarioConDocenteException {
		Usuario usuario = obtenerEntidad(id);
		validarDatosBasicos(dto);

		String correo = normalizarCorreo(dto.getCorreo());
		LanzadorException.verificarCorreoDisponible(usuarioRepository.existsByCorreoAndIdNot(correo, id), correo);

		boolean dejaDeSerAdmin = usuario.getRol() == Rol.ADMIN && dto.getRol() != Rol.ADMIN;
		LanzadorException.verificarNoEsUltimoAdmin(dejaDeSerAdmin && esUnicoAdmin());

		boolean dejaDeSerDocente = usuario.getRol() == Rol.DOCENTE && dto.getRol() != Rol.DOCENTE;
		LanzadorException.verificarUsuarioSinDocente(dejaDeSerDocente && docenteRepository.existsByUsuarioId(id),
				usuario.getCorreo());

		boolean cambiaPassword = dto.getPassword() != null && !dto.getPassword().isBlank();
		if (cambiaPassword) {
			LanzadorException.verificarPassword(dto.getPassword());
			usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
		}

		usuario.setNombre(dto.getNombre().trim());
		usuario.setCorreo(correo);
		usuario.setRol(dto.getRol());
		Usuario guardado = usuarioRepository.save(usuario);

		Docente docente = docenteRepository.findByUsuarioCorreo(guardado.getCorreo()).orElse(null);
		if (docente != null) {
			busquedaService.indexarDocente(docente);
		}
		return convertirADTO(guardado);
	}

	/**
	 * Elimina un usuario.
	 *
	 * @param id identificador del usuario.
	 * @throws EntidadNoEncontradaException si el usuario no existe.
	 * @throws UltimoAdminException         si es el último administrador.
	 * @throws UsuarioConDocenteException   si está vinculado a un docente.
	 */
	@Transactional
	public void eliminar(Long id)
			throws EntidadNoEncontradaException, UltimoAdminException, UsuarioConDocenteException {
		Usuario usuario = obtenerEntidad(id);
		boolean esAdmin = usuario.getRol() == Rol.ADMIN;
		LanzadorException.verificarNoEsUltimoAdmin(esAdmin && esUnicoAdmin());
		LanzadorException.verificarUsuarioSinDocente(docenteRepository.existsByUsuarioId(id), usuario.getCorreo());
		usuarioRepository.delete(usuario);
	}

	// ------------------------------------------------------------------
	// Soporte para autenticación y arranque
	// ------------------------------------------------------------------

	/**
	 * Busca un usuario por correo, sin distinguir mayúsculas ni espacios.
	 *
	 * @param correo correo a buscar.
	 * @return la entidad encontrada, o {@code null} si no existe.
	 */
	@Transactional(readOnly = true)
	public Usuario buscarEntidadPorCorreo(String correo) {
		if (correo == null) {
			return null;
		}
		return usuarioRepository.findByCorreo(normalizarCorreo(correo)).orElse(null);
	}

	/**
	 * Crea el primer administrador si todavía no existe ninguno.
	 *
	 * @param nombre   nombre del administrador.
	 * @param correo   correo del administrador.
	 * @param password contraseña en texto plano; se guarda cifrada.
	 * @return {@code true} si se creó; {@code false} si ya había un
	 *         administrador o faltan datos.
	 */
	@Transactional
	public boolean crearAdminInicialSiNoExiste(String nombre, String correo, String password) {
		if (usuarioRepository.existsByRol(Rol.ADMIN)) {
			return false;
		}
		if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
			return false;
		}
		Usuario admin = new Usuario(nombre, normalizarCorreo(correo), passwordEncoder.encode(password), Rol.ADMIN);
		usuarioRepository.save(admin);
		return true;
	}

	// ------------------------------------------------------------------
	// Conversiones
	// ------------------------------------------------------------------

	/**
	 * Convierte una entidad en DTO. La contraseña no se copia.
	 *
	 * @param usuario entidad a convertir.
	 * @return DTO sin contraseña.
	 */
	public UsuarioDTO convertirADTO(Usuario usuario) {
		return new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), null, usuario.getRol());
	}

	/**
	 * Convierte un DTO en una entidad nueva, sin id y con la contraseña cifrada.
	 *
	 * @param dto DTO a convertir.
	 * @return entidad lista para guardarse.
	 */
	public Usuario convertirAEntidad(UsuarioDTO dto) {
		String cifrada = null;
		if (dto.getPassword() != null) {
			cifrada = passwordEncoder.encode(dto.getPassword());
		}
		String nombre = null;
		if (dto.getNombre() != null) {
			nombre = dto.getNombre().trim();
		}
		return new Usuario(nombre, normalizarCorreo(dto.getCorreo()), cifrada, dto.getRol());
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private Usuario obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(usuario, ENTIDAD, id);
		return usuario;
	}

	private void validarDatosBasicos(UsuarioDTO dto) throws CampoObligatorioException {
		LanzadorException.verificarCampoObligatorio(dto.getNombre(), "nombre");
		LanzadorException.verificarCampoObligatorio(dto.getCorreo(), "correo");
		LanzadorException.verificarObjetoObligatorio(dto.getRol(), "rol");
	}

	private boolean esUnicoAdmin() {
		return usuarioRepository.countByRol(Rol.ADMIN) <= 1;
	}

	private String normalizarCorreo(String correo) {
		if (correo == null) {
			return null;
		}
		return correo.trim().toLowerCase();
	}
}
