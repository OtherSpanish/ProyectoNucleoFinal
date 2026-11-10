package unbosque.edu.co.aulasync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.model.TrazaMotor;
import unbosque.edu.co.aulasync.service.MotorHorarioService;

/**
 * Traza del motor de horarios para la vista que explica el algoritmo paso a
 * paso (grafo de conflictos, DSU y DSatur). No guarda nada. Solo para
 * {@code ADMIN}.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/motor/traza")
@PreAuthorize("hasRole('ADMIN')")
public class TrazaMotorController {

	private final MotorHorarioService motorHorarioService;

	/**
	 * Crea el controlador.
	 *
	 * @param motorHorarioService servicio del motor.
	 */
	public TrazaMotorController(MotorHorarioService motorHorarioService) {
		this.motorHorarioService = motorHorarioService;
	}

	/**
	 * Ejecuta el motor con los datos actuales y devuelve cómo construyó el grafo
	 * y en qué orden ubicó cada sesión.
	 *
	 * @return 200 con nodos, aristas, componentes del DSU y pasos de DSatur.
	 */
	@GetMapping
	public ResponseEntity<TrazaMotor> trazar() {
		return ResponseEntity.ok(motorHorarioService.trazar());
	}
}
