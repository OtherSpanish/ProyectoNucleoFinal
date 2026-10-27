package unbosque.edu.co.aulasync.config;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import unbosque.edu.co.aulasync.model.*;
import unbosque.edu.co.aulasync.motor.TipoAula;
import unbosque.edu.co.aulasync.repository.*;
import unbosque.edu.co.aulasync.seguridad.ContrasenaService;
import unbosque.edu.co.aulasync.seguridad.Rol;

@Configuration
@Profile("dev")
public class DataSeeder {

	@Bean
	CommandLineRunner seedData(UsuarioRepository usuarioRepo, CursoRepository cursoRepo, DocenteRepository docenteRepo,
			AulaRepository aulaRepo, GrupoRepository grupoRepo, SeccionRepository seccionRepo,
			ContrasenaService contrasenas) {
		return args -> {
			if (usuarioRepo.count() > 0)
				return;

			usuarioRepo.saveAll(List.of(
					new Usuario("Admin", "admin@aulasync.com", contrasenas.cifrar("admin123"), Rol.ADMIN.name()),
					new Usuario("Dirección Demo", "direccion@aulasync.com", contrasenas.cifrar("direccion123"),
							Rol.DIRECCION.name()),
					new Usuario("Comité Demo", "comite@aulasync.com", contrasenas.cifrar("comite123"),
							Rol.COMITE.name())));

			// Cursos
			Curso ed = curso("Estructuras de Datos", 2, 2, TipoAula.CONVENCIONAL);
			Curso cd = curso("Cálculo Diferencial", 2, 2, TipoAula.CONVENCIONAL);
			Curso bd = curso("Bases de Datos 1", 2, 2, TipoAula.CONVENCIONAL);
			Curso is = curso("Ingeniería de Software", 1, 4, TipoAula.SALA_COMPUTO);
			Curso re = curso("Redes", 2, 2, TipoAula.CONVENCIONAL);
			cursoRepo.saveAll(List.of(ed, cd, bd, is, re));

			// Docentes con franjas (ejemplo: ajusta a tu pantalla de disponibilidad)
			Docente a = docente("Docente A", "TIEMPO_COMPLETO");
			for (DayOfWeek d : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
					DayOfWeek.FRIDAY))
				franja(a, d, 7, 18);
			Docente b = docente("Docente B", "MEDIO_TIEMPO");
			for (DayOfWeek d : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
					DayOfWeek.FRIDAY))
				franja(b, d, 7, 18);
			Docente c = docente("Docente C", "UN_CUARTO");
			franja(c, DayOfWeek.MONDAY, 7, 13); // una sola franja: Redes quedará con una sesión sin ubicar
			docenteRepo.saveAll(List.of(a, b, c));

			// Aulas
			aulaRepo.saveAll(List.of(aula("A-204", 40, TipoAula.CONVENCIONAL),
					aula("Lab-03", 40, TipoAula.SALA_COMPUTO), aula("A-205", 40, TipoAula.CONVENCIONAL)));

			// Grupos
			Grupo x = grupo("Grupo X", 4, 32);
			Grupo y = grupo("Grupo Y", 6, 28);
			Grupo z = grupo("Grupo Z", 5, 30);
			grupoRepo.saveAll(List.of(x, y, z));

			// Secciones
			seccionRepo.saveAll(List.of(seccion(ed, a, x), seccion(cd, a, x), seccion(bd, b, x), seccion(is, b, y),
					seccion(re, c, z)));

			System.out.println(">> Datos de prueba insertados");
		};
	}

	private Curso curso(String n, int ses, int dur, TipoAula t) {
		Curso c = new Curso();
		c.setNombre(n);
		c.setSesionesSemana(ses);
		c.setDuracionHoras(dur);
		c.setTipoAula(t);
		return c;
	}

	private Docente docente(String n, String ded) {
		Docente d = new Docente();
		d.setNombre(n);
		d.setDedicacion(ded);
		return d;
	}

	private void franja(Docente d, DayOfWeek dia, int ini, int fin) {
		FranjaDocente f = new FranjaDocente();
		f.setDocente(d);
		f.setDia(dia);
		f.setHoraInicio(ini);
		f.setHoraFin(fin);
		d.getFranjas().add(f);
	}

	private Aula aula(String n, int cap, TipoAula t) {
		Aula a = new Aula();
		a.setNombre(n);
		a.setCapacidad(cap);
		a.setTipo(t);
		return a;
	}

	private Grupo grupo(String n, int sem, int est) {
		Grupo g = new Grupo();
		g.setNombre(n);
		g.setSemestre(sem);
		g.setEstudiantes(est);
		return g;
	}

	private Seccion seccion(Curso c, Docente d, Grupo g) {
		Seccion s = new Seccion();
		s.setNumero(1);
		s.setCurso(c);
		s.setDocente(d);
		s.setGrupo(g);
		return s;
	}
}