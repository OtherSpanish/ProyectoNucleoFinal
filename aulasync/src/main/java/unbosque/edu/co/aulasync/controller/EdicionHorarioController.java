package unbosque.edu.co.aulasync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.SesionHorarioDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.MovimientoInvalidoException;
import unbosque.edu.co.aulasync.model.ValidacionMovimiento;
import unbosque.edu.co.aulasync.service.EdicionHorarioService;

/**
 * Edición manual del horario guardado (drag and drop). Solo para
 * {@code COMITE}.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/horarios/sesiones")
@PreAuthorize("hasRole('COMITE')")
public class EdicionHorarioController {

	private final EdicionHorarioService edicionHorarioService;

	/**
	 * Crea el controlador.
	 *
	 * @param edicionHorarioService servicio de edición del horario.
	 */
	public EdicionHorarioController(EdicionHorarioService edicionHorarioService) {
		this.edicionHorarioService = edicionHorarioService;
	}

	/**
	 * Valida un movimiento sin aplicarlo.
	 *
	 * @param id      id de la sesión.
	 * @param destino {@code dia}, {@code horaInicio} y {@code aulaId}.
	 * @return 200 con {@code valido} y todos los motivos que fallan.
	 * @throws EntidadNoEncontradaException si la sesión o el aula no existen
	 *                                      (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 */
	@PostMapping("/{id}/validar")
	public ResponseEntity<ValidacionMovimiento> validar(@PathVariable Long id, @RequestBody SesionHorarioDTO destino)
			throws EntidadNoEncontradaException, CampoObligatorioException {
		return ResponseEntity.ok(edicionHorarioService.validar(id, destino));
	}

	/**
	 * Mueve una sesión si cumple todas las reglas.
	 *
	 * @param id      id de la sesión.
	 * @param destino {@code dia}, {@code horaInicio} y {@code aulaId}.
	 * @return 200 con la sesión actualizada.
	 * @throws EntidadNoEncontradaException si la sesión o el aula no existen
	 *                                      (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws MovimientoInvalidoException  si incumple alguna regla (409).
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<SesionHorarioDTO> mover(@PathVariable Long id, @RequestBody SesionHorarioDTO destino)
			throws EntidadNoEncontradaException, CampoObligatorioException, MovimientoInvalidoException {
		return ResponseEntity.ok(edicionHorarioService.mover(id, destino));
	}

	/**
	 * Devuelve una sesión a "sin ubicar".
	 *
	 * @param id id de la sesión.
	 * @return 200 con la sesión actualizada.
	 * @throws EntidadNoEncontradaException si la sesión no existe (404).
	 */
	@DeleteMapping("/{id}/ubicacion")
	public ResponseEntity<SesionHorarioDTO> desubicar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(edicionHorarioService.desubicar(id));
	}
}
