package unbosque.edu.co.aulasync.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.FranjaDocenteDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.FranjaInvalidaException;
import unbosque.edu.co.aulasync.exception.FranjasTraslapadasException;
import unbosque.edu.co.aulasync.model.DisponibilidadDocente;
import unbosque.edu.co.aulasync.service.DisponibilidadService;

/**
 * Disponibilidad horaria semanal de los docentes.
 *
 * <ul>
 * <li>Consultar: {@code ADMIN} y {@code COMITE}.</li>
 * <li>Reemplazar: solo {@code ADMIN}.</li>
 * </ul>
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/docentes/{docenteId}/disponibilidad")
public class DisponibilidadController {

	private final DisponibilidadService disponibilidadService;

	/**
	 * Crea el controlador.
	 *
	 * @param disponibilidadService servicio de disponibilidad.
	 */
	public DisponibilidadController(DisponibilidadService disponibilidadService) {
		this.disponibilidadService = disponibilidadService;
	}

	/**
	 * Consulta la disponibilidad semanal de un docente.
	 *
	 * @param docenteId id del docente.
	 * @return 200 con las franjas y los totales.
	 * @throws EntidadNoEncontradaException si el docente no existe (404).
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<DisponibilidadDocente> obtener(@PathVariable Long docenteId)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(disponibilidadService.obtener(docenteId));
	}

	/**
	 * Reemplaza la disponibilidad semanal completa de un docente.
	 *
	 * @param docenteId id del docente.
	 * @param franjas   lista de franjas ({@code dia}, {@code horaInicio},
	 *                  {@code horaFin}); vacía para dejarlo sin disponibilidad.
	 * @return 200 con la disponibilidad guardada.
	 * @throws EntidadNoEncontradaException si el docente no existe (404).
	 * @throws CampoObligatorioException    si falta un dato (400).
	 * @throws FranjaInvalidaException      si una franja es inválida (400).
	 * @throws FranjasTraslapadasException  si dos franjas se cruzan (400).
	 */
	@PutMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<DisponibilidadDocente> reemplazar(@PathVariable Long docenteId,
			@RequestBody List<FranjaDocenteDTO> franjas) throws EntidadNoEncontradaException,
			CampoObligatorioException, FranjaInvalidaException, FranjasTraslapadasException {
		return ResponseEntity.ok(disponibilidadService.reemplazar(docenteId, franjas));
	}
}
