package unbosque.edu.co.aulasync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.RespuestaDemoDto;
import unbosque.edu.co.aulasync.dto.RespuestaGeneracionDto;
import unbosque.edu.co.aulasync.seguridad.Rol;
import unbosque.edu.co.aulasync.seguridad.RolesPermitidos;
import unbosque.edu.co.aulasync.service.MotorHorarioService;

/**
 * Controlador REST del motor de generación de horarios (casos de uso CU04 y
 * CU07 del informe de ingeniería).
 *
 * <p>
 * Expone los endpoints bajo {@code /api/motor}. Según el patrón MVC, este
 * controlador no contiene lógica de negocio ni accede a la base de datos: solo
 * recibe la petición, la delega en {@link MotorHorarioService} y devuelve el
 * resultado como DTO.
 * </p>
 */
@RestController
@RequestMapping("/api/motor")
public class MotorHorarioController {

	private final MotorHorarioService motorService;

	/**
	 * Crea el controlador con el servicio que ejecuta el motor.
	 *
	 * @param motorService servicio de generación de horarios
	 */
	public MotorHorarioController(MotorHorarioService motorService) {
		this.motorService = motorService;
	}

	/**
	 * Ejecuta el motor con un caso fijo de siete sesiones, sin usar la base de
	 * datos. Sirve para comprobar rápidamente que el motor funciona.
	 *
	 * <p>
	 * {@code GET /api/motor/demo}
	 * </p>
	 *
	 * @return sesiones ubicadas y sin ubicar del caso de demostración
	 */
	@GetMapping("/demo")
	public RespuestaDemoDto demo() {
		return motorService.generarDemo();
	}

	/**
	 * Genera el horario con los cursos, secciones, docentes, aulas y grupos
	 * registrados. Las sesiones que no puedan ubicarse se devuelven en
	 * {@code sinUbicar}, con su causa principal y el desglose de franjas
	 * descartadas.
	 *
	 * <p>
	 * {@code POST /api/motor/generar}
	 * </p>
	 *
	 * @return sesiones ubicadas y sin ubicar
	 */
	@PostMapping("/generar")
	@RolesPermitidos(Rol.COMITE)
	public RespuestaGeneracionDto generar() {
		return motorService.generarHorario();
	}
}