package unbosque.edu.co.aulasync.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.GrupoDTO;
import unbosque.edu.co.aulasync.exception.AccesoDocenteException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.model.CalendarioSemanal;
import unbosque.edu.co.aulasync.model.DisponibilidadDocente;
import unbosque.edu.co.aulasync.model.InscritosGrupo;
import unbosque.edu.co.aulasync.service.PortalDocenteService;

/**
 * Portal del docente: consulta de su propia información. Solo para
 * {@code DOCENTE}; el docente se identifica por el token.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/mi")
@PreAuthorize("hasRole('DOCENTE')")
public class PortalDocenteController {

	private final PortalDocenteService portalDocenteService;

	/**
	 * Crea el controlador.
	 *
	 * @param portalDocenteService servicio del portal docente.
	 */
	public PortalDocenteController(PortalDocenteService portalDocenteService) {
		this.portalDocenteService = portalDocenteService;
	}

	/**
	 * Grupos que dicta el docente.
	 *
	 * @param autenticacion autenticación del docente.
	 * @return 200 con sus grupos.
	 * @throws AccesoDocenteException si no está vinculado a un docente (403).
	 */
	@GetMapping("/cursos")
	public ResponseEntity<List<GrupoDTO>> misCursos(Authentication autenticacion) throws AccesoDocenteException {
		return ResponseEntity.ok(portalDocenteService.misGrupos(autenticacion.getName()));
	}

	/**
	 * Inscritos de un grupo del docente.
	 *
	 * @param autenticacion autenticación del docente.
	 * @param grupoId       id del grupo.
	 * @return 200 con el grupo y sus inscritos.
	 * @throws AccesoDocenteException       si no está vinculado o el grupo es de
	 *                                      otro docente (403).
	 * @throws EntidadNoEncontradaException si el grupo no existe (404).
	 */
	@GetMapping("/grupos/{grupoId}/estudiantes")
	public ResponseEntity<InscritosGrupo> estudiantes(Authentication autenticacion, @PathVariable Long grupoId)
			throws AccesoDocenteException, EntidadNoEncontradaException {
		return ResponseEntity.ok(portalDocenteService.estudiantesDeGrupo(autenticacion.getName(), grupoId));
	}

	/**
	 * Calendario semanal del docente.
	 *
	 * @param autenticacion autenticación del docente.
	 * @return 200 con su calendario.
	 * @throws AccesoDocenteException si no está vinculado a un docente (403).
	 */
	@GetMapping("/horario")
	public ResponseEntity<CalendarioSemanal> miHorario(Authentication autenticacion) throws AccesoDocenteException {
		return ResponseEntity.ok(portalDocenteService.miHorario(autenticacion.getName()));
	}

	/**
	 * Disponibilidad registrada del docente.
	 *
	 * @param autenticacion autenticación del docente.
	 * @return 200 con sus franjas, horas disponibles y tope.
	 * @throws AccesoDocenteException       si no está vinculado a un docente
	 *                                      (403).
	 * @throws EntidadNoEncontradaException nunca en la práctica.
	 */
	@GetMapping("/disponibilidad")
	public ResponseEntity<DisponibilidadDocente> miDisponibilidad(Authentication autenticacion)
			throws AccesoDocenteException, EntidadNoEncontradaException {
		return ResponseEntity.ok(portalDocenteService.miDisponibilidad(autenticacion.getName()));
	}
}
