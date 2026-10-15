package unbosque.edu.co.aulasync.motor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;

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
				new Sesion(7, 4, 2, 20, 4, TipoAula.SALA_COMPUTO, 44) // IngSoft (Grupo Y)
		);
		Map<Integer, DocenteMotor> docs = Map.of(1, new DocenteMotor(1, 40, null), 2, new DocenteMotor(2, 40, null));
		List<AulaMotor> aulas = List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL), new AulaMotor(2, 45, TipoAula.SALA_COMPUTO));

		var r = new MotorHorario(ses, docs, aulas, GeneradorFranjas.Parametros.POR_DEFECTO).generar();

		assertTrue(r.sinUbicar().isEmpty());
		assertEquals(7, r.ubicadas().size());

		// RD6: ningún curso con dos sesiones el mismo día
		Map<Integer, Set<Object>> diasPorCurso = new HashMap<>();
		for (Sesion s : ses)
			assertTrue(diasPorCurso.computeIfAbsent(s.getClaveCurso(), k -> new HashSet<>()).add(s.getFranja().dia()),
					"curso repite día");

		// RD1/RD2: sin traslapes entre sesiones del mismo docente o grupo
		for (int i = 0; i < ses.size(); i++)
			for (int j = i + 1; j < ses.size(); j++) {
				Sesion a = ses.get(i), b = ses.get(j);
				if (a.getDocenteId() == b.getDocenteId() || a.getGrupoId() == b.getGrupoId())
					assertFalse(
							Franja.traslapan(a.getFranja(), a.getDuracionHoras(), b.getFranja(), b.getDuracionHoras()));
			}
	}
	
	@Test
	void docenteNoDisponible() {
	    // Solo disponible el lunes 7-9: caben una sesión, la otra no
	    Map<DayOfWeek, Set<Integer>> disp = Map.of(DayOfWeek.MONDAY, Set.of(7, 8));
	    List<Sesion> ses = List.of(
	        new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
	        new Sesion(2, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44));
	    var r = new MotorHorario(ses, Map.of(1, new DocenteMotor(1, 40, disp)),
	            List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL)),
	            GeneradorFranjas.Parametros.POR_DEFECTO).generar();

	    assertEquals(1, r.ubicadas().size());
	    assertEquals(1, r.sinUbicar().size());
	    assertEquals(MotorHorario.Motivo.DOCENTE_NO_DISPONIBLE,
	                 r.sinUbicar().get(0).getCausaSinUbicar());
	}

	@Test
	void sinAula() {
	    List<Sesion> ses = List.of(new Sesion(1, 1, 1, 10, 2, TipoAula.SALA_COMPUTO, 44));
	    var r = new MotorHorario(ses, Map.of(1, new DocenteMotor(1, 40, null)),
	            List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL)),
	            GeneradorFranjas.Parametros.POR_DEFECTO).generar();

	    assertEquals(1, r.sinUbicar().size());
	    assertEquals(MotorHorario.Motivo.SIN_AULA, r.sinUbicar().get(0).getCausaSinUbicar());
	}

	@Test
	void cargaDocenteExcedida() {
	    // Tope de 2 h semanales, dos sesiones de 2 h
	    List<Sesion> ses = List.of(
	        new Sesion(1, 1, 1, 10, 2, TipoAula.CONVENCIONAL, 44),
	        new Sesion(2, 2, 1, 10, 2, TipoAula.CONVENCIONAL, 44));
	    var r = new MotorHorario(ses, Map.of(1, new DocenteMotor(1, 2, null)),
	            List.of(new AulaMotor(1, 45, TipoAula.CONVENCIONAL)),
	            GeneradorFranjas.Parametros.POR_DEFECTO).generar();

	    assertEquals(1, r.ubicadas().size());
	    assertEquals(MotorHorario.Motivo.CARGA_DOCENTE, r.sinUbicar().get(0).getCausaSinUbicar());
	}
}