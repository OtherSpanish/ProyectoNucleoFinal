package unbosque.edu.co.aulasync.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.dto.UsuarioRequestDto;
import unbosque.edu.co.aulasync.dto.UsuarioResponseDto;
import unbosque.edu.co.aulasync.exception.ConflictoNegocioException;
import unbosque.edu.co.aulasync.exception.DatoInvalidoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.seguridad.AutenticacionInterceptor;
import unbosque.edu.co.aulasync.seguridad.Rol;
import unbosque.edu.co.aulasync.seguridad.RolesPermitidos;
import unbosque.edu.co.aulasync.seguridad.SesionUsuario;
import unbosque.edu.co.aulasync.service.UsuarioService;

/**
 * Usuarios bajo {@code /api/usuarios}. Consultar: Admin y Dirección. Crear,
 * editar y eliminar: solo Admin.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping
	@RolesPermitidos({ Rol.ADMIN, Rol.DIRECCION })
	public List<UsuarioResponseDto> listar() {
		return usuarioService.listar();
	}

	@GetMapping("/{id}")
	@RolesPermitidos({ Rol.ADMIN, Rol.DIRECCION })
	public UsuarioResponseDto obtener(@PathVariable Long id) throws EntidadNoEncontradaException {
		return usuarioService.obtener(id);
	}

	@PostMapping
	@RolesPermitidos(Rol.ADMIN)
	public ResponseEntity<UsuarioResponseDto> crear(@RequestBody UsuarioRequestDto dto)
			throws DatoInvalidoException, ConflictoNegocioException {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(dto));
	}

	@PutMapping("/{id}")
	@RolesPermitidos(Rol.ADMIN)
	public UsuarioResponseDto actualizar(@PathVariable Long id, @RequestBody UsuarioRequestDto dto)
			throws EntidadNoEncontradaException, DatoInvalidoException, ConflictoNegocioException {
		return usuarioService.actualizar(id, dto);
	}

	@DeleteMapping("/{id}")
	@RolesPermitidos(Rol.ADMIN)
	public ResponseEntity<Void> eliminar(@PathVariable Long id,
			@RequestAttribute(AutenticacionInterceptor.ATRIBUTO_SESION) SesionUsuario sesion)
			throws EntidadNoEncontradaException, ConflictoNegocioException {
		usuarioService.eliminar(id, sesion.getId());
		return ResponseEntity.noContent().build();
	}
}
