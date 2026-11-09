package unbosque.edu.co.aulasync.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.EstudianteDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.EstudianteEnOtroGrupoException;
import unbosque.edu.co.aulasync.exception.GrupoLlenoException;
import unbosque.edu.co.aulasync.model.InscritosGrupo;
import unbosque.edu.co.aulasync.service.InscripcionService;

/**
 * Inscripción de estudiantes en grupos. Todos los endpoints son para
 * {@code ADMIN} y {@code COMITE}.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/grupos/{grupoId}/estudiantes")
@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
public class InscripcionController {

	private final InscripcionService inscripcionService;

	/**
	 * Crea el controlador.
	 *
	 * @param inscripcionService servicio de inscripciones.
	 */
	public InscripcionController(InscripcionService inscripcionService) {
		this.inscripcionService = inscripcionService;
	}

	/**
	 * Consulta los inscritos de un grupo.
	 *
	 * @param grupoId id del grupo.
	 * @return 200 con el grupo, sus inscritos y cuántos faltan para abrir.
	 * @throws EntidadNoEncontradaException si el grupo no existe (404).
	 */
	@GetMapping
	public ResponseEntity<InscritosGrupo> obtener(@PathVariable Long grupoId) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(inscripcionService.obtener(grupoId));
	}

	/**
	 * Reemplaza la lista completa de inscritos.
	 *
	 * @param grupoId     id del grupo.
	 * @param estudiantes lista de estudiantes (basta con {@code id}).
	 * @return 200 con la lista guardada.
	 * @throws EntidadNoEncontradaException   si el grupo o un estudiante no
	 *                                        existen (404).
	 * @throws CampoObligatorioException      si falta la lista o un id (400).
	 * @throws GrupoLlenoException            si la lista supera el máximo de
	 *                                        inscritos (409).
	 * @throws EstudianteEnOtroGrupoException si un estudiante ya está en otro
	 *                                        grupo de la materia (409).
	 */
	@PutMapping
	public ResponseEntity<InscritosGrupo> reemplazar(@PathVariable Long grupoId,
			@RequestBody List<EstudianteDTO> estudiantes) throws EntidadNoEncontradaException,
			CampoObligatorioException, GrupoLlenoException, EstudianteEnOtroGrupoException {
		return ResponseEntity.ok(inscripcionService.reemplazar(grupoId, estudiantes));
	}

	/**
	 * Inscribe a un estudiante.
	 *
	 * @param grupoId      id del grupo.
	 * @param estudianteId id del estudiante.
	 * @return 200 con la lista actualizada.
	 * @throws EntidadNoEncontradaException   si el grupo o el estudiante no
	 *                                        existen (404).
	 * @throws GrupoLlenoException            si el grupo ya tiene el máximo de
	 *                                        inscritos (409).
	 * @throws EstudianteEnOtroGrupoException si ya está en otro grupo de la
	 *                                        materia (409).
	 */
	@PostMapping("/{estudianteId}")
	public ResponseEntity<InscritosGrupo> agregar(@PathVariable Long grupoId, @PathVariable Long estudianteId)
			throws EntidadNoEncontradaException, GrupoLlenoException, EstudianteEnOtroGrupoException {
		return ResponseEntity.ok(inscripcionService.agregar(grupoId, estudianteId));
	}

	/**
	 * Quita a un estudiante del grupo.
	 *
	 * @param grupoId      id del grupo.
	 * @param estudianteId id del estudiante.
	 * @return 200 con la lista actualizada.
	 * @throws EntidadNoEncontradaException si el grupo no existe (404).
	 */
	@DeleteMapping("/{estudianteId}")
	public ResponseEntity<InscritosGrupo> quitar(@PathVariable Long grupoId, @PathVariable Long estudianteId)
			throws EntidadNoEncontradaException {
		return ResponseEntity.ok(inscripcionService.quitar(grupoId, estudianteId));
	}
}
