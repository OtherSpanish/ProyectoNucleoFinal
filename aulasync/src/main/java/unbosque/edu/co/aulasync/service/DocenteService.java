package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.DocenteDTO;
import unbosque.edu.co.aulasync.dto.EspecialidadDTO;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.Especialidad;
import unbosque.edu.co.aulasync.entity.Usuario;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadConGruposException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.UsuarioNoEsDocenteException;
import unbosque.edu.co.aulasync.exception.UsuarioYaVinculadoException;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.model.Rol;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.GrupoRepository;
import unbosque.edu.co.aulasync.repository.UsuarioRepository;

/**
 * Lógica de negocio de los docentes.
 *
 * <p>
 * Valida que cada docente esté vinculado a un usuario con rol DOCENTE (y solo
 * a uno), que tenga dedicación y al menos una especialidad. Las especialidades
 * repetidas en una petición se ignoran. Un docente que dicta grupos no se puede
 * eliminar. Mantiene actualizado el índice de búsqueda por prefijo y hace las
 * conversiones entre {@link Docente} y {@link DocenteDTO}.
 * </p>
 *
 * <p>
 * Al actualizar un docente se publica {@link CambioDatosMaestros}, porque un
 * cambio de dedicación o de especialidades puede invalidar las sesiones ya
 * ubicadas de los grupos que dicta.
 * </p>
 *
 * @version 1.3
 */
@Service
public class DocenteService {

	private static final String ENTIDAD = "Docente";

	private final DocenteRepository docenteRepository;
	private final UsuarioRepository usuarioRepository;
	private final GrupoRepository grupoRepository;
	private final UsuarioService usuarioService;
	private final EspecialidadService especialidadService;
	private final BusquedaService busquedaService;
	private final ApplicationEventPublisher eventos;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param docenteRepository   acceso a la tabla de docentes.
	 * @param usuarioRepository   acceso a la tabla de usuarios.
	 * @param grupoRepository     consulta de grupos por docente.
	 * @param usuarioService      conversión de usuarios a DTO.
	 * @param especialidadService búsqueda y conversión de especialidades.
	 * @param busquedaService     índice de búsqueda por prefijo.
	 * @param eventos             publicador de eventos de Spring.
	 */
	public DocenteService(DocenteRepository docenteRepository, UsuarioRepository usuarioRepository,
			GrupoRepository grupoRepository, UsuarioService usuarioService, EspecialidadService especialidadService,
			BusquedaService busquedaService, ApplicationEventPublisher eventos) {
		this.docenteRepository = docenteRepository;
		this.usuarioRepository = usuarioRepository;
		this.grupoRepository = grupoRepository;
		this.usuarioService = usuarioService;
		this.especialidadService = especialidadService;
		this.busquedaService = busquedaService;
		this.eventos = eventos;
	}

	// ------------------------------------------------------------------
	// CRUD
	// ------------------------------------------------------------------

	/**
	 * Lista los docentes en orden alfabético, opcionalmente filtrados por
	 * especialidad.
	 *
	 * @param especialidadId id de la especialidad, o {@code null} para todos.
	 * @return lista de docentes.
	 */
	@Transactional(readOnly = true)
	public List<DocenteDTO> listar(Long especialidadId) {
		List<Docente> docentes;
		if (especialidadId == null) {
			docentes = docenteRepository.findAllByOrderByUsuarioNombreAsc();
		} else {
			docentes = docenteRepository.findByEspecialidadesIdOrderByUsuarioNombreAsc(especialidadId);
		}
		List<DocenteDTO> resultado = new ArrayList<>();
		for (Docente docente : docentes) {
			resultado.add(convertirADTO(docente));
		}
		return resultado;
	}

	/**
	 * Busca docentes con alguna palabra del nombre que empiece por el prefijo,
	 * usando el Trie.
	 *
	 * @param prefijo texto buscado.
	 * @return hasta {@value BusquedaService#LIMITE_RESULTADOS} docentes, en
	 *         orden lexicográfico.
	 * @throws CampoObligatorioException si el prefijo es nulo o vacío.
	 */
	@Transactional(readOnly = true)
	public List<DocenteDTO> buscarPorPrefijo(String prefijo) throws CampoObligatorioException {
		LanzadorException.verificarCampoObligatorio(prefijo, "prefijo");
		List<DocenteDTO> resultado = new ArrayList<>();
		for (Long id : busquedaService.buscarDocentes(prefijo)) {
			Docente docente = docenteRepository.findById(id).orElse(null);
			if (docente != null) {
				resultado.add(convertirADTO(docente));
			}
		}
		return resultado;
	}

	/**
	 * Busca un docente por id.
	 *
	 * @param id identificador.
	 * @return el docente.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public DocenteDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea un docente y lo indexa para la búsqueda. No afecta el horario
	 * guardado porque un docente nuevo no dicta grupos.
	 *
	 * @param dto datos del docente: {@code usuario.id}, {@code dedicacion} y
	 *            {@code especialidades[].id}.
	 * @return el docente creado.
	 * @throws CampoObligatorioException    si falta usuario, dedicación o
	 *                                      especialidades.
	 * @throws EntidadNoEncontradaException si el usuario o alguna especialidad
	 *                                      no existen.
	 * @throws UsuarioNoEsDocenteException  si el usuario no tiene rol DOCENTE.
	 * @throws UsuarioYaVinculadoException  si el usuario ya es de otro docente.
	 */
	@Transactional
	public DocenteDTO crear(DocenteDTO dto) throws CampoObligatorioException, EntidadNoEncontradaException,
			UsuarioNoEsDocenteException, UsuarioYaVinculadoException {
		Usuario usuario = resolverUsuario(dto, null);
		LanzadorException.verificarObjetoObligatorio(dto.getDedicacion(), "dedicacion");
		List<Especialidad> especialidades = resolverEspecialidades(dto.getEspecialidades());

		Docente docente = new Docente(usuario, dto.getDedicacion(), especialidades);
		Docente guardado = docenteRepository.save(docente);
		busquedaService.indexarDocente(guardado);
		return convertirADTO(guardado);
	}

	/**
	 * Actualiza usuario, dedicación y especialidades de un docente, lo reindexa
	 * y sincroniza el horario.
	 *
	 * @param id  identificador del docente.
	 * @param dto datos nuevos.
	 * @return el docente actualizado.
	 * @throws EntidadNoEncontradaException si el docente, el usuario o alguna
	 *                                      especialidad no existen.
	 * @throws CampoObligatorioException    si falta usuario, dedicación o
	 *                                      especialidades.
	 * @throws UsuarioNoEsDocenteException  si el usuario no tiene rol DOCENTE.
	 * @throws UsuarioYaVinculadoException  si el usuario ya es de otro docente.
	 */
	@Transactional
	public DocenteDTO actualizar(Long id, DocenteDTO dto) throws EntidadNoEncontradaException,
			CampoObligatorioException, UsuarioNoEsDocenteException, UsuarioYaVinculadoException {
		Docente docente = obtenerEntidad(id);
		Usuario usuario = resolverUsuario(dto, id);
		LanzadorException.verificarObjetoObligatorio(dto.getDedicacion(), "dedicacion");
		List<Especialidad> especialidades = resolverEspecialidades(dto.getEspecialidades());

		docente.setUsuario(usuario);
		docente.setDedicacion(dto.getDedicacion());
		docente.getEspecialidades().clear();
		docente.getEspecialidades().addAll(especialidades);
		Docente guardado = docenteRepository.save(docente);
		busquedaService.indexarDocente(guardado);
		eventos.publishEvent(new CambioDatosMaestros("Docente " + id + " actualizado"));
		return convertirADTO(guardado);
	}

	/**
	 * Elimina un docente que no dicte grupos (su usuario se conserva) y lo quita
	 * del índice. No afecta el horario guardado porque no tiene sesiones.
	 *
	 * @param id identificador.
	 * @throws EntidadNoEncontradaException si no existe.
	 * @throws EntidadConGruposException    si el docente dicta grupos.
	 */
	@Transactional
	public void eliminar(Long id) throws EntidadNoEncontradaException, EntidadConGruposException {
		Docente docente = obtenerEntidad(id);
		LanzadorException.verificarSinGrupos(grupoRepository.existsByDocenteId(id),
				"al docente " + docente.getUsuario().getNombre());
		docenteRepository.delete(docente);
		busquedaService.quitarDocente(id);
	}

	// ------------------------------------------------------------------
	// Soporte para otros servicios
	// ------------------------------------------------------------------

	/**
	 * Busca la entidad por id. La usan los servicios de disponibilidad y de
	 * grupos.
	 *
	 * @param id identificador.
	 * @return la entidad encontrada.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public Docente obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Docente docente = docenteRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(docente, ENTIDAD, id);
		return docente;
	}

	/**
	 * Convierte una entidad en DTO, con el usuario (sin contraseña) y las
	 * especialidades completas.
	 *
	 * @param docente entidad a convertir.
	 * @return DTO equivalente.
	 */
	public DocenteDTO convertirADTO(Docente docente) {
		List<EspecialidadDTO> especialidades = new ArrayList<>();
		for (Especialidad especialidad : docente.getEspecialidades()) {
			especialidades.add(especialidadService.convertirADTO(especialidad));
		}
		return new DocenteDTO(docente.getId(), usuarioService.convertirADTO(docente.getUsuario()),
				docente.getDedicacion(), especialidades);
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private Usuario resolverUsuario(DocenteDTO dto, Long idDocente) throws CampoObligatorioException,
			EntidadNoEncontradaException, UsuarioNoEsDocenteException, UsuarioYaVinculadoException {
		LanzadorException.verificarObjetoObligatorio(dto.getUsuario(), "usuario");
		Long usuarioId = dto.getUsuario().getId();
		LanzadorException.verificarObjetoObligatorio(usuarioId, "usuario.id");

		Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
		LanzadorException.verificarEntidadExiste(usuario, "Usuario", usuarioId);
		LanzadorException.verificarUsuarioEsDocente(usuario.getRol() == Rol.DOCENTE, usuario.getCorreo());

		boolean yaVinculado;
		if (idDocente == null) {
			yaVinculado = docenteRepository.existsByUsuarioId(usuarioId);
		} else {
			yaVinculado = docenteRepository.existsByUsuarioIdAndIdNot(usuarioId, idDocente);
		}
		LanzadorException.verificarUsuarioDisponible(yaVinculado, usuario.getCorreo());
		return usuario;
	}

	private List<Especialidad> resolverEspecialidades(List<EspecialidadDTO> dtos)
			throws CampoObligatorioException, EntidadNoEncontradaException {
		LanzadorException.verificarListaNoVacia(dtos, "especialidades");
		List<Especialidad> resultado = new ArrayList<>();
		Set<Long> vistos = new HashSet<>();
		for (EspecialidadDTO dto : dtos) {
			LanzadorException.verificarObjetoObligatorio(dto, "especialidades");
			LanzadorException.verificarObjetoObligatorio(dto.getId(), "especialidades.id");
			boolean esNuevo = vistos.add(dto.getId());
			if (esNuevo) {
				resultado.add(especialidadService.obtenerEntidad(dto.getId()));
			}
		}
		return resultado;
	}
}
