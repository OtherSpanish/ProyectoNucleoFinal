package unbosque.edu.co.aulasync.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.GrupoDTO;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.CargaDocenteExcedidaException;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.DisponibilidadDocenteInsuficienteException;
import unbosque.edu.co.aulasync.exception.DocenteSinEspecialidadException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.GrupoDuplicadoException;
import unbosque.edu.co.aulasync.service.GrupoService;

/**
 * Gestión de los grupos de cada curso.
 *
 * <ul>
 * <li>Consultar: {@code ADMIN} y {@code COMITE}.</li>
 * <li>Crear, actualizar y eliminar: solo {@code COMITE}.</li>
 * </ul>
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

	private final GrupoService grupoService;

	/**
	 * Crea el controlador.
	 *
	 * @param grupoService servicio de grupos.
	 */
	public GrupoController(GrupoService grupoService) {
		this.grupoService = grupoService;
	}

	/**
	 * Lista los grupos ordenados por semestre, curso y número.
	 *
	 * @param cursoId    filtro opcional por curso.
	 * @param docenteId  filtro opcional por docente.
	 * @param semestreId filtro opcional por semestre.
	 * @return 200 con la lista.
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<List<GrupoDTO>> listar(@RequestParam(required = false) Long cursoId,
			@RequestParam(required = false) Long docenteId, @RequestParam(required = false) Long semestreId) {
		return ResponseEntity.ok(grupoService.listar(cursoId, docenteId, semestreId));
	}

	/**
	 * Trae un grupo por id.
	 *
	 * @param id identificador.
	 * @return 200 con el grupo.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'COMITE')")
	public ResponseEntity<GrupoDTO> buscar(@PathVariable Long id) throws EntidadNoEncontradaException {
		return ResponseEntity.ok(grupoService.buscarPorId(id));
	}

	/**
	 * Crea un grupo.
	 *
	 * @param dto número, cursoId y docenteId.
	 * @return 201 con el grupo creado.
	 * @throws CampoObligatorioException                   si falta un dato
	 *                                                     (400).
	 * @throws DatoFueraDeRangoException                   si el número está
	 *                                                     fuera de rango (400).
	 * @throws EntidadNoEncontradaException                si el curso o el
	 *                                                     docente no existen
	 *                                                     (404).
	 * @throws GrupoDuplicadoException                     si el número ya existe
	 *                                                     en el curso (409).
	 * @throws DocenteSinEspecialidadException             si el docente no tiene
	 *                                                     la especialidad (400).
	 * @throws CargaDocenteExcedidaException               si supera el tope de
	 *                                                     horas (409).
	 * @throws DisponibilidadDocenteInsuficienteException si la disponibilidad
	 *                                                     no alcanza (400).
	 */
	@PostMapping
	@PreAuthorize("hasRole('COMITE')")
	public ResponseEntity<GrupoDTO> crear(@RequestBody GrupoDTO dto) throws CampoObligatorioException,
			DatoFueraDeRangoException, EntidadNoEncontradaException, GrupoDuplicadoException,
			DocenteSinEspecialidadException, CargaDocenteExcedidaException,
			DisponibilidadDocenteInsuficienteException {
		return ResponseEntity.status(HttpStatus.CREATED).body(grupoService.crear(dto));
	}

	/**
	 * Actualiza un grupo.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return 200 con el grupo actualizado.
	 * @throws EntidadNoEncontradaException                si algo no existe
	 *                                                     (404).
	 * @throws CampoObligatorioException                   si falta un dato
	 *                                                     (400).
	 * @throws DatoFueraDeRangoException                   si el número está
	 *                                                     fuera de rango (400).
	 * @throws GrupoDuplicadoException                     si el número ya existe
	 *                                                     en el curso (409).
	 * @throws DocenteSinEspecialidadException             si el docente no tiene
	 *                                                     la especialidad (400).
	 * @throws CargaDocenteExcedidaException               si supera el tope de
	 *                                                     horas (409).
	 * @throws DisponibilidadDocenteInsuficienteException si la disponibilidad
	 *                                                     no alcanza (400).
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('COMITE')")
	public ResponseEntity<GrupoDTO> actualizar(@PathVariable Long id, @RequestBody GrupoDTO dto)
			throws EntidadNoEncontradaException, CampoObligatorioException, DatoFueraDeRangoException,
			GrupoDuplicadoException, DocenteSinEspecialidadException, CargaDocenteExcedidaException,
			DisponibilidadDocenteInsuficienteException {
		return ResponseEntity.ok(grupoService.actualizar(id, dto));
	}

	/**
	 * Elimina un grupo junto con sus inscripciones.
	 *
	 * @param id identificador.
	 * @return 204 sin contenido.
	 * @throws EntidadNoEncontradaException si no existe (404).
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('COMITE')")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) throws EntidadNoEncontradaException {
		grupoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
