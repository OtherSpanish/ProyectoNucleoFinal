package unbosque.edu.co.aulasync.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.HorarioDto;
import unbosque.edu.co.aulasync.dto.MoverSesionRequestDto;
import unbosque.edu.co.aulasync.dto.SesionHorarioDto;
import unbosque.edu.co.aulasync.dto.ValidacionMovimientoDto;
import unbosque.edu.co.aulasync.exception.DatoInvalidoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.MovimientoInvalidoException;
import unbosque.edu.co.aulasync.seguridad.Rol;
import unbosque.edu.co.aulasync.seguridad.RolesPermitidos;
import unbosque.edu.co.aulasync.service.HorarioService;

/**
 * Horario guardado bajo {@code /api/horarios}. Consultar: cualquier rol
 * autenticado. Generar y mover sesiones (drag and drop): solo Comité.
 */
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

	private final HorarioService horarioService;

	public HorarioController(HorarioService horarioService) {
		this.horarioService = horarioService;
	}

	/**
	 * {@code GET /api/horarios}: sesiones ubicadas y sin ubicar. Filtros
	 * opcionales: {@code docenteId}, {@code grupoId}, {@code aulaId},
	 * {@code cursoId}.
	 */
	@GetMapping
	public HorarioDto obtener(@RequestParam(required = false) Long docenteId,
			@RequestParam(required = false) Long grupoId, @RequestParam(required = false) Long aulaId,
			@RequestParam(required = false) Long cursoId) {
		return horarioService.obtener(docenteId, grupoId, aulaId, cursoId);
	}

	/** {@code POST /api/horarios/generar}: corre el motor y reemplaza el horario guardado. */
	@PostMapping("/generar")
	@RolesPermitidos(Rol.COMITE)
	public HorarioDto generar() {
		return horarioService.generar();
	}

	/**
	 * {@code POST /api/horarios/sesiones/{id}/validar}: comprueba un movimiento
	 * sin aplicarlo (para marcar la celda mientras se arrastra).
	 */
	@PostMapping("/sesiones/{id}/validar")
	@RolesPermitidos(Rol.COMITE)
	public ValidacionMovimientoDto validar(@PathVariable Long id, @RequestBody MoverSesionRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException {
		return horarioService.validarMovimiento(id, dto);
	}

	/**
	 * {@code PATCH /api/horarios/sesiones/{id}}: mueve la sesión (drag and drop).
	 * Responde 409 con los motivos si alguna regla se incumple.
	 */
	@PatchMapping("/sesiones/{id}")
	@RolesPermitidos(Rol.COMITE)
	public SesionHorarioDto mover(@PathVariable Long id, @RequestBody MoverSesionRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException, MovimientoInvalidoException {
		return horarioService.mover(id, dto);
	}

	/** {@code DELETE /api/horarios/sesiones/{id}/ubicacion}: devuelve la sesión a "sin ubicar". */
	@DeleteMapping("/sesiones/{id}/ubicacion")
	@RolesPermitidos(Rol.COMITE)
	public SesionHorarioDto desubicar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return horarioService.desubicar(id);
	}
}
