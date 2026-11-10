package unbosque.edu.co.aulasync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unbosque.edu.co.aulasync.model.Catalogos;
import unbosque.edu.co.aulasync.service.CatalogoService;

/**
 * Catálogos del dominio (roles, tipos de aula, dedicaciones, intensidades,
 * días, horas de la jornada y motivos de rechazo) para que el frontend llene
 * sus listas sin datos escritos a mano. Para cualquier usuario autenticado.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/catalogos")
@PreAuthorize("isAuthenticated()")
public class CatalogoController {

	private final CatalogoService catalogoService;

	/**
	 * Crea el controlador.
	 *
	 * @param catalogoService servicio de catálogos.
	 */
	public CatalogoController(CatalogoService catalogoService) {
		this.catalogoService = catalogoService;
	}

	/**
	 * Devuelve todos los catálogos.
	 *
	 * @return 200 con los catálogos.
	 */
	@GetMapping
	public ResponseEntity<Catalogos> obtener() {
		return ResponseEntity.ok(catalogoService.obtener());
	}
}
