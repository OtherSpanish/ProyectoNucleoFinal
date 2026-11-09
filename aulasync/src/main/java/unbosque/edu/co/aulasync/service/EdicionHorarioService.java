package unbosque.edu.co.aulasync.service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.SesionHorarioDTO;
import unbosque.edu.co.aulasync.entity.Aula;
import unbosque.edu.co.aulasync.entity.Curso;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.FranjaDocente;
import unbosque.edu.co.aulasync.entity.FranjaSede;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.entity.Sede;
import unbosque.edu.co.aulasync.entity.SesionHorario;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.MovimientoInvalidoException;
import unbosque.edu.co.aulasync.model.JornadaInstitucional;
import unbosque.edu.co.aulasync.model.ValidacionMovimiento;
import unbosque.edu.co.aulasync.motor.AulaMotor;
import unbosque.edu.co.aulasync.motor.DocenteMotor;
import unbosque.edu.co.aulasync.motor.Franja;
import unbosque.edu.co.aulasync.motor.MotivoRechazo;
import unbosque.edu.co.aulasync.motor.ReglasHorario;
import unbosque.edu.co.aulasync.motor.SedeMotor;
import unbosque.edu.co.aulasync.motor.Sesion;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.SedeRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * Edición manual del horario guardado (drag and drop del comité).
 *
 * <p>
 * Valida un movimiento con las mismas reglas del motor
 * ({@link ReglasHorario}) más cuatro propias de la edición manual (franja de
 * la sede, aula disponible, aula compatible y aula ocupada) y reporta todos
 * los motivos que fallan. Solo aplica el movimiento si es válido. La
 * sincronización usa la misma evaluación para revalidar sesiones ya ubicadas.
 * </p>
 *
 * @version 1.1
 */
@Service
public class EdicionHorarioService {

	/** Causa que se guarda cuando el comité retira una sesión del horario. */
	public static final String CAUSA_DESUBICADA = "DESUBICADA_MANUALMENTE";

	private static final String ENTIDAD = "Sesión de horario";

	private final SesionHorarioRepository sesionHorarioRepository;
	private final AulaRepository aulaRepository;
	private final SedeRepository sedeRepository;
	private final DisponibilidadService disponibilidadService;
	private final HorarioService horarioService;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param sesionHorarioRepository acceso a las sesiones guardadas.
	 * @param aulaRepository          lectura de aulas.
	 * @param sedeRepository          lectura de sedes y sus franjas.
	 * @param disponibilidadService   disponibilidad de los docentes.
	 * @param horarioService          conversión de sesiones a DTO.
	 */
	public EdicionHorarioService(SesionHorarioRepository sesionHorarioRepository, AulaRepository aulaRepository,
			SedeRepository sedeRepository, DisponibilidadService disponibilidadService,
			HorarioService horarioService) {
		this.sesionHorarioRepository = sesionHorarioRepository;
		this.aulaRepository = aulaRepository;
		this.sedeRepository = sedeRepository;
		this.disponibilidadService = disponibilidadService;
		this.horarioService = horarioService;
	}

	/**
	 * Valida un movimiento sin aplicarlo.
	 *
	 * @param sesionId id de la sesión.
	 * @param destino  {@code dia}, {@code horaInicio} y {@code aulaId} destino.
	 * @return si es válido y todos los motivos que fallan.
	 * @throws EntidadNoEncontradaException si la sesión o el aula no existen.
	 * @throws CampoObligatorioException    si falta día, hora o aula.
	 */
	@Transactional(readOnly = true)
	public ValidacionMovimiento validar(Long sesionId, SesionHorarioDTO destino)
			throws EntidadNoEncontradaException, CampoObligatorioException {
		SesionHorario sesion = obtenerSesion(sesionId);
		Aula aula = obtenerAulaDestino(destino);
		return evaluar(sesion, destino.getDia(), destino.getHoraInicio(), aula);
	}

	/**
	 * Revalida una sesión ya ubicada en su posición actual (día, hora y aula),
	 * ignorándose a sí misma. Lo usa la sincronización del horario.
	 *
	 * @param sesion sesión ubicada.
	 * @return si sigue siendo válida y los motivos que fallan.
	 */
	@Transactional(readOnly = true)
	public ValidacionMovimiento evaluarUbicacionActual(SesionHorario sesion) {
		return evaluar(sesion, sesion.getDia(), sesion.getHoraInicio(), sesion.getAula());
	}

	/**
	 * Mueve una sesión al día, hora y aula indicados, si cumple todas las
	 * reglas.
	 *
	 * @param sesionId id de la sesión.
	 * @param destino  {@code dia}, {@code horaInicio} y {@code aulaId} destino.
	 * @return la sesión actualizada.
	 * @throws EntidadNoEncontradaException si la sesión o el aula no existen.
	 * @throws CampoObligatorioException    si falta día, hora o aula.
	 * @throws MovimientoInvalidoException  si incumple alguna regla.
	 */
	@Transactional
	public SesionHorarioDTO mover(Long sesionId, SesionHorarioDTO destino)
			throws EntidadNoEncontradaException, CampoObligatorioException, MovimientoInvalidoException {
		SesionHorario sesion = obtenerSesion(sesionId);
		Aula aula = obtenerAulaDestino(destino);
		ValidacionMovimiento validacion = evaluar(sesion, destino.getDia(), destino.getHoraInicio(), aula);
		LanzadorException.verificarMovimientoValido(validacion.isValido(), validacion.getMensajes());

		sesion.setDia(destino.getDia());
		sesion.setHoraInicio(destino.getHoraInicio());
		sesion.setAula(aula);
		sesion.setCausa(null);
		sesion.setDetalleCausa(null);
		return horarioService.convertirADTO(sesionHorarioRepository.save(sesion));
	}

	/**
	 * Devuelve una sesión a "sin ubicar".
	 *
	 * @param sesionId id de la sesión.
	 * @return la sesión actualizada.
	 * @throws EntidadNoEncontradaException si la sesión no existe.
	 */
	@Transactional
	public SesionHorarioDTO desubicar(Long sesionId) throws EntidadNoEncontradaException {
		SesionHorario sesion = obtenerSesion(sesionId);
		sesion.setDia(null);
		sesion.setHoraInicio(null);
		sesion.setAula(null);
		sesion.setCausa(CAUSA_DESUBICADA);
		sesion.setDetalleCausa("Retirada del horario por el comité táctico.");
		return horarioService.convertirADTO(sesionHorarioRepository.save(sesion));
	}

	// ------------------------------------------------------------------
	// Evaluación de reglas
	// ------------------------------------------------------------------

	private ValidacionMovimiento evaluar(SesionHorario entidad, DayOfWeek dia, int horaInicio, Aula aula) {
		Set<MotivoRechazo> motivos = EnumSet.noneOf(MotivoRechazo.class);
		Sesion movida = aSesionMotor(entidad, false);
		Franja franja = new Franja(dia, horaInicio, aula.getSede().getId());
		int duracion = movida.getDuracionHoras();

		List<SedeMotor> sedes = construirSedes();
		SedeMotor sedeAula = null;
		for (SedeMotor sede : sedes) {
			if (sede.getId() == aula.getSede().getId()) {
				sedeAula = sede;
			}
		}
		if (!JornadaInstitucional.esDiaHabil(dia) || sedeAula == null
				|| !sedeAula.permite(dia, horaInicio, duracion)) {
			motivos.add(MotivoRechazo.FUERA_DE_FRANJA_SEDE);
		}

		if (!aula.isDisponible()) {
			motivos.add(MotivoRechazo.AULA_NO_DISPONIBLE);
		}
		AulaMotor aulaMotor = new AulaMotor(aula.getId(), aula.getNombre(), aula.getCapacidad(), aula.getTipo(),
				aula.getSede().getId());
		if (!ReglasHorario.aulaCompatible(aulaMotor, movida, franja)) {
			motivos.add(MotivoRechazo.AULA_INCOMPATIBLE);
		}

		Docente docente = entidad.getGrupo().getDocente();
		DocenteMotor docenteMotor = new DocenteMotor(docente.getId(), docente.getDedicacion().getHorasMaximas());
		for (FranjaDocente franjaDocente : disponibilidadService.listarEntidades(docente.getId())) {
			docenteMotor.habilitar(franjaDocente.getDia(), franjaDocente.getHoraInicio(), franjaDocente.getHoraFin());
		}
		if (!docenteMotor.estaDisponibleEn(franja, duracion)) {
			motivos.add(MotivoRechazo.DOCENTE_NO_DISPONIBLE);
		}

		List<Sesion> otras = new ArrayList<>();
		List<Sesion> enLaMismaAula = new ArrayList<>();
		int horasDocente = 0;
		for (SesionHorario otraEntidad : sesionHorarioRepository.findAll()) {
			if (otraEntidad.getId().equals(entidad.getId()) || !estaUbicada(otraEntidad)) {
				continue;
			}
			Sesion otra = aSesionMotor(otraEntidad, true);
			otras.add(otra);
			if (otra.getDocenteId() == movida.getDocenteId()) {
				horasDocente = horasDocente + otra.getDuracionHoras();
			}
			if (otraEntidad.getAula().getId().equals(aula.getId())) {
				enLaMismaAula.add(otra);
			}
		}
		if (!ReglasHorario.cargaPermitida(horasDocente, duracion, docenteMotor.getTopeHorasSemanales())) {
			motivos.add(MotivoRechazo.CARGA_DOCENTE);
		}

		SedeMotor sedePrimerSemestre = ReglasHorario.buscarSedePrimerSemestre(sedes);
		List<DayOfWeek> diasDelBloque = Collections.emptyList();
		if (sedePrimerSemestre != null) {
			diasDelBloque = ReglasHorario.calcularDiasPrimerSemestre(movida.getNumeroGrupo(),
					sedePrimerSemestre.getDiasPrimerSemestre());
		}
		if (sedeAula != null
				&& !ReglasHorario.sedePermitida(movida, sedeAula, dia, sedePrimerSemestre, diasDelBloque)) {
			motivos.add(MotivoRechazo.SEDE_NO_PERMITIDA);
		}

		for (Sesion otra : otras) {
			MotivoRechazo choque = ReglasHorario.motivoChoque(movida, franja, otra);
			if (choque != null) {
				motivos.add(choque);
			}
			if (ReglasHorario.mismoDiaDelGrupo(movida, franja, otra)) {
				motivos.add(MotivoRechazo.MISMO_DIA_GRUPO);
			}
		}
		if (!ReglasHorario.aulaLibre(movida, franja, enLaMismaAula)) {
			motivos.add(MotivoRechazo.AULA_OCUPADA);
		}

		List<MotivoRechazo> lista = new ArrayList<>(motivos);
		List<String> mensajes = new ArrayList<>();
		for (MotivoRechazo motivo : lista) {
			mensajes.add(motivo.getDescripcion());
		}
		return new ValidacionMovimiento(entidad.getId(), lista, mensajes);
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private SesionHorario obtenerSesion(Long id) throws EntidadNoEncontradaException {
		SesionHorario sesion = sesionHorarioRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(sesion, ENTIDAD, id);
		return sesion;
	}

	private Aula obtenerAulaDestino(SesionHorarioDTO destino)
			throws CampoObligatorioException, EntidadNoEncontradaException {
		LanzadorException.verificarObjetoObligatorio(destino.getDia(), "dia");
		LanzadorException.verificarObjetoObligatorio(destino.getHoraInicio(), "horaInicio");
		LanzadorException.verificarObjetoObligatorio(destino.getAulaId(), "aulaId");
		Aula aula = aulaRepository.findById(destino.getAulaId()).orElse(null);
		LanzadorException.verificarEntidadExiste(aula, "Aula", destino.getAulaId());
		return aula;
	}

	private boolean estaUbicada(SesionHorario sesion) {
		return sesion.getDia() != null && sesion.getHoraInicio() != null && sesion.getAula() != null;
	}

	private Sesion aSesionMotor(SesionHorario entidad, boolean conUbicacion) {
		Grupo grupo = entidad.getGrupo();
		Curso curso = grupo.getCurso();
		Sesion sesion = new Sesion(grupo.getId(), entidad.getNumeroSesion(), curso.getId(), curso.getCodigo(),
				grupo.getNumero(), curso.getSemestre().getNumero(), curso.isNucleoComun(), grupo.getDocente().getId(),
				curso.getIntensidad().getDuracionHoras(), curso.getTipoAula(), grupo.getEstudiantes().size());
		if (conUbicacion) {
			Aula aula = entidad.getAula();
			sesion.setFranja(new Franja(entidad.getDia(), entidad.getHoraInicio(), aula.getSede().getId()));
			sesion.setAula(new AulaMotor(aula.getId(), aula.getNombre(), aula.getCapacidad(), aula.getTipo(),
					aula.getSede().getId()));
		}
		return sesion;
	}

	private List<SedeMotor> construirSedes() {
		List<SedeMotor> sedes = new ArrayList<>();
		for (Sede sede : sedeRepository.findAllByOrderByNombreAsc()) {
			SedeMotor motor = new SedeMotor(sede.getId(), sede.getNombre(), sede.isExclusivaPrimerSemestre(),
					sede.getDiasPrimerSemestre());
			for (FranjaSede franja : sede.getFranjas()) {
				motor.habilitar(franja.getDia(), franja.getHoraInicio(), franja.getHoraFin());
			}
			sedes.add(motor);
		}
		return sedes;
	}
}
