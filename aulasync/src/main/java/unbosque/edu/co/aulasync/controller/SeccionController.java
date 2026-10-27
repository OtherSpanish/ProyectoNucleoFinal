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

import unbosque.edu.co.aulasync.dto.SeccionRequestDto;
import unbosque.edu.co.aulasync.dto.SeccionResponseDto;
import unbosque.edu.co.aulasync.exception.*;
import unbosque.edu.co.aulasync.seguridad.Rol;
import unbosque.edu.co.aulasync.seguridad.RolesPermitidos;
import unbosque.edu.co.aulasync.service.SeccionService;

/**
 * Secciones (curso, docente y grupo; cambiar el docente edita profesores respecto a materias) bajo {@code /api/secciones}. Consultar: cualquier rol autenticado (Admin,
 * Dirección y Comité). Crear, editar y eliminar: solo Comité.
 */
@RestController
@RequestMapping("/api/secciones")
public class SeccionController {

	private final SeccionService servicio;

	public SeccionController(SeccionService servicio) {
		this.servicio = servicio;
	}

	@GetMapping
	public List<SeccionResponseDto> listar() {
		return servicio.listar();
	}

	@GetMapping("/{id}")
	public SeccionResponseDto obtener(@PathVariable Long id) throws EntidadNoEncontradaException {
		return servicio.obtener(id);
	}

	@PostMapping
	@RolesPermitidos(Rol.COMITE)
	public ResponseEntity<SeccionResponseDto> crear(@RequestBody SeccionRequestDto dto) throws EntidadNoEncontradaException, DatoInvalidoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(dto));
	}

	@PutMapping("/{id}")
	@RolesPermitidos(Rol.COMITE)
	public SeccionResponseDto actualizar(@PathVariable Long id, @RequestBody SeccionRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException {
		return servicio.actualizar(id, dto);
	}

	@DeleteMapping("/{id}")
	@RolesPermitidos(Rol.COMITE)
	public ResponseEntity<Void> eliminar(@PathVariable Long id) throws EntidadNoEncontradaException {
		servicio.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
