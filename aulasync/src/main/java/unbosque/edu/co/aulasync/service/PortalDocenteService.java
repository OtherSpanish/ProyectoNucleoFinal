package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.GrupoDTO;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.exception.AccesoDocenteException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.CalendarioSemanal;
import unbosque.edu.co.aulasync.model.DisponibilidadDocente;
import unbosque.edu.co.aulasync.model.InscritosGrupo;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.GrupoRepository;

/**
 * Consultas del docente sobre su propia información: sus grupos, los
 * inscritos de cada uno, su horario y su disponibilidad.
 *
 * <p>
 * El docente se identifica por el correo del token. Solo puede ver grupos que
 * él dicta.
 * </p>
 *
 * @version 1.0
 */
@Service
public class PortalDocenteService {

	private final DocenteRepository docenteRepository;
	private final GrupoRepository grupoRepository;
	private final GrupoService grupoService;
	private final InscripcionService inscripcionService;
	private final HorarioService horarioService;
	private final DisponibilidadService disponibilidadService;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param docenteRepository     búsqueda del docente por correo.
	 * @param grupoRepository       grupos del docente.
	 * @param grupoService          búsqueda y conversión de grupos.
	 * @param inscripcionService    inscritos de un grupo.
	 * @param horarioService        calendario del docente.
	 * @param disponibilidadService disponibilidad del docente.
	 */
	public PortalDocenteService(DocenteRepository docenteRepository, GrupoRepository grupoRepository,
			GrupoService grupoService, InscripcionService inscripcionService, HorarioService horarioService,
			DisponibilidadService disponibilidadService) {
		this.docenteRepository = docenteRepository;
		this.grupoRepository = grupoRepository;
		this.grupoService = grupoService;
		this.inscripcionService = inscripcionService;
		this.horarioService = horarioService;
		this.disponibilidadService = disponibilidadService;
	}

	/**
	 * Lista los grupos que dicta el docente, ordenados por semestre, curso y
	 * número.
	 *
	 * @param correo correo del usuario autenticado.
	 * @return grupos del docente.
	 * @throws AccesoDocenteException si el usuario no está vinculado a un
	 *                                docente.
	 */
	@Transactional(readOnly = true)
	public List<GrupoDTO> misGrupos(String correo) throws AccesoDocenteException {
		Docente docente = obtenerDocente(correo);
		List<Grupo> grupos = new ArrayList<>(grupoRepository.findByDocenteId(docente.getId()));
		Collections.sort(grupos, new Comparator<Grupo>() {
			@Override
			public int compare(Grupo a, Grupo b) {
				int porSemestre = Integer.compare(a.getCurso().getSemestre().getNumero(),
						b.getCurso().getSemestre().getNumero());
				if (porSemestre != 0) {
					return porSemestre;
				}
				int porCurso = a.getCurso().getNombre().compareToIgnoreCase(b.getCurso().getNombre());
				if (porCurso != 0) {
					return porCurso;
				}
				return Integer.compare(a.getNumero(), b.getNumero());
			}
		});
		List<GrupoDTO> resultado = new ArrayList<>();
		for (Grupo grupo : grupos) {
			resultado.add(grupoService.convertirADTO(grupo));
		}
		return resultado;
	}

	/**
	 * Consulta los inscritos de un grupo que dicta el docente.
	 *
	 * @param correo  correo del usuario autenticado.
	 * @param grupoId id del grupo.
	 * @return grupo e inscritos.
	 * @throws AccesoDocenteException       si el usuario no está vinculado a un
	 *                                      docente o el grupo es de otro.
	 * @throws EntidadNoEncontradaException si el grupo no existe.
	 */
	@Transactional(readOnly = true)
	public InscritosGrupo estudiantesDeGrupo(String correo, Long grupoId)
			throws AccesoDocenteException, EntidadNoEncontradaException {
		Docente docente = obtenerDocente(correo);
		Grupo grupo = grupoService.obtenerEntidad(grupoId);
		LanzadorException.verificarGrupoDelDocente(grupo.getDocente().getId().equals(docente.getId()));
		return inscripcionService.obtener(grupoId);
	}

	/**
	 * Arma el calendario semanal del docente.
	 *
	 * @param correo correo del usuario autenticado.
	 * @return calendario con sus sesiones.
	 * @throws AccesoDocenteException si el usuario no está vinculado a un
	 *                                docente.
	 */
	@Transactional(readOnly = true)
	public CalendarioSemanal miHorario(String correo) throws AccesoDocenteException {
		Docente docente = obtenerDocente(correo);
		return horarioService.calendarioDocente(docente.getId(), docente.getUsuario().getNombre());
	}

	/**
	 * Consulta la disponibilidad registrada del docente.
	 *
	 * @param correo correo del usuario autenticado.
	 * @return franjas, horas disponibles y tope.
	 * @throws AccesoDocenteException       si el usuario no está vinculado a un
	 *                                      docente.
	 * @throws EntidadNoEncontradaException nunca en la práctica (el docente ya
	 *                                      se encontró).
	 */
	@Transactional(readOnly = true)
	public DisponibilidadDocente miDisponibilidad(String correo)
			throws AccesoDocenteException, EntidadNoEncontradaException {
		Docente docente = obtenerDocente(correo);
		return disponibilidadService.obtener(docente.getId());
	}

	private Docente obtenerDocente(String correo) throws AccesoDocenteException {
		String normalizado = null;
		if (correo != null) {
			normalizado = correo.trim().toLowerCase();
		}
		Docente docente = docenteRepository.findByUsuarioCorreo(normalizado).orElse(null);
		LanzadorException.verificarDocenteVinculado(docente);
		return docente;
	}
}
