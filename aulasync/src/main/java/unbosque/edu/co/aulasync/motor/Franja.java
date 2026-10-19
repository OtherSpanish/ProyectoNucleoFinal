package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;

/**
 * Franja horaria de inicio de una sesión: un día de la semana y una hora de
 * inicio.
 *
 * <p>
 * Es inmutable. Dos sesiones se traslapan según su duración, no solo si
 * comparten la misma hora de inicio.
 * </p>
 */
public class Franja {

	private final DayOfWeek dia;
	private final int horaInicio;

	/**
	 * Crea una franja.
	 *
	 * @param dia        día de la semana
	 * @param horaInicio hora de inicio (por ejemplo 7 para las 7:00 a. m.)
	 */
	public Franja(DayOfWeek dia, int horaInicio) {
		this.dia = dia;
		this.horaInicio = horaInicio;
	}

	/** @return día de la semana de la franja */
	public DayOfWeek getDia() {
		return dia;
	}

	/** @return hora de inicio de la franja */
	public int getHoraInicio() {
		return horaInicio;
	}

	/**
	 * Indica si dos sesiones se traslapan en el tiempo.
	 *
	 * @param a    franja de la primera sesión
	 * @param durA duración de la primera sesión en horas
	 * @param b    franja de la segunda sesión
	 * @param durB duración de la segunda sesión en horas
	 * @return {@code true} si caen el mismo día y sus intervalos se cruzan
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
		return dia == otra.dia && horaInicio == otra.horaInicio;
	}

	@Override
	public int hashCode() {
		return 31 * dia.hashCode() + horaInicio;
	}

	@Override
	public String toString() {
		return "Franja[dia=" + dia + ", horaInicio=" + horaInicio + "]";
	}
}