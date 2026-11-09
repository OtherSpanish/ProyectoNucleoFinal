package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import unbosque.edu.co.aulasync.model.CausaDescartada;
import unbosque.edu.co.aulasync.model.PasoTraza;

/**
 * Motor de generación de horarios basado en coloreo de grafos.
 *
 * <p>
 * Cada sesión es un nodo de un {@link GrafoConflictos}. Con la heurística
 * DSatur se elige en cada paso la sesión más restringida (la que tiene más
 * franjas bloqueadas) y se le asigna la franja viable del día con menos carga.
 * Un {@link ComponenteDSU} une las sesiones de un mismo grupo para que caigan
 * en días distintos. Las reglas se evalúan con {@link ReglasHorario}, la misma
 * clase que usa el editor manual.
 * </p>
 *
 * <p>
 * Orden de evaluación (ver {@link MotivoRechazo}): disponibilidad del docente,
 * tope de horas, sede, choques con vecinos del grafo, un día por grupo y aula.
 * Es voraz: no hace backtracking. No depende de Spring ni de JPA.
 * </p>
 *
 * <p>
 * Cada decisión de DSatur queda registrada como un {@link PasoTraza} (sesión
 * elegida, saturación, grado, franjas descartadas por regla y resultado), para
 * poder explicar visualmente la ejecución. Registrar la traza no cambia el
 * resultado.
 * </p>
 *
 * @version 2.2
 */
public class MotorHorario {

	/**
	 * Resultado de una generación.
	 */
	public static class Resultado {

		private final List<Sesion> ubicadas;
		private final List<Sesion> sinUbicar;
		private final List<String> avisos;

		/**
		 * Crea el resultado.
		 *
		 * @param ubicadas  sesiones con franja y aula.
		 * @param sinUbicar sesiones que no se pudieron ubicar.
		 * @param avisos    advertencias que no impiden el horario.
		 */
		public Resultado(List<Sesion> ubicadas, List<Sesion> sinUbicar, List<String> avisos) {
			this.ubicadas = ubicadas;
			this.sinUbicar = sinUbicar;
			this.avisos = avisos;
		}

		/**
		 * @return sesiones ubicadas.
		 */
		public List<Sesion> getUbicadas() {
			return ubicadas;
		}

		/**
		 * @return sesiones sin ubicar.
		 */
		public List<Sesion> getSinUbicar() {
			return sinUbicar;
		}

		/**
		 * @return advertencias de la generación.
		 */
		public List<String> getAvisos() {
			return avisos;
		}
	}

	/** Ordena las aulas de menor a mayor capacidad (la más ajustada primero). */
	private static class ComparadorPorCapacidad implements Comparator<AulaMotor> {

		@Override
		public int compare(AulaMotor a, AulaMotor b) {
			return Integer.compare(a.getCapacidad(), b.getCapacidad());
		}
	}

	private static final int PENALIZACION_SABADO = 1;

	private final List<Sesion> sesiones;
	private final Map<Long, DocenteMotor> docentes = new HashMap<>();
	private final List<SedeMotor> sedes;
	private final Map<Long, SedeMotor> sedesPorId = new HashMap<>();
	private final List<AulaMotor> aulas;
	private final SedeMotor sedePrimerSemestre;
	private final GrafoConflictos grafo;
	private final ComponenteDSU dsu;
	private final Map<Integer, List<Integer>> miembrosPorRaiz = new HashMap<>();

	private final Map<Integer, List<Franja>> franjasPorDuracion = new HashMap<>();
	private final Map<Integer, List<DayOfWeek>> diasPorBloque = new HashMap<>();
	private final Map<Long, Integer> horasDocente = new HashMap<>();
	private final Map<Long, List<Sesion>> ocupacionAula = new HashMap<>();
	private final List<PasoTraza> pasos = new ArrayList<>();

	/**
	 * Crea el motor: construye el grafo de conflictos y une en el DSU las
	 * sesiones de cada grupo.
	 *
	 * @param sesiones sesiones por ubicar.
	 * @param docentes docentes de esas sesiones.
	 * @param sedes    sedes con sus horas habilitadas.
	 * @param aulas    aulas disponibles.
	 */
	public MotorHorario(List<Sesion> sesiones, List<DocenteMotor> docentes, List<SedeMotor> sedes,
			List<AulaMotor> aulas) {
		this.sesiones = sesiones;
		for (DocenteMotor docente : docentes) {
			this.docentes.put(docente.getId(), docente);
		}
		this.sedes = sedes;
		for (SedeMotor sede : sedes) {
			sedesPorId.put(sede.getId(), sede);
		}
		this.sedePrimerSemestre = ReglasHorario.buscarSedePrimerSemestre(sedes);
		this.aulas = new ArrayList<>(aulas);
		Collections.sort(this.aulas, new ComparadorPorCapacidad());

		this.grafo = new GrafoConflictos(sesiones);
		this.dsu = new ComponenteDSU(sesiones.size());
		Map<Long, Integer> primeraDelGrupo = new HashMap<>();
		for (int i = 0; i < sesiones.size(); i++) {
			Long grupoId = sesiones.get(i).getGrupoId();
			Integer primera = primeraDelGrupo.get(grupoId);
			if (primera == null) {
				primeraDelGrupo.put(grupoId, i);
			} else {
				dsu.unir(primera, i);
			}
		}
		for (int i = 0; i < sesiones.size(); i++) {
			int raiz = dsu.encontrar(i);
			List<Integer> miembros = miembrosPorRaiz.get(raiz);
			if (miembros == null) {
				miembros = new ArrayList<>();
				miembrosPorRaiz.put(raiz, miembros);
			}
			miembros.add(i);
		}
	}

	/**
	 * Ejecuta DSatur: en cada paso toma la sesión no procesada con mayor
	 * saturación (en empate, mayor grado; luego menor índice) y trata de
	 * ubicarla.
	 *
	 * @return sesiones ubicadas, sin ubicar y avisos.
	 */
	public Resultado generar() {
		int n = sesiones.size();
		boolean[] procesada = new boolean[n];
		List<Sesion> ubicadas = new ArrayList<>();
		List<Sesion> sinUbicar = new ArrayList<>();
		pasos.clear();

		for (int paso = 0; paso < n; paso++) {
			int elegido = -1;
			int mejorSaturacion = -1;
			int mejorGrado = -1;
			for (int i = 0; i < n; i++) {
				if (procesada[i]) {
					continue;
				}
				int saturacion = saturacion(i);
				int grado = grafo.grado(i);
				if (saturacion > mejorSaturacion || (saturacion == mejorSaturacion && grado > mejorGrado)) {
					elegido = i;
					mejorSaturacion = saturacion;
					mejorGrado = grado;
				}
			}
			procesada[elegido] = true;
			if (intentarUbicar(elegido, paso + 1, mejorSaturacion, mejorGrado)) {
				ubicadas.add(sesiones.get(elegido));
			} else {
				sinUbicar.add(sesiones.get(elegido));
			}
		}
		return new Resultado(ubicadas, sinUbicar, generarAvisos());
	}

	/**
	 * @return pasos de DSatur de la última ejecución de {@link #generar()}, en
	 *         orden.
	 */
	public List<PasoTraza> getPasos() {
		return pasos;
	}

	/**
	 * @return grafo de conflictos construido con las sesiones.
	 */
	public GrafoConflictos getGrafo() {
		return grafo;
	}

	/**
	 * @param i índice de una sesión.
	 * @return índice de la raíz de su componente en el DSU.
	 */
	public int raizDsu(int i) {
		return dsu.encontrar(i);
	}

	/**
	 * @param i índice de una sesión.
	 * @return índices de las sesiones de su misma componente (su grupo),
	 *         incluida ella.
	 */
	public List<Integer> miembrosComponente(int i) {
		return new ArrayList<>(miembrosPorRaiz.get(dsu.encontrar(i)));
	}

	/**
	 * @return cantidad de componentes del DSU (una por grupo).
	 */
	public int totalComponentes() {
		return miembrosPorRaiz.size();
	}

	/**
	 * Atajo a {@link ReglasHorario#calcularDiasPrimerSemestre(int, int)}.
	 *
	 * @param numeroGrupo número de grupo (bloque).
	 * @param cantidad    días por semana en la sede.
	 * @return días asignados, ordenados.
	 */
	public static List<DayOfWeek> calcularDiasPrimerSemestre(int numeroGrupo, int cantidad) {
		return ReglasHorario.calcularDiasPrimerSemestre(numeroGrupo, cantidad);
	}

	// ------------------------------------------------------------------
	// DSatur
	// ------------------------------------------------------------------

	private List<Franja> candidatas(Sesion sesion) {
		int duracion = sesion.getDuracionHoras();
		List<Franja> franjas = franjasPorDuracion.get(duracion);
		if (franjas == null) {
			franjas = GeneradorFranjas.generar(duracion, sedes);
			franjasPorDuracion.put(duracion, franjas);
		}
		return franjas;
	}

	private int saturacion(int i) {
		int bloqueadas = 0;
		for (Franja franja : candidatas(sesiones.get(i))) {
			if (motivoRechazo(i, franja) != null) {
				bloqueadas++;
			}
		}
		return bloqueadas;
	}

	/**
	 * Busca la franja viable del día con menos carga y le asigna aula. Deja
	 * registrado el paso en la traza.
	 *
	 * @param i          índice de la sesión elegida.
	 * @param numeroPaso número del paso de DSatur (empieza en 1).
	 * @param saturacion saturación con la que fue elegida.
	 * @param grado      grado del nodo en el grafo.
	 * @return {@code true} si se ubicó.
	 */
	private boolean intentarUbicar(int i, int numeroPaso, int saturacion, int grado) {
		Sesion sesion = sesiones.get(i);
		Map<MotivoRechazo, Integer> causas = new EnumMap<>(MotivoRechazo.class);
		Franja mejor = null;
		int mejorCarga = Integer.MAX_VALUE;
		int candidatas = 0;
		int viables = 0;

		for (Franja franja : candidatas(sesion)) {
			candidatas++;
			MotivoRechazo motivo = motivoRechazo(i, franja);
			if (motivo != null) {
				Integer actual = causas.get(motivo);
				if (actual == null) {
					causas.put(motivo, 1);
				} else {
					causas.put(motivo, actual + 1);
				}
			} else {
				viables++;
				int carga = cargaDelDia(sesion, franja.getDia());
				if (carga < mejorCarga) {
					mejor = franja;
					mejorCarga = carga;
				}
			}
		}

		PasoTraza traza = new PasoTraza(numeroPaso, i, saturacion, grado, candidatas, viables,
				listarBloqueos(causas), mejor != null);
		pasos.add(traza);

		if (mejor == null) {
			sesion.setDesgloseCausas(causas);
			sesion.setCausaSinUbicar(causaPrincipal(causas));
			traza.registrarCausa(sesion.getCausaSinUbicar());
			return false;
		}

		AulaMotor aula = buscarAula(sesion, mejor);
		sesion.setFranja(mejor);
		sesion.setAula(aula);
		traza.registrarUbicacion(mejor.getDia(), mejor.getHoraInicio(), aula.getNombre(),
				sedesPorId.get(mejor.getSedeId()).getNombre(), mejorCarga);

		List<Sesion> ocupadas = ocupacionAula.get(aula.getId());
		if (ocupadas == null) {
			ocupadas = new ArrayList<>();
			ocupacionAula.put(aula.getId(), ocupadas);
		}
		ocupadas.add(sesion);
		horasDocente.put(sesion.getDocenteId(), horasAsignadas(sesion.getDocenteId()) + sesion.getDuracionHoras());
		return true;
	}

	/**
	 * Lista las franjas descartadas por cada regla que evalúa el motor (las de
	 * {@link MotivoRechazo} hasta {@link MotivoRechazo#SIN_AULA}, en ese orden),
	 * incluidas las que no descartaron ninguna, para mostrar la revisión
	 * completa.
	 */
	private List<CausaDescartada> listarBloqueos(Map<MotivoRechazo, Integer> causas) {
		List<CausaDescartada> bloqueos = new ArrayList<>();
		for (MotivoRechazo motivo : MotivoRechazo.values()) {
			if (motivo.ordinal() > MotivoRechazo.SIN_AULA.ordinal()) {
				break;
			}
			Integer franjas = causas.get(motivo);
			bloqueos.add(new CausaDescartada(motivo, motivo.getDescripcion(), franjas == null ? 0 : franjas));
		}
		return bloqueos;
	}

	private MotivoRechazo causaPrincipal(Map<MotivoRechazo, Integer> causas) {
		MotivoRechazo principal = null;
		int maximo = -1;
		for (Map.Entry<MotivoRechazo, Integer> entrada : causas.entrySet()) {
			if (entrada.getValue() > maximo) {
				principal = entrada.getKey();
				maximo = entrada.getValue();
			}
		}
		return principal;
	}

	/**
	 * Carga de un día para una sesión: sesiones ya ubicadas ese día con el mismo
	 * docente o del mismo bloque. El sábado suma una penalización.
	 */
	private int cargaDelDia(Sesion sesion, DayOfWeek dia) {
		int carga = 0;
		if (dia == DayOfWeek.SATURDAY) {
			carga = PENALIZACION_SABADO;
		}
		for (Sesion otra : sesiones) {
			if (otra == sesion || otra.getFranja() == null || otra.getFranja().getDia() != dia) {
				continue;
			}
			if (otra.getDocenteId() == sesion.getDocenteId() || GrafoConflictos.mismoBloque(otra, sesion)) {
				carga++;
			}
		}
		return carga;
	}

	// ------------------------------------------------------------------
	// Reglas (delegadas a ReglasHorario)
	// ------------------------------------------------------------------

	/**
	 * @return {@code null} si la franja es viable; si no, el primer motivo que
	 *         falla.
	 */
	private MotivoRechazo motivoRechazo(int i, Franja franja) {
		Sesion sesion = sesiones.get(i);
		int duracion = sesion.getDuracionHoras();
		DocenteMotor docente = docentes.get(sesion.getDocenteId());

		if (docente == null || !docente.estaDisponibleEn(franja, duracion)) {
			return MotivoRechazo.DOCENTE_NO_DISPONIBLE;
		}
		if (!ReglasHorario.cargaPermitida(horasAsignadas(docente.getId()), duracion,
				docente.getTopeHorasSemanales())) {
			return MotivoRechazo.CARGA_DOCENTE;
		}
		if (!ReglasHorario.sedePermitida(sesion, sedesPorId.get(franja.getSedeId()), franja.getDia(),
				sedePrimerSemestre, diasDelBloque(sesion.getNumeroGrupo()))) {
			return MotivoRechazo.SEDE_NO_PERMITIDA;
		}
		for (int v : grafo.vecinosDe(i)) {
			MotivoRechazo choque = ReglasHorario.motivoChoque(sesion, franja, sesiones.get(v));
			if (choque != null) {
				return choque;
			}
		}
		for (int j : miembrosPorRaiz.get(dsu.encontrar(i))) {
			if (ReglasHorario.mismoDiaDelGrupo(sesion, franja, sesiones.get(j))) {
				return MotivoRechazo.MISMO_DIA_GRUPO;
			}
		}
		if (buscarAula(sesion, franja) == null) {
			return MotivoRechazo.SIN_AULA;
		}
		return null;
	}

	private List<DayOfWeek> diasDelBloque(int numeroGrupo) {
		if (sedePrimerSemestre == null) {
			return Collections.emptyList();
		}
		List<DayOfWeek> dias = diasPorBloque.get(numeroGrupo);
		if (dias == null) {
			dias = ReglasHorario.calcularDiasPrimerSemestre(numeroGrupo, sedePrimerSemestre.getDiasPrimerSemestre());
			diasPorBloque.put(numeroGrupo, dias);
		}
		return dias;
	}

	private AulaMotor buscarAula(Sesion sesion, Franja franja) {
		for (AulaMotor aula : aulas) {
			if (ReglasHorario.aulaCompatible(aula, sesion, franja)
					&& ReglasHorario.aulaLibre(sesion, franja, ocupacionAula.get(aula.getId()))) {
				return aula;
			}
		}
		return null;
	}

	private int horasAsignadas(long docenteId) {
		Integer horas = horasDocente.get(docenteId);
		if (horas == null) {
			return 0;
		}
		return horas;
	}

	// ------------------------------------------------------------------
	// Avisos
	// ------------------------------------------------------------------

	/**
	 * Revisa, para cada bloque de primer semestre con materias núcleo, cuántos
	 * días distintos quedó en la sede de primer semestre.
	 */
	private List<String> generarAvisos() {
		List<String> avisos = new ArrayList<>();
		if (sedePrimerSemestre == null) {
			return avisos;
		}
		Map<Integer, Set<DayOfWeek>> diasUsados = new TreeMap<>();
		for (Sesion sesion : sesiones) {
			if (sesion.getSemestre() != 1 || !sesion.isNucleoComun()) {
				continue;
			}
			Set<DayOfWeek> usados = diasUsados.get(sesion.getNumeroGrupo());
			if (usados == null) {
				usados = EnumSet.noneOf(DayOfWeek.class);
				diasUsados.put(sesion.getNumeroGrupo(), usados);
			}
			Franja franja = sesion.getFranja();
			if (franja != null && franja.getSedeId() == sedePrimerSemestre.getId()) {
				usados.add(franja.getDia());
			}
		}

		int requeridos = sedePrimerSemestre.getDiasPrimerSemestre();
		for (Map.Entry<Integer, Set<DayOfWeek>> entrada : diasUsados.entrySet()) {
			int usados = entrada.getValue().size();
			if (usados < requeridos) {
				avisos.add("El bloque G" + entrada.getKey() + " de primer semestre quedó con " + usados + " de "
						+ requeridos + " días en " + sedePrimerSemestre.getNombre() + " (días asignados: "
						+ diasDelBloque(entrada.getKey()) + ").");
			}
		}
		return avisos;
	}
}
