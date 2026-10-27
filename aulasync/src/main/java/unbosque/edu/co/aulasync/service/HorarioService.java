package unbosque.edu.co.aulasync.service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.HorarioDto;
import unbosque.edu.co.aulasync.dto.MoverSesionRequestDto;
import unbosque.edu.co.aulasync.dto.SesionHorarioDto;
import unbosque.edu.co.aulasync.dto.ValidacionMovimientoDto;
import unbosque.edu.co.aulasync.exception.DatoInvalidoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.MovimientoInvalidoException;
import unbosque.edu.co.aulasync.model.Aula;
import unbosque.edu.co.aulasync.model.Curso;
import unbosque.edu.co.aulasync.model.Docente;
import unbosque.edu.co.aulasync.model.FranjaDocente;
import unbosque.edu.co.aulasync.model.Grupo;
import unbosque.edu.co.aulasync.model.Seccion;
import unbosque.edu.co.aulasync.model.SesionHorario;
import unbosque.edu.co.aulasync.motor.Franja;
import unbosque.edu.co.aulasync.motor.GeneradorFranjas;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * Horario guardado: consulta, generación, movimiento de sesiones (drag and
 * drop) y sincronización con los cambios en secciones, cursos, aulas y grupos.
 *
 * <p>
 * Al mover una sesión se aplican las mismas reglas del motor: franja dentro del
 * horario institucional y fuera del almuerzo, disponibilidad y tope de horas del
 * docente, sin choques de docente, grupo ni aula, una sola sesión por día de la
 * misma sección, y aula del tipo y capacidad requeridos.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class HorarioService {

	/** Causa de las sesiones nuevas que aún no se han ubicado. */
	public static final String CAUSA_PENDIENTE = "PENDIENTE_UBICACION_MANUAL";
	/** Causa de las sesiones que el Comité devolvió a "sin ubicar". */
	public static final String CAUSA_MANUAL = "DESUBICADA_MANUALMENTE";

	private static final GeneradorFranjas.Parametros PARAMS = GeneradorFranjas.Parametros.POR_DEFECTO;

	private enum Motivo {
		FRANJA_INVALIDA("La franja queda fuera del horario institucional o cruza el almuerzo."),
		DOCENTE_NO_DISPONIBLE("El docente no tiene disponibilidad en esa franja."),
		CARGA_DOCENTE("El docente superaría su tope de horas semanales."),
		CHOQUE_DOCENTE("El docente ya tiene otra sesión que se cruza con esa franja."),
		CHOQUE_GRUPO("El grupo ya tiene otra sesión que se cruza con esa franja."),
		MISMO_DIA_CURSO("La sección ya tiene otra sesión ese mismo día."),
		SIN_AULA("No hay un aula compatible y libre en esa franja."),
		AULA_INCOMPATIBLE("El aula no es del tipo requerido o no tiene capacidad suficiente."),
		CHOQUE_AULA("El aula ya está ocupada en esa franja.");

		private final String texto;

		Motivo(String texto) {
			this.texto = texto;
		}
	}

	private static class Evaluacion {
		private final List<Motivo> motivos = new ArrayList<>();
		private Aula aula;
	}

	private final SesionHorarioRepository sesionRepo;
	private final AulaRepository aulaRepo;
	private final MotorHorarioService motorService;

	public HorarioService(SesionHorarioRepository sesionRepo, AulaRepository aulaRepo,
			MotorHorarioService motorService) {
		this.sesionRepo = sesionRepo;
		this.aulaRepo = aulaRepo;
		this.motorService = motorService;
	}

	// ---------- Consulta y generación ----------

	/**
	 * Devuelve el horario guardado, con filtros opcionales.
	 *
	 * @param docenteId filtra por docente, o {@code null}
	 * @param grupoId   filtra por grupo, o {@code null}
	 * @param aulaId    filtra por aula (solo sesiones ubicadas), o {@code null}
	 * @param cursoId   filtra por curso, o {@code null}
	 * @return sesiones ubicadas y sin ubicar
	 */
	public HorarioDto obtener(Long docenteId, Long grupoId, Long aulaId, Long cursoId) {
		List<SesionHorarioDto> ubicadas = new ArrayList<>();
		List<SesionHorarioDto> sinUbicar = new ArrayList<>();
		for (SesionHorario s : sesionRepo.findAll()) {
			Seccion sec = s.getSeccion();
			if (docenteId != null && !docenteId.equals(sec.getDocente().getId())) {
				continue;
			}
			if (grupoId != null && !grupoId.equals(sec.getGrupo().getId())) {
				continue;
			}
			if (cursoId != null && !cursoId.equals(sec.getCurso().getId())) {
				continue;
			}
			if (aulaId != null && (s.getAula() == null || !aulaId.equals(s.getAula().getId()))) {
				continue;
			}
			if (s.getDia() == null) {
				sinUbicar.add(aDto(s));
			} else {
				ubicadas.add(aDto(s));
			}
		}
		return new HorarioDto(ubicadas, sinUbicar);
	}

	/**
	 * Ejecuta el motor con los datos actuales, reemplaza el horario guardado por
	 * el resultado y lo devuelve.
	 *
	 * @return horario recién generado
	 */
	@Transactional
	public HorarioDto generar() {
		motorService.generarYGuardarHorario();
		return obtener(null, null, null, null);
	}

	// ---------- Drag and drop ----------

	/**
	 * Comprueba si una sesión puede ubicarse en el destino, sin aplicar el
	 * cambio. El front lo usa mientras se arrastra para marcar la celda.
	 *
	 * @param id  sesión que se arrastra
	 * @param req día, hora de inicio y aula opcional de destino
	 * @return si es válido, los motivos del rechazo y el aula que se asignaría
	 */
	public ValidacionMovimientoDto validarMovimiento(Long id, MoverSesionRequestDto req)
			throws EntidadNoEncontradaException, DatoInvalidoException {
		SesionHorario sesion = buscar(id);
		verificarDestino(req);
		Evaluacion ev = evaluar(sesion, req.dia(), req.horaInicio(), req.aulaId());
		return new ValidacionMovimientoDto(ev.motivos.isEmpty(), codigos(ev.motivos), textos(ev.motivos),
				ev.aula == null ? null : ev.aula.getId(), ev.aula == null ? null : ev.aula.getNombre());
	}

	/**
	 * Mueve una sesión a un nuevo día, hora y aula. Con día, hora y aula nulos la
	 * devuelve a "sin ubicar".
	 *
	 * @throws MovimientoInvalidoException si alguna regla se incumple (409)
	 */
	@Transactional(rollbackFor = Exception.class)
	public SesionHorarioDto mover(Long id, MoverSesionRequestDto req)
			throws EntidadNoEncontradaException, DatoInvalidoException, MovimientoInvalidoException {
		SesionHorario sesion = buscar(id);
		LanzadorException.verificar(req != null, "El cuerpo de la petición es obligatorio.");
		if (req.dia() == null && req.horaInicio() == null && req.aulaId() == null) {
			desubicarSesion(sesion, CAUSA_MANUAL);
			return aDto(sesionRepo.save(sesion));
		}
		verificarDestino(req);
		Evaluacion ev = evaluar(sesion, req.dia(), req.horaInicio(), req.aulaId());
		if (!ev.motivos.isEmpty()) {
			throw new MovimientoInvalidoException(textos(ev.motivos));
		}
		sesion.setDia(req.dia());
		sesion.setHoraInicio(req.horaInicio());
		sesion.setAula(ev.aula);
		sesion.setCausa(null);
		return aDto(sesionRepo.save(sesion));
	}

	/** Devuelve una sesión a la lista de "sin ubicar". */
	@Transactional(rollbackFor = Exception.class)
	public SesionHorarioDto desubicar(Long id) throws EntidadNoEncontradaException {
		SesionHorario sesion = buscar(id);
		desubicarSesion(sesion, CAUSA_MANUAL);
		return aDto(sesionRepo.save(sesion));
	}

	// ---------- Sincronización con otros cambios ----------

	/**
	 * Ajusta las sesiones guardadas de una sección a las sesiones semanales de su
	 * curso: crea las que falten (sin ubicar) y descarta las sobrantes
	 * (primero las no ubicadas).
	 *
	 * @param seccion        sección ya persistida
	 * @param desubicarTodas si es {@code true}, todas sus sesiones quedan sin
	 *                       ubicar (cambió el docente, el curso o el grupo)
	 */
	@Transactional
	public void sincronizarSeccion(Seccion seccion, boolean desubicarTodas) {
		List<SesionHorario> actuales = new ArrayList<>(sesionRepo.findBySeccionId(seccion.getId()));
		int objetivo = seccion.getCurso().getSesionesSemana();

		if (actuales.size() > objetivo) {
			actuales.sort(Comparator.comparingInt((SesionHorario s) -> s.getDia() == null ? 0 : 1));
			int sobrantes = actuales.size() - objetivo;
			List<SesionHorario> aBorrar = new ArrayList<>(actuales.subList(0, sobrantes));
			sesionRepo.deleteAll(aBorrar);
			actuales.removeAll(aBorrar);
		}
		while (actuales.size() < objetivo) {
			SesionHorario nueva = new SesionHorario();
			nueva.setSeccion(seccion);
			nueva.setCausa(CAUSA_PENDIENTE);
			actuales.add(nueva);
		}
		if (desubicarTodas) {
			for (SesionHorario s : actuales) {
				if (s.getDia() != null) {
					desubicarSesion(s, "SECCION_MODIFICADA");
				}
			}
		}
		sesionRepo.saveAll(actuales);
	}

	/** Elimina todas las sesiones guardadas de una sección. */
	@Transactional
	public void eliminarSesionesDeSeccion(Long seccionId) {
		sesionRepo.deleteBySeccionId(seccionId);
	}

	/** Devuelve a "sin ubicar" las sesiones de un aula a las que ya no les sirve. */
	@Transactional
	public void revisarAula(Aula aula) {
		List<SesionHorario> afectadas = new ArrayList<>();
		for (SesionHorario s : sesionRepo.findByAulaId(aula.getId())) {
			Seccion sec = s.getSeccion();
			if (!aulaCompatible(aula, sec.getCurso(), sec.getGrupo())) {
				desubicarSesion(s, "AULA_MODIFICADA");
				afectadas.add(s);
			}
		}
		sesionRepo.saveAll(afectadas);
	}

	/** Devuelve a "sin ubicar" las sesiones de un grupo que ya no caben en su aula. */
	@Transactional
	public void revisarGrupo(Grupo grupo) {
		List<SesionHorario> afectadas = new ArrayList<>();
		for (SesionHorario s : sesionRepo.findBySeccionGrupoId(grupo.getId())) {
			if (s.getAula() != null && !aulaCompatible(s.getAula(), s.getSeccion().getCurso(), grupo)) {
				desubicarSesion(s, "GRUPO_MODIFICADO");
				afectadas.add(s);
			}
		}
		sesionRepo.saveAll(afectadas);
	}

	// ---------- Reglas del movimiento ----------

	private Evaluacion evaluar(SesionHorario sesion, DayOfWeek dia, Integer hora, Long aulaId)
			throws EntidadNoEncontradaException {
		Seccion seccion = sesion.getSeccion();
		Curso curso = seccion.getCurso();
		Docente docente = seccion.getDocente();
		Grupo grupo = seccion.getGrupo();
		int duracion = curso.getDuracionHoras();
		Franja destino = new Franja(dia, hora);
		Evaluacion ev = new Evaluacion();

		if (!GeneradorFranjas.generar(duracion, PARAMS).contains(destino)) {
			ev.motivos.add(Motivo.FRANJA_INVALIDA);
		}
		if (!docenteDisponible(docente, destino, duracion)) {
			ev.motivos.add(Motivo.DOCENTE_NO_DISPONIBLE);
		}

		List<SesionHorario> otras = new ArrayList<>();
		for (SesionHorario o : sesionRepo.findAll()) {
			if (o.getDia() != null && !o.getId().equals(sesion.getId())) {
				otras.add(o);
			}
		}

		int horasDocente = 0;
		boolean choqueDocente = false;
		boolean choqueGrupo = false;
		boolean mismoDia = false;
		for (SesionHorario o : otras) {
			Seccion os = o.getSeccion();
			int duracionOtra = os.getCurso().getDuracionHoras();
			boolean traslapa = Franja.traslapan(destino, duracion, new Franja(o.getDia(), o.getHoraInicio()),
					duracionOtra);
			if (os.getDocente().getId().equals(docente.getId())) {
				horasDocente += duracionOtra;
				choqueDocente = choqueDocente || traslapa;
			}
			if (os.getGrupo().getId().equals(grupo.getId()) && traslapa) {
				choqueGrupo = true;
			}
			if (os.getId().equals(seccion.getId()) && o.getDia() == dia) {
				mismoDia = true;
			}
		}
		if (horasDocente + duracion > MotorHorarioService.topeHoras(docente.getDedicacion())) {
			ev.motivos.add(Motivo.CARGA_DOCENTE);
		}
		if (choqueDocente) {
			ev.motivos.add(Motivo.CHOQUE_DOCENTE);
		}
		if (choqueGrupo) {
			ev.motivos.add(Motivo.CHOQUE_GRUPO);
		}
		if (mismoDia) {
			ev.motivos.add(Motivo.MISMO_DIA_CURSO);
		}

		if (aulaId != null) {
			Aula aula = aulaRepo.findById(aulaId).orElse(null);
			LanzadorException.verificarEntidadExiste(aula, "Aula", aulaId);
			ev.aula = aula;
			if (!aulaCompatible(aula, curso, grupo)) {
				ev.motivos.add(Motivo.AULA_INCOMPATIBLE);
			} else if (aulaOcupada(aula, destino, duracion, otras)) {
				ev.motivos.add(Motivo.CHOQUE_AULA);
			}
		} else {
			List<Aula> aulas = new ArrayList<>(aulaRepo.findAll());
			aulas.sort(Comparator.comparingInt(Aula::getCapacidad));
			for (Aula a : aulas) {
				if (aulaCompatible(a, curso, grupo) && !aulaOcupada(a, destino, duracion, otras)) {
					ev.aula = a;
					break;
				}
			}
			if (ev.aula == null) {
				ev.motivos.add(Motivo.SIN_AULA);
			}
		}
		return ev;
	}

	private boolean docenteDisponible(Docente docente, Franja franja, int duracion) {
		if (docente.getFranjas().isEmpty()) {
			return true; // sin franjas declaradas = siempre disponible (igual que en el motor)
		}
		for (int h = franja.getHoraInicio(); h < franja.getHoraInicio() + duracion; h++) {
			boolean cubierta = false;
			for (FranjaDocente f : docente.getFranjas()) {
				if (f.getDia() == franja.getDia() && f.getHoraInicio() <= h && h < f.getHoraFin()) {
					cubierta = true;
					break;
				}
			}
			if (!cubierta) {
				return false;
			}
		}
		return true;
	}

	private boolean aulaCompatible(Aula aula, Curso curso, Grupo grupo) {
		return aula.getTipo() == curso.getTipoAula() && aula.getCapacidad() >= grupo.getEstudiantes();
	}

	private boolean aulaOcupada(Aula aula, Franja destino, int duracion, List<SesionHorario> otras) {
		for (SesionHorario o : otras) {
			if (o.getAula() != null && o.getAula().getId().equals(aula.getId())
					&& Franja.traslapan(destino, duracion, new Franja(o.getDia(), o.getHoraInicio()),
							o.getSeccion().getCurso().getDuracionHoras())) {
				return true;
			}
		}
		return false;
	}

	private void verificarDestino(MoverSesionRequestDto req) throws DatoInvalidoException {
		LanzadorException.verificar(req != null, "El cuerpo de la petición es obligatorio.");
		LanzadorException.verificar(req.dia() != null && req.horaInicio() != null,
				"Debes indicar el día y la hora de inicio de destino.");
	}

	// ---------- Utilidades ----------

	private SesionHorario buscar(Long id) throws EntidadNoEncontradaException {
		SesionHorario sesion = sesionRepo.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(sesion, "Sesión", id);
		return sesion;
	}

	private void desubicarSesion(SesionHorario s, String causa) {
		s.setDia(null);
		s.setHoraInicio(null);
		s.setAula(null);
		s.setCausa(causa);
	}

	private List<String> codigos(List<Motivo> motivos) {
		return motivos.stream().map(Motivo::name).toList();
	}

	private List<String> textos(List<Motivo> motivos) {
		return motivos.stream().map(m -> m.texto).toList();
	}

	private SesionHorarioDto aDto(SesionHorario s) {
		Seccion sec = s.getSeccion();
		return new SesionHorarioDto(s.getId(), sec.getId(), sec.getCurso().getNombre(), sec.getDocente().getId(),
				sec.getDocente().getNombre(), sec.getGrupo().getId(), sec.getGrupo().getNombre(), s.getDia(),
				s.getHoraInicio(), sec.getCurso().getDuracionHoras(), s.getAula() == null ? null : s.getAula().getId(),
				s.getAula() == null ? null : s.getAula().getNombre(), s.getCausa());
	}
}
