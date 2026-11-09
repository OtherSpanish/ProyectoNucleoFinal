package unbosque.edu.co.aulasync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.model.ResultadoGeneracion;
import unbosque.edu.co.aulasync.service.MotorHorarioService;

/**
 * Ejecución del motor de horarios en modo vista previa (no guarda nada). Solo
 * para {@code COMITE}.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/motor")
@PreAuthorize("hasRole('COMITE')")
public class MotorHorarioController {

	private final MotorHorarioService motorHorarioService;

	/**
	 * Crea el controlador.
	 *
	 * @param motorHorarioService servicio del motor.
	 */
	public MotorHorarioController(MotorHorarioService motorHorarioService) {
		this.motorHorarioService = motorHorarioService;
	}

	/**
	 * Genera una propuesta de horario con los datos actuales, sin guardarla.
	 *
	 * @return 200 con sesiones ubicadas, sin ubicar, grupos no abiertos y
	 *         avisos.
	 */
	@PostMapping("/generar")
	public ResponseEntity<ResultadoGeneracion> generar() {
		return ResponseEntity.ok(motorHorarioService.generar());
	}
}
