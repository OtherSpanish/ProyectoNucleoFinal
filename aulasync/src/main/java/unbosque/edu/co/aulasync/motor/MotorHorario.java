package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Motor de generación de horarios basado en coloreo de grafos.
 *
 * <p>
 * Cada sesión es un nodo de un {@link GrafoConflictos}; dos sesiones quedan
 * unidas si comparten docente o grupo. Con la heurística DSatur se elige en
 * cada paso la sesión más restringida y se le asigna la mejor franja viable. Un
 * {@link ComponenteDSU} agrupa las sesiones de un mismo curso para impedir que
 * repita día (RD6), y las aulas se asignan en un segundo paso según tipo,
 * capacidad y disponibilidad (RD3, RD9).
 * </p>
 *
 * <p>
 * Esta clase no depende de Spring ni de JPA, por lo que puede probarse de forma
 * aislada.
 * </p>
 */
public class MotorHorario {

	/** Motivos por los que una franja se descarta para una sesión. */
	public enum Motivo {
		DOCENTE_NO_DISPONIBLE, CARGA_DOCENTE, CHOQUE_DOCENTE, CHOQUE_GRUPO, MISMO_DIA_CURSO, SIN_AULA
	}

	/**
	 * Resultado de una generación: las sesiones que se lograron ubicar y las que
	 * quedaron sin ubicar (estas últimas con su causa y desglose).
	 */
	public static class Resultado {

		private final List<Sesion> ubicadas;
		private final List<Sesion> sinUbicar;

		/**
		 * Crea el resultado de una generación.
		 *
		 * @param ubicadas  sesiones con franja y aula asignadas
		 * @param sinUbicar sesiones que no se pudieron ubicar
		 */
		public Resultado(List<Sesion> ubicadas, List<Sesion> sinUbicar) {
			this.ubicadas = ubicadas;
			this.sinUbicar = sinUbicar;
		}

		/** @return sesiones ubicadas */
		public List<Sesion> getUbicadas() {
			return ubicadas;
		}

		/** @return sesiones que no se pudieron ubicar */
		public List<Sesion> getSinUbicar() {
			return sinUbicar;
		}
	}

	/** Ordena las aulas de menor a mayor capacidad (la más ajustada primero). */
	private static class ComparadorPorCapacidad implements Comparator<AulaMotor> {

		@Override
		public int compare(AulaMotor a, AulaMotor b) {
			return Integer.compare(a.getCapacidad(), b.getCapacidad());
		}
	}

	private final List<Sesion> sesiones;
	private final Map<Integer, DocenteMotor> docentes;
	private final List<AulaMotor> aulas;
	private final GeneradorFranjas.Parametros params;
	private final GrafoConflictos grafo;
	private final ComponenteDSU dsu;

	private final Map<Integer, List<Franja>> franjasPorDuracion = new HashMap<>();
	private final Map<Integer, Integer> horasDocente = new HashMap<>();
	private final Map<Integer, List<Sesion>> ocupacionAula = new HashMap<>();

	/**
	 * Crea el motor y construye el grafo de conflictos y los grupos de sesiones por
	 * curso.
	 *
	 * @param sesiones sesiones por ubicar
	 * @param docentes docentes del motor, indexados por identificador
	 * @param aulas    aulas disponibles
	 * @param params   parámetros de la franja horaria general
	 */
	public MotorHorario(List<Sesion> sesiones, Map<Integer, DocenteMotor> docentes, List<AulaMotor> aulas,
			GeneradorFranjas.Parametros params) {
		this.sesiones = sesiones;
		this.docentes = docentes;
		this.params = params;
		this.aulas = new ArrayList<>(aulas);
		Collections.sort(this.aulas, new ComparadorPorCapacidad());

		this.grafo = new GrafoConflictos(sesiones);
		this.dsu = new ComponenteDSU(sesiones.size());
		Map<Integer, Integer> primeraDeCurso = new HashMap<>();
		for (int i = 0; i < sesiones.size(); i++) {
			Integer p = primeraDeCurso.putIfAbsent(sesiones.get(i).getClaveCurso(), i);
			if (p != null) {
				dsu.unir(p, i);
			}
		}
	}

	/**
	 * Ejecuta la heurística DSatur: en cada paso toma la sesión más restringida y
	 * trata de ubicarla.
	 *
	 * @return sesiones ubicadas y sin ubicar
	 */
	public Resultado generar() {
		int n = sesiones.size();
		boolean[] procesada = new boolean[n];
		List<Sesion> ubicadas = new ArrayList<>();
		List<Sesion> sinUbicar = new ArrayList<>();

		for (int paso = 0; paso < n; paso++) {
			// DSatur: mayor saturación, luego mayor grado, luego menor índice
			int elegido = -1;
			int mejorSat = -1;
			for (int i = 0; i < n; i++) {
				if (procesada[i]) {
					continue;
				}
				int sat = saturacion(i);
				if (sat > mejorSat || (sat == mejorSat && grafo.grado(i) > grafo.grado(elegido))) {
					elegido = i;
					mejorSat = sat;
				}
			}
			procesada[elegido] = true;
			if (intentarUbicar(elegido)) {
				ubicadas.add(sesiones.get(elegido));
			} else {
				sinUbicar.add(sesiones.get(elegido));
			}
		}
		return new Resultado(ubicadas, sinUbicar);
	}

	/**
	 * Devuelve las franjas candidatas para la duración de una sesión, generándolas
	 * una sola vez por duración.
	 */
	private List<Franja> candidatas(Sesion s) {
		int duracion = s.getDuracionHoras();
		List<Franja> franjas = franjasPorDuracion.get(duracion);
		if (franjas == null) {
			franjas = GeneradorFranjas.generar(duracion, params);
			franjasPorDuracion.put(duracion, franjas);
		}
		return franjas;
	}

	/**
	 * Saturación de una sesión: cantidad de franjas candidatas que ya no son
	 * viables.
	 */
	private int saturacion(int i) {
		int c = 0;
		for (Franja f : candidatas(sesiones.get(i))) {
			if (motivoRechazo(i, f) != null) {
				c++;
			}
		}
		return c;
	}

	/**
	 * Intenta ubicar una sesión. Entre las franjas viables elige la del día con
	 * menos carga (la más temprana en caso de empate) y le asigna aula. Si ninguna
	 * sirve, guarda la causa principal y el desglose de franjas descartadas por
	 * motivo.
	 *
	 * @param i índice de la sesión
	 * @return {@code true} si se ubicó, {@code false} si quedó sin ubicar
	 */
	private boolean intentarUbicar(int i) {
		Sesion s = sesiones.get(i);
		Map<Motivo, Integer> causas = new EnumMap<>(Motivo.class);
		Franja mejor = null;
		int mejorCarga = Integer.MAX_VALUE;

		for (Franja f : candidatas(s)) {
			Motivo m = motivoRechazo(i, f);
			if (m != null) {
				Integer actual = causas.get(m);
				causas.put(m, actual == null ? 1 : actual + 1);
				continue;
			}
			int carga = cargaDelDia(s, f.getDia());
			if (carga < mejorCarga) { // "<" estricto: en empate gana la franja más temprana
				mejor = f;
				mejorCarga = carga;
			}
		}

		if (mejor != null) {
			AulaMotor aula = buscarAula(s, mejor);
			s.setFranja(mejor);
			s.setAula(aula);

			List<Sesion> ocupadas = ocupacionAula.get(aula.getId());
			if (ocupadas == null) {
				ocupadas = new ArrayList<>();
				ocupacionAula.put(aula.getId(), ocupadas);
			}
			ocupadas.add(s);

			int horasActuales = horasDocente.getOrDefault(s.getDocenteId(), 0);
			horasDocente.put(s.getDocenteId(), horasActuales + s.getDuracionHoras());
			return true;
		}

		s.setDesgloseCausas(causas);
		s.setCausaSinUbicar(causaPrincipal(causas));
		return false;
	}

	/**
	 * Devuelve el motivo que descartó más franjas. En caso de empate gana el
	 * primero según el orden del enumerado.
	 *
	 * @param causas cantidad de franjas descartadas por cada motivo
	 * @return motivo principal, o {@code null} si no hay causas
	 */
	private Motivo causaPrincipal(Map<Motivo, Integer> causas) {
		Motivo principal = null;
		int maximo = -1;
		for (Map.Entry<Motivo, Integer> e : causas.entrySet()) {
			if (e.getValue() > maximo) {
				principal = e.getKey();
				maximo = e.getValue();
			}
		}
		return principal;
	}

	/**
	 * Sesiones ya ubicadas ese día del mismo grupo o docente; el sábado pesa 1
	 * extra.
	 */
	private int cargaDelDia(Sesion s, DayOfWeek dia) {
		int c = (dia == DayOfWeek.SATURDAY) ? 1 : 0;
		for (Sesion o : sesiones) {
			if (o == s || o.getFranja() == null || o.getFranja().getDia() != dia) {
				continue;
			}
			if (o.getGrupoId() == s.getGrupoId() || o.getDocenteId() == s.getDocenteId()) {
				c++;
			}
		}
		return c;
	}

	/** null = la franja es viable; si no, el motivo por el que se rechaza. */
	private Motivo motivoRechazo(int i, Franja f) {
		Sesion s = sesiones.get(i);
		int dur = s.getDuracionHoras();
		DocenteMotor doc = docentes.get(s.getDocenteId());

		if (!doc.estaDisponibleEn(f, dur)) {
			return Motivo.DOCENTE_NO_DISPONIBLE; // RD4
		}
		if (horasDocente.getOrDefault(doc.getId(), 0) + dur > doc.getTopeHorasSemanales()) {
			return Motivo.CARGA_DOCENTE; // RD5
		}

		for (int v : grafo.vecinosDe(i)) { // RD1, RD2
			Sesion o = sesiones.get(v);
			if (o.getFranja() != null && Franja.traslapan(f, dur, o.getFranja(), o.getDuracionHoras())) {
				return o.getDocenteId() == s.getDocenteId() ? Motivo.CHOQUE_DOCENTE : Motivo.CHOQUE_GRUPO;
			}
		}

		int raiz = dsu.encontrar(i); // RD6
		for (int j = 0; j < sesiones.size(); j++) {
			if (j == i || dsu.encontrar(j) != raiz) {
				continue;
			}
			Franja otra = sesiones.get(j).getFranja();
			if (otra != null && otra.getDia() == f.getDia()) {
				return Motivo.MISMO_DIA_CURSO;
			}
		}

		if (buscarAula(s, f) == null) {
			return Motivo.SIN_AULA; // RD3, RD9
		}
		return null;
	}

	/**
	 * Busca el aula más ajustada que tenga el tipo y la capacidad requeridos y esté
	 * libre en la franja dada.
	 *
	 * @return aula disponible, o {@code null} si no hay ninguna
	 */
	private AulaMotor buscarAula(Sesion s, Franja f) {
		for (AulaMotor a : aulas) {
			if (a.getTipo() != s.getTipoAula() || a.getCapacidad() < s.getCapacidadRequerida()) {
				continue;
			}
			boolean libre = true;
			for (Sesion o : ocupacionAula.getOrDefault(a.getId(), List.of())) {
				if (Franja.traslapan(f, s.getDuracionHoras(), o.getFranja(), o.getDuracionHoras())) {
					libre = false;
					break;
				}
			}
			if (libre) {
				return a;
			}
		}
		return null;
	}
}