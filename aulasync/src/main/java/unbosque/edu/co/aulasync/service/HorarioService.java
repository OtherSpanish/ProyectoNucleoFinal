package unbosque.edu.co.aulasync.service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.SesionHorarioDTO;
import unbosque.edu.co.aulasync.entity.Aula;
import unbosque.edu.co.aulasync.entity.Curso;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.entity.SesionHorario;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.CalendarioSemanal;
import unbosque.edu.co.aulasync.model.CausaDescartada;
import unbosque.edu.co.aulasync.model.DiaCalendario;
import unbosque.edu.co.aulasync.model.JornadaInstitucional;
import unbosque.edu.co.aulasync.model.ResultadoGeneracion;
import unbosque.edu.co.aulasync.model.SesionGenerada;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.GrupoRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * Lógica del horario guardado: generarlo con el motor, reemplazar el anterior,
 * consultarlo con filtros y armarlo como calendario semanal (por semestre, por
 * prefijo o por docente).
 *
 * <p>
 * El calendario por semestre, curso o grupo usa el Trie del horario con la
 * clave {@code semestre/curso/grupo}.
 * </p>
 *
 * @version 1.2
 */
@Service
public class HorarioService {

	private static final int MAX_DETALLE = 500;

	private static final DayOfWeek[] DIAS_CALENDARIO = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY };

	private final SesionHorarioRepository sesionHorarioRepository;
	private final GrupoRepository grupoRepository;
	private final AulaRepository aulaRepository;
	private final MotorHorarioService motorHorarioService;
	private final BusquedaService busquedaService;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param sesionHorarioRepository acceso a las sesiones guardadas.
	 * @param grupoRepository         lectura de grupos.
	 * @param aulaRepository          lectura de aulas.
	 * @param motorHorarioService     ejecución del motor.
	 * @param busquedaService         índice del horario por prefijo.
	 */
	public HorarioService(SesionHorarioRepository sesionHorarioRepository, GrupoRepository grupoRepository,
			AulaRepository aulaRepository, MotorHorarioService motorHorarioService,
			BusquedaService busquedaService) {
		this.sesionHorarioRepository = sesionHorarioRepository;
		this.grupoRepository = grupoRepository;
		this.aulaRepository = aulaRepository;
		this.motorHorarioService = motorHorarioService;
		this.busquedaService = busquedaService;
	}

	/**
	 * Ejecuta el motor, borra el horario guardado, guarda el nuevo (sesiones
	 * ubicadas y sin ubicar) y reconstruye el índice del horario.
	 *
	 * @return el mismo resultado que la vista previa del motor.
	 */
	@Transactional
	public ResultadoGeneracion generarYGuardar() {
		ResultadoGeneracion resultado = motorHorarioService.generar();
		sesionHorarioRepository.deleteAllInBatch();

		List<SesionHorario> nuevas = new ArrayList<>();
		for (SesionGenerada sesion : resultado.getUbicadas()) {
			Grupo grupo = grupoRepository.findById(sesion.getGrupoId()).orElse(null);
			Aula aula = aulaRepository.findById(sesion.getAulaId()).orElse(null);
			nuevas.add(new SesionHorario(grupo, sesion.getNumeroSesion(), sesion.getDia(), sesion.getHoraInicio(),
					aula, null, null));
		}
		for (SesionGenerada sesion : resultado.getSinUbicar()) {
			Grupo grupo = grupoRepository.findById(sesion.getGrupoId()).orElse(null);
			String causa = null;
			if (sesion.getCausa() != null) {
				causa = sesion.getCausa().name();
			}
			nuevas.add(new SesionHorario(grupo, sesion.getNumeroSesion(), null, null, null, causa,
					textoDesglose(sesion.getDesglose())));
		}
		List<SesionHorario> guardadas = sesionHorarioRepository.saveAll(nuevas);
		busquedaService.reconstruirIndiceHorario(guardadas);
		return resultado;
	}

	/**
	 * Consulta el horario guardado con filtros opcionales.
	 *
	 * @param semestre     número de semestre, o {@code null}.
	 * @param cursoId      id del curso, o {@code null}.
	 * @param grupoId      id del grupo, o {@code null}.
	 * @param docenteId    id del docente, o {@code null}.
	 * @param aulaId       id del aula, o {@code null}.
	 * @param sedeId       id de la sede, o {@code null}.
	 * @param soloUbicadas si es {@code true}, excluye las sin ubicar.
	 * @return sesiones ordenadas por semestre, día y hora (las sin ubicar al
	 *         final de su semestre).
	 */
	@Transactional(readOnly = true)
	public List<SesionHorarioDTO> listar(Integer semestre, Long cursoId, Long grupoId, Long docenteId, Long aulaId,
			Long sedeId, Boolean soloUbicadas) {
		List<SesionHorarioDTO> resultado = new ArrayList<>();
		for (SesionHorario sesion : sesionHorarioRepository.findAll()) {
			Grupo grupo = sesion.getGrupo();
			Curso curso = grupo.getCurso();
			Aula aula = sesion.getAula();
			boolean ubicada = estaUbicada(sesion);

			boolean pasa = semestre == null || curso.getSemestre().getNumero() == semestre;
			pasa = pasa && (cursoId == null || curso.getId().equals(cursoId));
			pasa = pasa && (grupoId == null || grupo.getId().equals(grupoId));
			pasa = pasa && (docenteId == null || grupo.getDocente().getId().equals(docenteId));
			pasa = pasa && (aulaId == null || (aula != null && aula.getId().equals(aulaId)));
			pasa = pasa && (sedeId == null || (aula != null && aula.getSede().getId().equals(sedeId)));
			pasa = pasa && (soloUbicadas == null || !soloUbicadas || ubicada);
			if (pasa) {
				resultado.add(convertirADTO(sesion));
			}
		}
		ordenar(resultado);
		return resultado;
	}

	/**
	 * Arma el horario como calendario semanal. Si se da {@code prefijo} se usa
	 * tal cual en el Trie; si no, y se da {@code semestre}, se busca con
	 * {@code "semestre/"}; si no se da ninguno, se arma el horario completo.
	 *
	 * @param semestre número de semestre, o {@code null}.
	 * @param prefijo  prefijo de la clave {@code semestre/curso/grupo}, o
	 *                 {@code null}.
	 * @return calendario con columnas de lunes a sábado y sesiones sin ubicar.
	 * @throws DatoFueraDeRangoException si el semestre no está entre 1 y 9.
	 */
	@Transactional(readOnly = true)
	public CalendarioSemanal calendario(Integer semestre, String prefijo) throws DatoFueraDeRangoException {
		String clave = null;
		String descripcion = "Horario completo";
		if (prefijo != null && !prefijo.isBlank()) {
			clave = prefijo.trim();
			descripcion = "Prefijo " + clave;
		} else if (semestre != null) {
			LanzadorException.verificarRango(semestre, "semestre", SemestreService.MIN_SEMESTRE,
					SemestreService.MAX_SEMESTRE);
			clave = semestre + "/";
			descripcion = "Semestre " + semestre;
		}

		List<SesionHorario> sesiones;
		if (clave == null) {
			sesiones = sesionHorarioRepository.findAll();
		} else {
			sesiones = sesionHorarioRepository.findAllById(busquedaService.buscarSesiones(clave));
		}
		return armarCalendario(sesiones, descripcion, clave);
	}

	/**
	 * Arma el calendario semanal de un docente con las sesiones de todos sus
	 * grupos.
	 *
	 * @param docenteId     id del docente.
	 * @param nombreDocente nombre del docente, para la descripción.
	 * @return calendario del docente.
	 */
	@Transactional(readOnly = true)
	public CalendarioSemanal calendarioDocente(Long docenteId, String nombreDocente) {
		return armarCalendario(sesionHorarioRepository.findByGrupoDocenteId(docenteId), "Horario de " + nombreDocente,
				null);
	}

	/**
	 * Convierte una sesión guardada en DTO, con datos informativos.
	 *
	 * @param sesion entidad a convertir.
	 * @return DTO equivalente.
	 */
	public SesionHorarioDTO convertirADTO(SesionHorario sesion) {
		Grupo grupo = sesion.getGrupo();
		Curso curso = grupo.getCurso();
		int duracion = curso.getIntensidad().getDuracionHoras();

		SesionHorarioDTO dto = new SesionHorarioDTO();
		dto.setId(sesion.getId());
		dto.setGrupoId(grupo.getId());
		dto.setNumeroSesion(sesion.getNumeroSesion());
		dto.setDia(sesion.getDia());
		dto.setHoraInicio(sesion.getHoraInicio());
		dto.setCausa(sesion.getCausa());
		dto.setDetalleCausa(sesion.getDetalleCausa());
		dto.setUbicada(estaUbicada(sesion));
		dto.setDuracionHoras(duracion);
		dto.setCursoId(curso.getId());
		dto.setCursoCodigo(curso.getCodigo());
		dto.setCursoNombre(curso.getNombre());
		dto.setSemestre(curso.getSemestre().getNumero());
		dto.setNumeroGrupo(grupo.getNumero());
		dto.setDocenteId(grupo.getDocente().getId());
		dto.setDocenteNombre(grupo.getDocente().getUsuario().getNombre());

		if (sesion.getDia() != null) {
			dto.setDiaNombre(MotorHorarioService.nombreDia(sesion.getDia()));
		}
		if (sesion.getHoraInicio() != null) {
			dto.setHoraFin(sesion.getHoraInicio() + duracion);
		}
		Aula aula = sesion.getAula();
		if (aula != null) {
			dto.setAulaId(aula.getId());
			dto.setAulaNombre(aula.getNombre());
			dto.setSedeId(aula.getSede().getId());
			dto.setSedeNombre(aula.getSede().getNombre());
		}
		return dto;
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private CalendarioSemanal armarCalendario(List<SesionHorario> sesiones, String descripcion, String clave) {
		List<SesionHorarioDTO> sinUbicar = new ArrayList<>();
		List<List<SesionHorarioDTO>> porDia = new ArrayList<>();
		for (int i = 0; i < DIAS_CALENDARIO.length; i++) {
			porDia.add(new ArrayList<SesionHorarioDTO>());
		}
		int ubicadas = 0;
		for (SesionHorario sesion : sesiones) {
			SesionHorarioDTO dto = convertirADTO(sesion);
			if (dto.getUbicada()) {
				porDia.get(sesion.getDia().getValue() - 1).add(dto);
				ubicadas++;
			} else {
				sinUbicar.add(dto);
			}
		}

		List<DiaCalendario> dias = new ArrayList<>();
		for (int i = 0; i < DIAS_CALENDARIO.length; i++) {
			List<SesionHorarioDTO> delDia = porDia.get(i);
			ordenarPorHora(delDia);
			dias.add(new DiaCalendario(DIAS_CALENDARIO[i], MotorHorarioService.nombreDia(DIAS_CALENDARIO[i]), delDia));
		}
		ordenar(sinUbicar);

		return new CalendarioSemanal(descripcion, clave, JornadaInstitucional.APERTURA,
				JornadaInstitucional.CIERRE_ENTRE_SEMANA, dias, sinUbicar, ubicadas);
	}

	private boolean estaUbicada(SesionHorario sesion) {
		return sesion.getDia() != null && sesion.getHoraInicio() != null && sesion.getAula() != null;
	}

	private String textoDesglose(List<CausaDescartada> desglose) {
		StringBuilder texto = new StringBuilder();
		for (CausaDescartada causa : desglose) {
			if (texto.length() > 0) {
				texto.append(" | ");
			}
			texto.append(causa.getMotivo().name()).append(": ").append(causa.getFranjas()).append(" franjas");
		}
		if (texto.length() > MAX_DETALLE) {
			return texto.substring(0, MAX_DETALLE);
		}
		if (texto.length() == 0) {
			return null;
		}
		return texto.toString();
	}

	private void ordenarPorHora(List<SesionHorarioDTO> sesiones) {
		Collections.sort(sesiones, new Comparator<SesionHorarioDTO>() {
			@Override
			public int compare(SesionHorarioDTO a, SesionHorarioDTO b) {
				int porHora = Integer.compare(a.getHoraInicio(), b.getHoraInicio());
				if (porHora != 0) {
					return porHora;
				}
				int porCurso = a.getCursoCodigo().compareTo(b.getCursoCodigo());
				if (porCurso != 0) {
					return porCurso;
				}
				return Integer.compare(a.getNumeroGrupo(), b.getNumeroGrupo());
			}
		});
	}

	private void ordenar(List<SesionHorarioDTO> sesiones) {
		Collections.sort(sesiones, new Comparator<SesionHorarioDTO>() {
			@Override
			public int compare(SesionHorarioDTO a, SesionHorarioDTO b) {
				int porSemestre = Integer.compare(a.getSemestre(), b.getSemestre());
				if (porSemestre != 0) {
					return porSemestre;
				}
				boolean aUbicada = a.getUbicada();
				boolean bUbicada = b.getUbicada();
				if (aUbicada != bUbicada) {
					if (aUbicada) {
						return -1;
					}
					return 1;
				}
				if (aUbicada) {
					int porDia = a.getDia().compareTo(b.getDia());
					if (porDia != 0) {
						return porDia;
					}
					int porHora = Integer.compare(a.getHoraInicio(), b.getHoraInicio());
					if (porHora != 0) {
						return porHora;
					}
				}
				int porCurso = a.getCursoCodigo().compareTo(b.getCursoCodigo());
				if (porCurso != 0) {
					return porCurso;
				}
				return Integer.compare(a.getNumeroGrupo(), b.getNumeroGrupo());
			}
		});
	}
}
