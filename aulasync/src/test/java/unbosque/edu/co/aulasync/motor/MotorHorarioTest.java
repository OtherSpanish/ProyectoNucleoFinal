package unbosque.edu.co.aulasync.motor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import unbosque.edu.co.aulasync.model.TipoAula;

/**
 * Pruebas unitarias de {@link MotorHorario}. No usan base de datos ni Spring.
 */
class MotorHorarioTest {

	private static final long USAQUEN = 1L;
	private static final long CHIA = 2L;
	private static final DayOfWeek[] ENTRE_SEMANA = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };

	private List<SedeMotor> sedes;
	private List<AulaMotor> aulas;

	@BeforeEach
	void preparar() {
		SedeMotor usaquen = new SedeMotor(USAQUEN, "Usaquén", false, 0);
		SedeMotor chia = new SedeMotor(CHIA, "Chía", true, 2);
		for (DayOfWeek dia : ENTRE_SEMANA) {
			usaquen.habilitar(dia, 7, 13);
			usaquen.habilitar(dia, 14, 22);
			chia.habilitar(dia, 8, 12);
			chia.habilitar(dia, 13, 15);
		}
		usaquen.habilitar(DayOfWeek.SATURDAY, 7, 13);
		sedes = new ArrayList<>(Arrays.asList(usaquen, chia));

		aulas = new ArrayList<>();
		aulas.add(new AulaMotor(1L, "U-101", 40, TipoAula.CONVENCIONAL, USAQUEN));
		aulas.add(new AulaMotor(2L, "U-102", 40, TipoAula.CONVENCIONAL, USAQUEN));
		aulas.add(new AulaMotor(3L, "LAB-1", 30, TipoAula.SALA_COMPUTO, USAQUEN));
		aulas.add(new AulaMotor(4L, "C-101", 40, TipoAula.CONVENCIONAL, CHIA));
	}

	// ------------------------------------------------------------------
	// Pruebas
	// ------------------------------------------------------------------

	@Test
	void sesionesDelGrupoEnDiasDistintosTest() {
		List<Sesion> sesiones = grupo(1L, 10L, 1, 3, false, 1L, 3, 2, TipoAula.CONVENCIONAL, 20);
		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteTodaLaSemana(1L, 40));

		assertEquals(3, resultado.getUbicadas().size());
		Set<DayOfWeek> dias = EnumSet.noneOf(DayOfWeek.class);
		for (Sesion sesion : resultado.getUbicadas()) {
			dias.add(sesion.getFranja().getDia());
		}
		assertEquals(3, dias.size());
	}

	@Test
	void respetaAlmuerzoTest() {
		DocenteMotor docente = new DocenteMotor(1L, 40);
		docente.habilitar(DayOfWeek.MONDAY, 10, 18);
		List<Sesion> sesiones = grupo(1L, 10L, 1, 3, false, 1L, 1, 4, TipoAula.CONVENCIONAL, 20);

		MotorHorario.Resultado resultado = ejecutar(sesiones, docente);

		assertEquals(1, resultado.getUbicadas().size());
		assertEquals(14, resultado.getUbicadas().get(0).getFranja().getHoraInicio());
	}

	@Test
	void choqueDocenteTest() {
		List<Sesion> sesiones = new ArrayList<>();
		sesiones.addAll(grupo(1L, 10L, 1, 5, false, 1L, 1, 2, TipoAula.CONVENCIONAL, 20));
		sesiones.addAll(grupo(2L, 20L, 1, 8, false, 1L, 1, 2, TipoAula.CONVENCIONAL, 20));

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteSoloLunes(1L));

		assertEquals(1, resultado.getUbicadas().size());
		assertEquals(1, resultado.getSinUbicar().size());
		assertTrue(resultado.getSinUbicar().get(0).getDesgloseCausas().containsKey(MotivoRechazo.CHOQUE_DOCENTE));
	}

	@Test
	void mismoBloqueNoSeCruzaTest() {
		List<Sesion> sesiones = new ArrayList<>();
		sesiones.addAll(grupo(1L, 10L, 1, 3, false, 1L, 1, 2, TipoAula.CONVENCIONAL, 20));
		sesiones.addAll(grupo(2L, 20L, 1, 4, false, 2L, 1, 2, TipoAula.CONVENCIONAL, 20));

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteSoloLunes(1L), docenteSoloLunes(2L));

		assertEquals(1, resultado.getSinUbicar().size());
		assertTrue(resultado.getSinUbicar().get(0).getDesgloseCausas().containsKey(MotivoRechazo.CHOQUE_BLOQUE));
	}

	@Test
	void distintoNumeroDeGrupoPuedeCruzarseTest() {
		List<Sesion> sesiones = new ArrayList<>();
		sesiones.addAll(grupo(1L, 10L, 1, 3, false, 1L, 1, 2, TipoAula.CONVENCIONAL, 20));
		sesiones.addAll(grupo(2L, 20L, 2, 3, false, 2L, 1, 2, TipoAula.CONVENCIONAL, 20));

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteSoloLunes(1L), docenteSoloLunes(2L));

		assertEquals(2, resultado.getUbicadas().size());
		Sesion a = resultado.getUbicadas().get(0);
		Sesion b = resultado.getUbicadas().get(1);
		assertEquals(a.getFranja().getHoraInicio(), b.getFranja().getHoraInicio());
		assertNotEquals(a.getAula().getId(), b.getAula().getId());
	}

	@Test
	void mismaMateriaDistintoGrupoPuedeCruzarseTest() {
		List<Sesion> sesiones = new ArrayList<>();
		sesiones.addAll(grupo(1L, 10L, 1, 3, false, 1L, 1, 2, TipoAula.CONVENCIONAL, 20));
		sesiones.addAll(grupo(2L, 10L, 2, 3, false, 2L, 1, 2, TipoAula.CONVENCIONAL, 20));

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteSoloLunes(1L), docenteSoloLunes(2L));

		assertEquals(2, resultado.getUbicadas().size());
	}

	@Test
	void nucleoPrimerSemestreVaAChiaEnSusDiasTest() {
		List<Sesion> sesiones = grupo(1L, 10L, 1, 1, true, 1L, 2, 2, TipoAula.CONVENCIONAL, 20);

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteTodaLaSemana(1L, 40));

		assertEquals(2, resultado.getUbicadas().size());
		Set<DayOfWeek> dias = EnumSet.noneOf(DayOfWeek.class);
		for (Sesion sesion : resultado.getUbicadas()) {
			assertEquals(CHIA, sesion.getFranja().getSedeId());
			dias.add(sesion.getFranja().getDia());
		}
		assertEquals(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), dias);
		assertTrue(resultado.getAvisos().isEmpty());
	}

	@Test
	void propiaPrimerSemestreVaAUsaquenOtrosDiasTest() {
		List<Sesion> sesiones = grupo(1L, 10L, 1, 1, false, 1L, 1, 2, TipoAula.CONVENCIONAL, 20);

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteTodaLaSemana(1L, 40));

		Franja franja = resultado.getUbicadas().get(0).getFranja();
		assertEquals(USAQUEN, franja.getSedeId());
		assertEquals(DayOfWeek.TUESDAY, franja.getDia());
	}

	@Test
	void otroSemestreNoVaAChiaTest() {
		List<Sesion> sesiones = grupo(1L, 10L, 1, 3, false, 1L, 2, 2, TipoAula.CONVENCIONAL, 20);

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteTodaLaSemana(1L, 40));

		for (Sesion sesion : resultado.getUbicadas()) {
			assertEquals(USAQUEN, sesion.getFranja().getSedeId());
		}
	}

	@Test
	void sinAulaTest() {
		List<Sesion> sesiones = grupo(1L, 10L, 1, 3, false, 1L, 1, 2, TipoAula.SALA_COMPUTO, 50);

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteTodaLaSemana(1L, 40));

		assertEquals(1, resultado.getSinUbicar().size());
		assertEquals(MotivoRechazo.SIN_AULA, resultado.getSinUbicar().get(0).getCausaSinUbicar());
	}

	@Test
	void cargaDocenteTest() {
		List<Sesion> sesiones = grupo(1L, 10L, 1, 3, false, 1L, 3, 2, TipoAula.CONVENCIONAL, 20);

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteTodaLaSemana(1L, 4));

		assertEquals(2, resultado.getUbicadas().size());
		assertEquals(MotivoRechazo.CARGA_DOCENTE, resultado.getSinUbicar().get(0).getCausaSinUbicar());
	}

	@Test
	void avisoDiasChiaIncompletosTest() {
		List<Sesion> sesiones = grupo(1L, 10L, 1, 1, true, 1L, 1, 2, TipoAula.CONVENCIONAL, 20);

		MotorHorario.Resultado resultado = ejecutar(sesiones, docenteTodaLaSemana(1L, 40));

		assertEquals(1, resultado.getAvisos().size());
		assertFalse(resultado.getAvisos().get(0).isBlank());
	}

	@Test
	void diasPrimerSemestrePorBloqueTest() {
		assertEquals(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
				MotorHorario.calcularDiasPrimerSemestre(1, 2));
		assertEquals(Arrays.asList(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
				MotorHorario.calcularDiasPrimerSemestre(2, 2));
		assertEquals(Arrays.asList(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
				MotorHorario.calcularDiasPrimerSemestre(3, 2));
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private MotorHorario.Resultado ejecutar(List<Sesion> sesiones, DocenteMotor... docentes) {
		return new MotorHorario(sesiones, Arrays.asList(docentes), sedes, aulas).generar();
	}

	private List<Sesion> grupo(long grupoId, long cursoId, int numeroGrupo, int semestre, boolean nucleo,
			long docenteId, int cantidad, int duracion, TipoAula tipo, int estudiantes) {
		List<Sesion> sesiones = new ArrayList<>();
		for (int i = 1; i <= cantidad; i++) {
			sesiones.add(new Sesion(grupoId, i, cursoId, "C" + cursoId, numeroGrupo, semestre, nucleo, docenteId,
					duracion, tipo, estudiantes));
		}
		return sesiones;
	}

	private DocenteMotor docenteTodaLaSemana(long id, int tope) {
		DocenteMotor docente = new DocenteMotor(id, tope);
		for (DayOfWeek dia : ENTRE_SEMANA) {
			docente.habilitar(dia, 7, 22);
		}
		docente.habilitar(DayOfWeek.SATURDAY, 7, 13);
		return docente;
	}

	private DocenteMotor docenteSoloLunes(long id) {
		DocenteMotor docente = new DocenteMotor(id, 40);
		docente.habilitar(DayOfWeek.MONDAY, 7, 9);
		return docente;
	}
}
