package unbosque.edu.co.aulasync.controller;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.model.Aula;
import unbosque.edu.co.aulasync.model.Curso;
import unbosque.edu.co.aulasync.model.Docente;
import unbosque.edu.co.aulasync.model.FranjaDocente;
import unbosque.edu.co.aulasync.model.Seccion;
import unbosque.edu.co.aulasync.motor.AulaMotor;
import unbosque.edu.co.aulasync.motor.DocenteMotor;
import unbosque.edu.co.aulasync.motor.GeneradorFranjas;
import unbosque.edu.co.aulasync.motor.MotorHorario;
import unbosque.edu.co.aulasync.motor.Sesion;
import unbosque.edu.co.aulasync.motor.TipoAula;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.SeccionRepository;

@RestController
@RequestMapping("/api/motor")
public class MotorHorarioController {

	// ---------- DTOs ----------

	public record SesionDto(int id, int curso, String dia, Integer horaInicio, Integer aula, String causa) {
	}

	public record RespuestaMotor(List<SesionDto> ubicadas, List<SesionDto> sinUbicar) {
	}

	public record SesionGenDto(int id, String curso, String docente, String grupo, String dia, Integer horaInicio,
			int duracion, String aula, String causa, Map<String, Integer> desglose) {
	}

	public record RespuestaGeneracion(List<SesionGenDto> ubicadas, List<SesionGenDto> sinUbicar) {
	}

	// ---------- Dependencias ----------

	private final SeccionRepository seccionRepo;
	private final DocenteRepository docenteRepo;
	private final AulaRepository aulaRepo;

	public MotorHorarioController(SeccionRepository seccionRepo, DocenteRepository docenteRepo,
			AulaRepository aulaRepo) {
		this.seccionRepo = seccionRepo;
		this.docenteRepo = docenteRepo;
		this.aulaRepo = aulaRepo;
	}

	// ---------- Demo con datos fijos (sin BD) ----------

	@GetMapping("/demo")
	public RespuestaMotor demo() {
		List<Sesion> ses = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(2, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(3, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(4, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(5, 3, 2, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(6, 3, 2, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(7, 4, 2, 20, 4, TipoAula.SALA_COMPUTO, 44));

		var r = new MotorHorario(ses, Map.of(1, new DocenteMotor(1, 40, null), 2, new DocenteMotor(2, 40, null)),
				List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL), new AulaMotor(2, 45, TipoAula.SALA_COMPUTO)),
				GeneradorFranjas.Parametros.POR_DEFECTO).generar();

		return new RespuestaMotor(r.ubicadas().stream().map(this::dto).toList(),
				r.sinUbicar().stream().map(this::dto).toList());
	}

	// ---------- Generación con datos de la BD ----------

	@PostMapping("/generar")
	@Transactional(readOnly = true)
	public RespuestaGeneracion generar() {
		// 1. Sesiones: una por cada sesión semanal de cada sección
		List<Sesion> sesiones = new ArrayList<>();
		Map<Integer, Seccion> porClave = new HashMap<>();
		int idSesion = 1;
		for (Seccion sec : seccionRepo.findAll()) {
			Curso c = sec.getCurso();
			porClave.put(sec.getId().intValue(), sec);
			for (int k = 0; k < c.getSesionesSemana(); k++) {
				sesiones.add(new Sesion(idSesion++, sec.getId().intValue(), sec.getDocente().getId().intValue(),
						sec.getGrupo().getId().intValue(), c.getDuracionHoras(), c.getTipoAula(),
						sec.getGrupo().getEstudiantes()));
			}
		}

		// 2. Docentes del motor (tope por dedicación + disponibilidad)
		Map<Integer, DocenteMotor> docentes = new HashMap<>();
		Map<Integer, String> nombreDocente = new HashMap<>();
		for (Docente d : docenteRepo.findAll()) {
			Map<DayOfWeek, Set<Integer>> disp = null;
			if (!d.getFranjas().isEmpty()) {
				disp = new EnumMap<>(DayOfWeek.class);
				for (FranjaDocente f : d.getFranjas()) {
					Set<Integer> horas = disp.computeIfAbsent(f.getDia(), x -> new HashSet<>());
					for (int h = f.getHoraInicio(); h < f.getHoraFin(); h++)
						horas.add(h);
				}
			}
			docentes.put(d.getId().intValue(),
					new DocenteMotor(d.getId().intValue(), topeHoras(d.getDedicacion()), disp));
			nombreDocente.put(d.getId().intValue(), d.getNombre());
		}

		// 3. Aulas del motor
		Map<Integer, String> nombreAula = new HashMap<>();
		List<AulaMotor> aulas = new ArrayList<>();
		for (Aula a : aulaRepo.findAll()) {
			aulas.add(new AulaMotor(a.getId().intValue(), a.getCapacidad(), a.getTipo()));
			nombreAula.put(a.getId().intValue(), a.getNombre());
		}

		// 4. Ejecutar el motor
		var r = new MotorHorario(sesiones, docentes, aulas, GeneradorFranjas.Parametros.POR_DEFECTO).generar();

		return new RespuestaGeneracion(
				r.ubicadas().stream().map(s -> dto(s, porClave, nombreDocente, nombreAula)).toList(),
				r.sinUbicar().stream().map(s -> dto(s, porClave, nombreDocente, nombreAula)).toList());
	}

	// ---------- Utilidades ----------

	private int topeHoras(String dedicacion) {
		if (dedicacion == null)
			return 10;
		return switch (dedicacion) {
		case "TIEMPO_COMPLETO" -> 40;
		case "TRES_CUARTOS" -> 30;
		case "MEDIO_TIEMPO" -> 20;
		default -> 10; // UN_CUARTO
		};
	}

	private SesionDto dto(Sesion s) {
		return new SesionDto(s.getId(), s.getClaveCurso(),
				s.getFranja() == null ? null : s.getFranja().dia().toString(),
				s.getFranja() == null ? null : s.getFranja().horaInicio(),
				s.getAula() == null ? null : s.getAula().getId(),
				s.getCausaSinUbicar() == null ? null : s.getCausaSinUbicar().name());
	}

	private SesionGenDto dto(Sesion s, Map<Integer, Seccion> porClave, Map<Integer, String> docentes,
			Map<Integer, String> aulas) {
		Seccion sec = porClave.get(s.getClaveCurso());

		Map<String, Integer> desglose = new LinkedHashMap<>();
		s.getDesgloseCausas().forEach((k, v) -> desglose.put(k.name(), v));

		return new SesionGenDto(s.getId(), sec.getCurso().getNombre(), docentes.get(s.getDocenteId()),
				sec.getGrupo().getNombre(), s.getFranja() == null ? null : s.getFranja().dia().toString(),
				s.getFranja() == null ? null : s.getFranja().horaInicio(), s.getDuracionHoras(),
				s.getAula() == null ? null : aulas.get(s.getAula().getId()),
				s.getCausaSinUbicar() == null ? null : s.getCausaSinUbicar().name(), desglose);
	}
}