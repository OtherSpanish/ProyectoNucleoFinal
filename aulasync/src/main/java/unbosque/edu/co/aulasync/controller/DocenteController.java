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

import unbosque.edu.co.aulasync.dto.DocenteRequestDto;
import unbosque.edu.co.aulasync.dto.DocenteResponseDto;
import unbosque.edu.co.aulasync.exception.*;
import unbosque.edu.co.aulasync.seguridad.Rol;
import unbosque.edu.co.aulasync.seguridad.RolesPermitidos;
import unbosque.edu.co.aulasync.service.DocenteService;

/**
 * Docentes y su disponibilidad bajo {@code /api/docentes}. Consultar: cualquier rol autenticado (Admin,
 * Dirección y Comité). Crear, editar y eliminar: solo Comité.
 */
@RestController
@RequestMapping("/api/docentes")
public class DocenteController {

	private final DocenteService servicio;

	public DocenteController(DocenteService servicio) {
		this.servicio = servicio;
	}

	@GetMapping
	public List<DocenteResponseDto> listar() {
		return servicio.listar();
	}

	@GetMapping("/{id}")
	public DocenteResponseDto obtener(@PathVariable Long id) throws EntidadNoEncontradaException {
		return servicio.obtener(id);
	}

	@PostMapping
	@RolesPermitidos(Rol.COMITE)
	public ResponseEntity<DocenteResponseDto> crear(@RequestBody DocenteRequestDto dto) throws DatoInvalidoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(dto));
	}

	@PutMapping("/{id}")
	@RolesPermitidos(Rol.COMITE)
	public DocenteResponseDto actualizar(@PathVariable Long id, @RequestBody DocenteRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException {
		return servicio.actualizar(id, dto);
	}

	@DeleteMapping("/{id}")
	@RolesPermitidos(Rol.COMITE)
	public ResponseEntity<Void> eliminar(@PathVariable Long id) throws EntidadNoEncontradaException, ConflictoNegocioException {
		servicio.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
