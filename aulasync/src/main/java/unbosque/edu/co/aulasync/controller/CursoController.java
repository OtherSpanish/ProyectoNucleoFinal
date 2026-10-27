package unbosque.edu.co.aulasync.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.CursoRequestDto;
import unbosque.edu.co.aulasync.dto.CursoResponseDto;
import unbosque.edu.co.aulasync.exception.ConflictoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.ValidacionException;
import unbosque.edu.co.aulasync.seguridad.Rol;
import unbosque.edu.co.aulasync.seguridad.RolesPermitidos;
import unbosque.edu.co.aulasync.service.CursoService;

/**
 * Controlador REST de cursos (CU01, gestión de datos maestros; RF1).
 *
 * <p>
 * Expone los endpoints bajo {@code /api/cursos}. Consultar: cualquier rol
 * autenticado; crear, editar y eliminar: solo Comité. No contiene validaciones ni
 * acceso a datos: delega todo en {@link CursoService}. Las excepciones de
 * negocio que el servicio propaga las convierte en respuestas HTTP el
 * {@code GlobalExceptionHandler}.
 * </p>
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

	private final CursoService cursoService;

	/**
	 * Crea el controlador con el servicio de cursos.
	 *
	 * @param cursoService servicio con la lógica de cursos
	 */
	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	/**
	 * Lista todos los cursos.
	 *
	 * <p>
	 * {@code GET /api/cursos}
	 * </p>
	 *
	 * @return lista de cursos
	 */
	@GetMapping
	public List<CursoResponseDto> listar() {
		return cursoService.listar();
	}

	/**
	 * Consulta un curso por su identificador.
	 *
	 * <p>
	 * {@code GET /api/cursos/{id}}
	 * </p>
	 *
	 * @param id identificador del curso
	 * @return el curso encontrado
	 * @throws EntidadNoEncontradaException si no existe (respuesta 404)
	 */
	@GetMapping("/{id}")
	public CursoResponseDto obtener(@PathVariable Long id) throws EntidadNoEncontradaException {
		return cursoService.obtener(id);
	}

	/**
	 * Crea un curso nuevo.
	 *
	 * <p>
	 * {@code POST /api/cursos}
	 * </p>
	 *
	 * @param dto datos del curso (nombre, sesiones por semana, duración y tipo de
	 *            aula)
	 * @return el curso creado con código 201
	 * @throws ValidacionException si los datos son inválidos (respuesta 400)
	 */
	@PostMapping
	@RolesPermitidos(Rol.COMITE)
	public ResponseEntity<CursoResponseDto> crear(@RequestBody CursoRequestDto dto) throws ValidacionException {
		return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.crear(dto));
	}

	/**
	 * Edita un curso (solo Comité).
	 *
	 * <p>
	 * {@code PUT /api/cursos/{id}}
	 * </p>
	 *
	 * @param id  identificador del curso
	 * @param dto datos nuevos del curso
	 * @return el curso actualizado
	 * @throws EntidadNoEncontradaException si el curso no existe (respuesta 404)
	 * @throws ValidacionException          si los datos son inválidos (respuesta 400)
	 */
	@PutMapping("/{id}")
	@RolesPermitidos(Rol.COMITE)
	public CursoResponseDto actualizar(@PathVariable Long id, @RequestBody CursoRequestDto dto)
			throws EntidadNoEncontradaException, ValidacionException {
		return cursoService.actualizar(id, dto);
	}

	/**
	 * Elimina un curso.
	 *
	 * <p>
	 * {@code DELETE /api/cursos/{id}}
	 * </p>
	 *
	 * @param id identificador del curso
	 * @return 204 si se eliminó
	 * @throws EntidadNoEncontradaException si el curso no existe (respuesta 404)
	 * @throws ConflictoException           si el curso tiene secciones asociadas
	 *                                      (respuesta 409)
	 */
	@DeleteMapping("/{id}")
	@RolesPermitidos(Rol.COMITE)
	public ResponseEntity<Void> eliminar(@PathVariable Long id)
			throws EntidadNoEncontradaException, ConflictoException {
		cursoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
