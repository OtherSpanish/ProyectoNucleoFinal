package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;

/**
 * Franja candidata para una sesión: día, hora de inicio y sede. Es el "color"
 * con el que se colorea cada nodo del grafo de conflictos. Es inmutable.
 *
 * @version 2.0
 */
public class Franja {

	private final DayOfWeek dia;
	private final int horaInicio;
	private final long sedeId;

	/**
	 * Crea una franja.
	 *
	 * @param dia        día de la semana.
	 * @param horaInicio hora de inicio (0-23).
	 * @param sedeId     id de la sede donde se dictaría.
	 */
	public Franja(DayOfWeek dia, int horaInicio, long sedeId) {
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.sedeId = sedeId;
	}

	/**
	 * @return día de la semana.
	 */
	public DayOfWeek getDia() {
		return dia;
	}

	/**
	 * @return hora de inicio.
	 */
	public int getHoraInicio() {
		return horaInicio;
	}

	/**
	 * @return id de la sede.
	 */
	public long getSedeId() {
		return sedeId;
	}

	/**
	 * Indica si dos sesiones se cruzan en el tiempo, sin importar la sede (un
	 * docente o un estudiante no pueden estar en dos lugares a la vez).
	 *
	 * @param a    franja de la primera sesión.
	 * @param durA duración de la primera sesión, en horas.
	 * @param b    franja de la segunda sesión.
	 * @param durB duración de la segunda sesión, en horas.
	 * @return {@code true} si son el mismo día y sus intervalos se traslapan.
	 */
	public static boolean traslapan(Franja a, int durA, Franja b, int durB) {
		return a.getDia() == b.getDia() && a.getHoraInicio() < b.getHoraInicio() + durB
				&& b.getHoraInicio() < a.getHoraInicio() + durA;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Franja)) {
			return false;
		}
		Franja otra = (Franja) o;
		return dia == otra.dia && horaInicio == otra.horaInicio && sedeId == otra.sedeId;
	}

	@Override
	public int hashCode() {
		int resultado = dia.hashCode();
		resultado = 31 * resultado + horaInicio;
		resultado = 31 * resultado + Long.hashCode(sedeId);
		return resultado;
	}

	@Override
	public String toString() {
		return "Franja[" + dia + " " + horaInicio + ":00, sede " + sedeId + "]";
	}
}
