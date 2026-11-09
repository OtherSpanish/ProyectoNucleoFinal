package unbosque.edu.co.aulasync.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.SesionHorarioDTO;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.model.CalendarioSemanal;
import unbosque.edu.co.aulasync.model.ResultadoGeneracion;
import unbosque.edu.co.aulasync.model.ResultadoSincronizacion;
import unbosque.edu.co.aulasync.service.HorarioService;
import unbosque.edu.co.aulasync.service.SincronizacionHorarioService;

/**
 * Horario guardado.
 *
 * <ul>
 * <li>Generar, guardar y sincronizar: solo {@code COMITE}.</li>
 * <li>Consultar (lista o calendario): cualquier usuario autenticado.</li>
 * </ul>
 *
 * @version 1.2
 */
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

	private final HorarioService horarioService;
	private final SincronizacionHorarioService sincronizacionHorarioService;

	/**
	 * Crea el controlador.
	 *
	 * @param horarioService               servicio del horario.
	 * @param sincronizacionHorarioService servicio de sincronización.
	 */
	public HorarioController(HorarioService horarioService,
			SincronizacionHorarioService sincronizacionHorarioService) {
		this.horarioService = horarioService;
		this.sincronizacionHorarioService = sincronizacionHorarioService;
	}

	/**
	 * Genera el horario con el motor y reemplaza el guardado.
	 *
	 * @return 200 con el resultado de la generación.
	 */
	@PostMapping("/generar")
	@PreAuthorize("hasRole('COMITE')")
	public ResponseEntity<ResultadoGeneracion> generar() {
		return ResponseEntity.ok(horarioService.generarYGuardar());
	}

	/**
	 * Sincroniza el horario guardado con los datos maestros actuales.
	 *
	 * @return 200 con las sesiones creadas, eliminadas y desubicadas.
	 */
	@PostMapping("/sincronizar")
	@PreAuthorize("hasRole('COMITE')")
	public ResponseEntity<ResultadoSincronizacion> sincronizar() {
		return ResponseEntity.ok(sincronizacionHorarioService.sincronizarTodo());
	}

	/**
	 * Consulta el horario guardado como lista.
	 *
	 * @param semestre     filtro opcional por número de semestre.
	 * @param cursoId      filtro opcional por curso.
	 * @param grupoId      filtro opcional por grupo.
	 * @param docenteId    filtro opcional por docente.
	 * @param aulaId       filtro opcional por aula.
	 * @param sedeId       filtro opcional por sede.
	 * @param soloUbicadas si es {@code true}, excluye las sin ubicar.
	 * @return 200 con las sesiones ordenadas.
	 */
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<SesionHorarioDTO>> listar(@RequestParam(required = false) Integer semestre,
			@RequestParam(required = false) Long cursoId, @RequestParam(required = false) Long grupoId,
			@RequestParam(required = false) Long docenteId, @RequestParam(required = false) Long aulaId,
			@RequestParam(required = false) Long sedeId, @RequestParam(required = false) Boolean soloUbicadas) {
		return ResponseEntity.ok(
				horarioService.listar(semestre, cursoId, grupoId, docenteId, aulaId, sedeId, soloUbicadas));
	}

	/**
	 * Consulta el horario guardado como calendario semanal, por semestre o por
	 * prefijo de la clave {@code semestre/curso/grupo}.
	 *
	 * @param semestre número de semestre (opcional).
	 * @param prefijo  prefijo de la clave, por ejemplo {@code 1/ISIS-1101/}
	 *                 (opcional; tiene prioridad sobre {@code semestre}).
	 * @return 200 con el calendario.
	 * @throws DatoFueraDeRangoException si el semestre no está entre 1 y 9
	 *                                   (400).
	 */
	@GetMapping("/calendario")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<CalendarioSemanal> calendario(@RequestParam(required = false) Integer semestre,
			@RequestParam(required = false) String prefijo) throws DatoFueraDeRangoException {
		return ResponseEntity.ok(horarioService.calendario(semestre, prefijo));
	}
}
