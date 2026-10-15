package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MotorHorario {

	public enum Motivo {
		DOCENTE_NO_DISPONIBLE, CARGA_DOCENTE, CHOQUE_DOCENTE, CHOQUE_GRUPO, MISMO_DIA_CURSO, SIN_AULA
	}

	public record Resultado(List<Sesion> ubicadas, List<Sesion> sinUbicar) {
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

	public MotorHorario(List<Sesion> sesiones, Map<Integer, DocenteMotor> docentes, List<AulaMotor> aulas,
			GeneradorFranjas.Parametros params) {
		this.sesiones = sesiones;
		this.docentes = docentes;
		this.params = params;
		this.aulas = new ArrayList<>(aulas);
		this.aulas.sort(Comparator.comparingInt(AulaMotor::getCapacidad)); // la más ajustada primero

		this.grafo = new GrafoConflictos(sesiones);
		this.dsu = new ComponenteDSU(sesiones.size());
		Map<Integer, Integer> primeraDeCurso = new HashMap<>();
		for (int i = 0; i < sesiones.size(); i++) {
			Integer p = primeraDeCurso.putIfAbsent(sesiones.get(i).getClaveCurso(), i);
			if (p != null)
				dsu.unir(p, i);
		}
	}

	public Resultado generar() {
		int n = sesiones.size();
		boolean[] procesada = new boolean[n];
		List<Sesion> ubicadas = new ArrayList<>(), sinUbicar = new ArrayList<>();

		for (int paso = 0; paso < n; paso++) {
			// DSatur: mayor saturación, luego mayor grado, luego menor índice
			int elegido = -1, mejorSat = -1;
			for (int i = 0; i < n; i++) {
				if (procesada[i])
					continue;
				int sat = saturacion(i);
				if (sat > mejorSat || (sat == mejorSat && grafo.grado(i) > grafo.grado(elegido))) {
					elegido = i;
					mejorSat = sat;
				}
			}
			procesada[elegido] = true;
			if (intentarUbicar(elegido))
				ubicadas.add(sesiones.get(elegido));
			else
				sinUbicar.add(sesiones.get(elegido));
		}
		return new Resultado(ubicadas, sinUbicar);
	}

	private List<Franja> candidatas(Sesion s) {
		return franjasPorDuracion.computeIfAbsent(s.getDuracionHoras(), d -> GeneradorFranjas.generar(d, params));
	}

	private int saturacion(int i) {
		int c = 0;
		for (Franja f : candidatas(sesiones.get(i)))
			if (motivoRechazo(i, f) != null)
				c++;
		return c;
	}

	private boolean intentarUbicar(int i) {
		Sesion s = sesiones.get(i);
		Map<Motivo, Integer> causas = new EnumMap<>(Motivo.class);
		Franja mejor = null;
		int mejorCarga = Integer.MAX_VALUE;

		for (Franja f : candidatas(s)) {
			Motivo m = motivoRechazo(i, f);
			if (m != null) {
				causas.merge(m, 1, Integer::sum);
				continue;
			}
			int carga = cargaDelDia(s, f.dia());
			if (carga < mejorCarga) { // "<" estricto: en empate gana la franja más temprana
				mejor = f;
				mejorCarga = carga;
			}
		}

		if (mejor != null) {
			AulaMotor aula = buscarAula(s, mejor);
			s.setFranja(mejor);
			s.setAula(aula);
			ocupacionAula.computeIfAbsent(aula.getId(), k -> new ArrayList<>()).add(s);
			horasDocente.merge(s.getDocenteId(), s.getDuracionHoras(), Integer::sum);
			return true;
		}

		s.setDesgloseCausas(causas);
		s.setCausaSinUbicar(
				causas.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null));
		return false;
	}

	/**
	 * Sesiones ya ubicadas ese día del mismo grupo o docente; el sábado pesa 1
	 * extra.
	 */
	private int cargaDelDia(Sesion s, DayOfWeek dia) {
		int c = (dia == DayOfWeek.SATURDAY) ? 1 : 0;
		for (Sesion o : sesiones) {
			if (o == s || o.getFranja() == null || o.getFranja().dia() != dia)
				continue;
			if (o.getGrupoId() == s.getGrupoId() || o.getDocenteId() == s.getDocenteId())
				c++;
		}
		return c;
	}

	/** null = la franja es viable; si no, el motivo por el que se rechaza. */
	private Motivo motivoRechazo(int i, Franja f) {
		Sesion s = sesiones.get(i);
		int dur = s.getDuracionHoras();
		DocenteMotor doc = docentes.get(s.getDocenteId());

		if (!doc.estaDisponibleEn(f, dur))
			return Motivo.DOCENTE_NO_DISPONIBLE; // RD4
		if (horasDocente.getOrDefault(doc.getId(), 0) + dur > doc.getTopeHorasSemanales())
			return Motivo.CARGA_DOCENTE; // RD5

		for (int v : grafo.vecinosDe(i)) { // RD1, RD2
			Sesion o = sesiones.get(v);
			if (o.getFranja() != null && Franja.traslapan(f, dur, o.getFranja(), o.getDuracionHoras()))
				return o.getDocenteId() == s.getDocenteId() ? Motivo.CHOQUE_DOCENTE : Motivo.CHOQUE_GRUPO;
		}

		int raiz = dsu.encontrar(i); // RD6
		for (int j = 0; j < sesiones.size(); j++) {
			if (j == i || dsu.encontrar(j) != raiz)
				continue;
			Franja otra = sesiones.get(j).getFranja();
			if (otra != null && otra.dia() == f.dia())
				return Motivo.MISMO_DIA_CURSO;
		}

		if (buscarAula(s, f) == null)
			return Motivo.SIN_AULA; // RD3, RD9
		return null;
	}

	private AulaMotor buscarAula(Sesion s, Franja f) {
		for (AulaMotor a : aulas) {
			if (a.getTipo() != s.getTipoAula() || a.getCapacidad() < s.getCapacidadRequerida())
				continue;
			boolean libre = true;
			for (Sesion o : ocupacionAula.getOrDefault(a.getId(), List.of()))
				if (Franja.traslapan(f, s.getDuracionHoras(), o.getFranja(), o.getDuracionHoras())) {
					libre = false;
					break;
				}
			if (libre)
				return a;
		}
		return null;
	}
}