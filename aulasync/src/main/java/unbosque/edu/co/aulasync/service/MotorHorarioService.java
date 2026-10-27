package unbosque.edu.co.aulasync.service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.RespuestaDemoDto;
import unbosque.edu.co.aulasync.dto.RespuestaGeneracionDto;
import unbosque.edu.co.aulasync.dto.SesionDemoDto;
import unbosque.edu.co.aulasync.dto.SesionGeneradaDto;
import unbosque.edu.co.aulasync.model.Aula;
import unbosque.edu.co.aulasync.model.Curso;
import unbosque.edu.co.aulasync.model.Docente;
import unbosque.edu.co.aulasync.model.FranjaDocente;
import unbosque.edu.co.aulasync.model.Seccion;
import unbosque.edu.co.aulasync.model.SesionHorario;
import unbosque.edu.co.aulasync.motor.AulaMotor;
import unbosque.edu.co.aulasync.motor.DocenteMotor;
import unbosque.edu.co.aulasync.motor.GeneradorFranjas;
import unbosque.edu.co.aulasync.motor.MotorHorario;
import unbosque.edu.co.aulasync.motor.Sesion;
import unbosque.edu.co.aulasync.motor.TipoAula;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.SeccionRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * Servicio que conecta la persistencia con el motor de generación de horarios.
 *
 * <p>
 * Lee las secciones, docentes y aulas de la base de datos, las traduce a los
 * objetos del motor ({@link Sesion}, {@link DocenteMotor}, {@link AulaMotor}),
 * ejecuta {@link MotorHorario} y convierte el resultado en DTOs para el
 * controlador. El motor no conoce Spring ni JPA: este servicio es el único
 * puente entre ambos mundos.
 * </p>
 */
@Service
public class MotorHorarioService {

	private static final int TOPE_TIEMPO_COMPLETO = 40;
	private static final int TOPE_TRES_CUARTOS = 30;
	private static final int TOPE_MEDIO_TIEMPO = 20;
	private static final int TOPE_UN_CUARTO = 10;

	private final SeccionRepository seccionRepo;
	private final DocenteRepository docenteRepo;
	private final AulaRepository aulaRepo;
	private final SesionHorarioRepository sesionHorarioRepo;

	/**
	 * Crea el servicio con los repositorios que necesita.
	 *
	 * @param seccionRepo repositorio de secciones
	 * @param docenteRepo repositorio de docentes
	 * @param aulaRepo    repositorio de aulas
	 * @param sesionHorarioRepo repositorio del horario guardado
	 */
	public MotorHorarioService(SeccionRepository seccionRepo, DocenteRepository docenteRepo, AulaRepository aulaRepo,
			SesionHorarioRepository sesionHorarioRepo) {
		this.seccionRepo = seccionRepo;
		this.docenteRepo = docenteRepo;
		this.aulaRepo = aulaRepo;
		this.sesionHorarioRepo = sesionHorarioRepo;
	}

	/**
	 * Ejecuta el motor con un conjunto fijo de siete sesiones en memoria (el caso
	 * del informe de ingeniería). No usa la base de datos; sirve para comprobar que
	 * el motor y el servidor funcionan.
	 *
	 * @return sesiones ubicadas y sin ubicar del caso de demostración
	 */
	public RespuestaDemoDto generarDemo() {
		List<Sesion> sesiones = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(2, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(3, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(4, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(5, 3, 2, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(6, 3, 2, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(7, 4, 2, 20, 4, TipoAula.SALA_COMPUTO, 44));

		Map<Integer, DocenteMotor> docentes = Map.of(1, new DocenteMotor(1, TOPE_TIEMPO_COMPLETO, null), 2,
				new DocenteMotor(2, TOPE_TIEMPO_COMPLETO, null));
		List<AulaMotor> aulas = List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL),
				new AulaMotor(2, 45, TipoAula.SALA_COMPUTO));

		MotorHorario.Resultado r = new MotorHorario(sesiones, docentes, aulas, GeneradorFranjas.Parametros.POR_DEFECTO)
				.generar();

		List<SesionDemoDto> ubicadas = new ArrayList<>();
		for (Sesion s : r.getUbicadas()) {
			ubicadas.add(aDemoDto(s));
		}

		List<SesionDemoDto> sinUbicar = new ArrayList<>();
		for (Sesion s : r.getSinUbicar()) {
			sinUbicar.add(aDemoDto(s));
		}

		return new RespuestaDemoDto(ubicadas, sinUbicar);
	}

	/**
	 * Genera el horario a partir de los datos maestros guardados en la base de
	 * datos.
	 *
	 * <p>
	 * Crea una sesión por cada sesión semanal de cada sección, aplica la
	 * disponibilidad y el tope de horas de cada docente según su dedicación, usa el
	 * número de estudiantes del grupo como capacidad requerida del aula, y ejecuta
	 * el motor.
	 * </p>
	 *
	 * @return sesiones ubicadas y sin ubicar, estas últimas con causa y desglose
	 */
	@Transactional(readOnly = true)
	public RespuestaGeneracionDto generarHorario() {
		Map<Integer, Seccion> seccionPorClave = new HashMap<>();
		List<Sesion> sesiones = construirSesiones(seccionPorClave);

		Map<Integer, String> nombreDocente = new HashMap<>();
		Map<Integer, DocenteMotor> docentes = construirDocentes(nombreDocente);

		Map<Integer, String> nombreAula = new HashMap<>();
		List<AulaMotor> aulas = construirAulas(nombreAula);

		MotorHorario.Resultado r = new MotorHorario(sesiones, docentes, aulas, GeneradorFranjas.Parametros.POR_DEFECTO)
				.generar();

		List<SesionGeneradaDto> ubicadas = new ArrayList<>();
		for (Sesion s : r.getUbicadas()) {
			ubicadas.add(aGeneradaDto(s, seccionPorClave, nombreDocente, nombreAula));
		}

		List<SesionGeneradaDto> sinUbicar = new ArrayList<>();
		for (Sesion s : r.getSinUbicar()) {
			sinUbicar.add(aGeneradaDto(s, seccionPorClave, nombreDocente, nombreAula));
		}

		return new RespuestaGeneracionDto(ubicadas, sinUbicar);
	}

	/**
	 * Ejecuta el motor igual que {@link #generarHorario()} pero guarda el
	 * resultado: reemplaza el horario guardado por una sesión persistida por cada
	 * sesión semanal de cada sección, ubicada o no. Sobre ese horario guardado
	 * trabaja el Comité con drag and drop.
	 */
	@Transactional
	public void generarYGuardarHorario() {
		Map<Integer, Seccion> seccionPorClave = new HashMap<>();
		List<Sesion> sesiones = construirSesiones(seccionPorClave);
		Map<Integer, DocenteMotor> docentes = construirDocentes(new HashMap<>());
		List<AulaMotor> aulas = construirAulas(new HashMap<>());

		MotorHorario.Resultado r = new MotorHorario(sesiones, docentes, aulas, GeneradorFranjas.Parametros.POR_DEFECTO)
				.generar();

		Map<Long, Aula> aulaPorId = aulaRepo.findAll().stream().collect(Collectors.toMap(Aula::getId, a -> a));

		List<SesionHorario> nuevas = new ArrayList<>();
		for (Sesion s : r.getUbicadas()) {
			SesionHorario sh = new SesionHorario();
			sh.setSeccion(seccionPorClave.get(s.getClaveCurso()));
			sh.setDia(s.getFranja().getDia());
			sh.setHoraInicio(s.getFranja().getHoraInicio());
			sh.setAula(aulaPorId.get((long) s.getAula().getId()));
			nuevas.add(sh);
		}
		for (Sesion s : r.getSinUbicar()) {
			SesionHorario sh = new SesionHorario();
			sh.setSeccion(seccionPorClave.get(s.getClaveCurso()));
			sh.setCausa(s.getCausaSinUbicar() == null ? "SIN_UBICAR" : s.getCausaSinUbicar().name());
			nuevas.add(sh);
		}

		sesionHorarioRepo.deleteAll();
		sesionHorarioRepo.flush();
		sesionHorarioRepo.saveAll(nuevas);
	}

	// ---------- Construcción de objetos del motor ----------

	/**
	 * Crea las sesiones del motor: una por cada sesión semanal de cada sección.
	 * Todas las sesiones de una misma sección comparten la clave de curso (el
	 * identificador de la sección), que el motor usa para impedir dos sesiones del
	 * mismo curso el mismo día.
	 *
	 * @param seccionPorClave mapa que se llena con la sección correspondiente a
	 *                        cada clave
	 * @return lista de sesiones por ubicar
	 */
	private List<Sesion> construirSesiones(Map<Integer, Seccion> seccionPorClave) {
		List<Sesion> sesiones = new ArrayList<>();
		int idSesion = 1;
		for (Seccion sec : seccionRepo.findAll()) {
			Curso curso = sec.getCurso();
			seccionPorClave.put(sec.getId().intValue(), sec);
			for (int k = 0; k < curso.getSesionesSemana(); k++) {
				sesiones.add(new Sesion(idSesion++, sec.getId().intValue(), sec.getDocente().getId().intValue(),
						sec.getGrupo().getId().intValue(), curso.getDuracionHoras(), curso.getTipoAula(),
						sec.getGrupo().getEstudiantes()));
			}
		}
		return sesiones;
	}

	/**
	 * Convierte los docentes persistidos en docentes del motor, con su tope de
	 * horas semanales según la dedicación y su disponibilidad por día y hora. Un
	 * docente sin franjas declaradas se considera siempre disponible.
	 *
	 * @param nombreDocente mapa que se llena con el nombre de cada docente por
	 *                      identificador
	 * @return docentes del motor, indexados por identificador
	 */
	private Map<Integer, DocenteMotor> construirDocentes(Map<Integer, String> nombreDocente) {
		Map<Integer, DocenteMotor> docentes = new HashMap<>();
		for (Docente d : docenteRepo.findAll()) {
			Map<DayOfWeek, Set<Integer>> disponibilidad = null;
			if (!d.getFranjas().isEmpty()) {
				disponibilidad = new EnumMap<>(DayOfWeek.class);
				for (FranjaDocente f : d.getFranjas()) {
					Set<Integer> horas = disponibilidad.get(f.getDia());
					if (horas == null) {
						horas = new HashSet<>();
						disponibilidad.put(f.getDia(), horas);
					}
					for (int h = f.getHoraInicio(); h < f.getHoraFin(); h++) {
						horas.add(h);
					}
				}
			}
			docentes.put(d.getId().intValue(),
					new DocenteMotor(d.getId().intValue(), topeHoras(d.getDedicacion()), disponibilidad));
			nombreDocente.put(d.getId().intValue(), d.getNombre());
		}
		return docentes;
	}

	/**
	 * Convierte las aulas persistidas en aulas del motor.
	 *
	 * @param nombreAula mapa que se llena con el nombre de cada aula por
	 *                   identificador
	 * @return aulas del motor
	 */
	private List<AulaMotor> construirAulas(Map<Integer, String> nombreAula) {
		List<AulaMotor> aulas = new ArrayList<>();
		for (Aula a : aulaRepo.findAll()) {
			aulas.add(new AulaMotor(a.getId().intValue(), a.getCapacidad(), a.getTipo()));
			nombreAula.put(a.getId().intValue(), a.getNombre());
		}
		return aulas;
	}

	/**
	 * Traduce el tipo de dedicación de un docente a su tope de horas de clase por
	 * semana.
	 *
	 * @param dedicacion {@code TIEMPO_COMPLETO}, {@code TRES_CUARTOS},
	 *                   {@code MEDIO_TIEMPO} o {@code UN_CUARTO}
	 * @return tope de horas semanales; si la dedicación es nula o desconocida, el
	 *         de un cuarto de tiempo
	 */
	public static int topeHoras(String dedicacion) {
		if (dedicacion == null) {
			return TOPE_UN_CUARTO;
		}
		if (dedicacion.equals("TIEMPO_COMPLETO")) {
			return TOPE_TIEMPO_COMPLETO;
		}
		if (dedicacion.equals("TRES_CUARTOS")) {
			return TOPE_TRES_CUARTOS;
		}
		if (dedicacion.equals("MEDIO_TIEMPO")) {
			return TOPE_MEDIO_TIEMPO;
		}
		return TOPE_UN_CUARTO;
	}

	// ---------- Conversión a DTO ----------

	/**
	 * Convierte una sesión del motor en el DTO de la demostración.
	 *
	 * @param s sesión del motor
	 * @return DTO con identificadores numéricos
	 */
	private SesionDemoDto aDemoDto(Sesion s) {
		return new SesionDemoDto(s.getId(), s.getClaveCurso(),
				s.getFranja() == null ? null : s.getFranja().getDia().toString(),
				s.getFranja() == null ? null : s.getFranja().getHoraInicio(),
				s.getAula() == null ? null : s.getAula().getId(),
				s.getCausaSinUbicar() == null ? null : s.getCausaSinUbicar().name());
	}

	/**
	 * Convierte una sesión del motor en el DTO del horario generado, reemplazando
	 * identificadores por nombres legibles.
	 *
	 * @param s               sesión del motor
	 * @param seccionPorClave secciones indexadas por la clave de curso de la sesión
	 * @param nombreDocente   nombres de docente por identificador
	 * @param nombreAula      nombres de aula por identificador
	 * @return DTO con nombres, franja, aula, causa y desglose
	 */
	private SesionGeneradaDto aGeneradaDto(Sesion s, Map<Integer, Seccion> seccionPorClave,
			Map<Integer, String> nombreDocente, Map<Integer, String> nombreAula) {
		Seccion sec = seccionPorClave.get(s.getClaveCurso());

		Map<String, Integer> desglose = new LinkedHashMap<>();
		for (Map.Entry<MotorHorario.Motivo, Integer> e : s.getDesgloseCausas().entrySet()) {
			desglose.put(e.getKey().name(), e.getValue());
		}

		return new SesionGeneradaDto(s.getId(), sec.getCurso().getNombre(), nombreDocente.get(s.getDocenteId()),
				sec.getGrupo().getNombre(), s.getFranja() == null ? null : s.getFranja().getDia().toString(),
				s.getFranja() == null ? null : s.getFranja().getHoraInicio(), s.getDuracionHoras(),
				s.getAula() == null ? null : nombreAula.get(s.getAula().getId()),
				s.getCausaSinUbicar() == null ? null : s.getCausaSinUbicar().name(), desglose);
	}
}