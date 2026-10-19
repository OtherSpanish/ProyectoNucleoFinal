package unbosque.edu.co.aulasync.motor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class MotorHorarioTest {

	@Test
	void casoDelInforme() {
		// id, claveCurso, docente, grupo, duración, tipo aula, capacidad
		List<Sesion> ses = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44), // EstDatos s1 (Doc A, Grupo
																							// X)
				new Sesion(2, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44), // EstDatos s2
				new Sesion(3, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44), // Cálculo s1
				new Sesion(4, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44), // Cálculo s2
				new Sesion(5, 3, 2, 10, 2, TipoAula.CONVENCIONAL, 44), // BD1 s1 (Doc B)
				new Sesion(6, 3, 2, 10, 2, TipoAula.CONVENCIONAL, 44), // BD1 s2
				new Sesion(7, 4, 2, 20, 4, TipoAula.SALA_COMPUTO, 44)); // IngSoft (Grupo Y)
		Map<Integer, DocenteMotor> docs = Map.of(1, new DocenteMotor(1, 40, null), 2, new DocenteMotor(2, 40, null));
		List<AulaMotor> aulas = List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL),
				new AulaMotor(2, 45, TipoAula.SALA_COMPUTO));

		MotorHorario.Resultado r = new MotorHorario(ses, docs, aulas, GeneradorFranjas.Parametros.POR_DEFECTO)
				.generar();

		assertTrue(r.getSinUbicar().isEmpty());
		assertEquals(7, r.getUbicadas().size());

		// RD6: ningún curso con dos sesiones el mismo día
		Map<Integer, Set<DayOfWeek>> diasPorCurso = new HashMap<>();
		for (Sesion s : ses) {
			Set<DayOfWeek> dias = diasPorCurso.get(s.getClaveCurso());
			if (dias == null) {
				dias = new HashSet<>();
				diasPorCurso.put(s.getClaveCurso(), dias);
			}
			assertTrue(dias.add(s.getFranja().getDia()), "curso repite día");
		}

		// RD1/RD2: sin traslapes entre sesiones del mismo docente o grupo
		for (int i = 0; i < ses.size(); i++) {
			for (int j = i + 1; j < ses.size(); j++) {
				Sesion a = ses.get(i);
				Sesion b = ses.get(j);
				if (a.getDocenteId() == b.getDocenteId() || a.getGrupoId() == b.getGrupoId()) {
					assertFalse(
							Franja.traslapan(a.getFranja(), a.getDuracionHoras(), b.getFranja(), b.getDuracionHoras()));
				}
			}
		}
	}

	@Test
	void docenteNoDisponible() {
		// Solo disponible el lunes 7-9: caben una sesión, la otra no
		Map<DayOfWeek, Set<Integer>> disp = Map.of(DayOfWeek.MONDAY, Set.of(7, 8));
		List<Sesion> ses = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(2, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44));

		MotorHorario.Resultado r = new MotorHorario(ses, Map.of(1, new DocenteMotor(1, 40, disp)),
				List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL)), GeneradorFranjas.Parametros.POR_DEFECTO)
				.generar();

		assertEquals(1, r.getUbicadas().size());
		assertEquals(1, r.getSinUbicar().size());
		assertEquals(MotorHorario.Motivo.DOCENTE_NO_DISPONIBLE, r.getSinUbicar().get(0).getCausaSinUbicar());
	}

	@Test
	void sinAula() {
		List<Sesion> ses = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.SALA_COMPUTO, 44));

		MotorHorario.Resultado r = new MotorHorario(ses, Map.of(1, new DocenteMotor(1, 40, null)),
				List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL)), GeneradorFranjas.Parametros.POR_DEFECTO)
				.generar();

		assertEquals(1, r.getSinUbicar().size());
		assertEquals(MotorHorario.Motivo.SIN_AULA, r.getSinUbicar().get(0).getCausaSinUbicar());
	}

	@Test
	void cargaDocenteExcedida() {
		// Tope de 2 h semanales, dos sesiones de 2 h
		List<Sesion> ses = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
				new Sesion(2, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44));

		MotorHorario.Resultado r = new MotorHorario(ses, Map.of(1, new DocenteMotor(1, 2, null)),
				List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL)), GeneradorFranjas.Parametros.POR_DEFECTO)
				.generar();

		assertEquals(1, r.getUbicadas().size());
		assertEquals(MotorHorario.Motivo.CARGA_DOCENTE, r.getSinUbicar().get(0).getCausaSinUbicar());
	}

	@Test
	void repartePorDias() {
		// Un grupo con 4 sesiones de cursos y docentes distintos: deben caer en 4 días
		// distintos
		List<Sesion> ses = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 40),
				new Sesion(2, 2, 2, 10, 2, TipoAula.CONVENCIONAL, 40),
				new Sesion(3, 3, 3, 10, 2, TipoAula.CONVENCIONAL, 40),
				new Sesion(4, 4, 4, 10, 2, TipoAula.CONVENCIONAL, 40));
		Map<Integer, DocenteMotor> docs = Map.of(1, new DocenteMotor(1, 40, null), 2, new DocenteMotor(2, 40, null), 3,
				new DocenteMotor(3, 40, null), 4, new DocenteMotor(4, 40, null));

		MotorHorario.Resultado r = new MotorHorario(ses, docs, List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL)),
				GeneradorFranjas.Parametros.POR_DEFECTO).generar();

		assertEquals(4, r.getUbicadas().size());
		Set<DayOfWeek> dias = new HashSet<>();
		for (Sesion s : ses) {
			dias.add(s.getFranja().getDia());
		}
		assertEquals(4, dias.size(), "las sesiones del grupo deben repartirse en 4 días");
		assertFalse(dias.contains(DayOfWeek.SATURDAY));
	}

	@Test
	void desgloseDeCausas() {
		// Docente disponible solo el lunes 7-9; dos sesiones del mismo curso
		Map<DayOfWeek, Set<Integer>> disp = Map.of(DayOfWeek.MONDAY, Set.of(7, 8));
		List<Sesion> ses = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 40),
				new Sesion(2, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 40));

		MotorHorario.Resultado r = new MotorHorario(ses, Map.of(1, new DocenteMotor(1, 40, disp)),
				List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL)), GeneradorFranjas.Parametros.POR_DEFECTO)
				.generar();

		assertEquals(1, r.getSinUbicar().size());
		Sesion sin = r.getSinUbicar().get(0);
		assertEquals(MotorHorario.Motivo.DOCENTE_NO_DISPONIBLE, sin.getCausaSinUbicar());
		assertEquals(63, sin.getDesgloseCausas().get(MotorHorario.Motivo.DOCENTE_NO_DISPONIBLE));
		assertEquals(1, sin.getDesgloseCausas().get(MotorHorario.Motivo.CHOQUE_DOCENTE));
	}
}