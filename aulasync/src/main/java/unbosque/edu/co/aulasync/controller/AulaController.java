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

import unbosque.edu.co.aulasync.dto.AulaRequestDto;
import unbosque.edu.co.aulasync.dto.AulaResponseDto;
import unbosque.edu.co.aulasync.exception.*;
import unbosque.edu.co.aulasync.seguridad.Rol;
import unbosque.edu.co.aulasync.seguridad.RolesPermitidos;
import unbosque.edu.co.aulasync.service.AulaService;

/**
 * Aulas bajo {@code /api/aulas}. Consultar: cualquier rol autenticado (Admin,
 * Dirección y Comité). Crear, editar y eliminar: solo Comité.
 */
@RestController
@RequestMapping("/api/aulas")
public class AulaController {

	private final AulaService servicio;

	public AulaController(AulaService servicio) {
		this.servicio = servicio;
	}

	@GetMapping
	public List<AulaResponseDto> listar() {
		return servicio.listar();
	}

	@GetMapping("/{id}")
	public AulaResponseDto obtener(@PathVariable Long id) throws EntidadNoEncontradaException {
		return servicio.obtener(id);
	}

	@PostMapping
	@RolesPermitidos(Rol.COMITE)
	public ResponseEntity<AulaResponseDto> crear(@RequestBody AulaRequestDto dto) throws DatoInvalidoException {
		return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(dto));
	}

	@PutMapping("/{id}")
	@RolesPermitidos(Rol.COMITE)
	public AulaResponseDto actualizar(@PathVariable Long id, @RequestBody AulaRequestDto dto)
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
