package unbosque.edu.co.aulasync.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unbosque.edu.co.aulasync.model.Curso;
import unbosque.edu.co.aulasync.repository.CursoRepository;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

	private final CursoRepository repo;

	public CursoController(CursoRepository repo) {
		this.repo = repo;
	}

	@GetMapping
	public List<Curso> listar() {
		return repo.findAll();
	}

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody Curso c) {
		if (c.getNombre() == null || c.getNombre().isBlank())
			return ResponseEntity.badRequest().body("El nombre del curso es obligatorio.");
		if (c.getSesionesSemana() < 1 || c.getSesionesSemana() > 4)
			return ResponseEntity.badRequest().body("Sesiones por semana debe estar entre 1 y 4.");
		return ResponseEntity.ok(repo.save(c));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		repo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}