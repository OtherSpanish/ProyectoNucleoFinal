package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;

/**
 * Docente tal como lo ve el motor: su tope de horas semanales según la
 * dedicación (RD5) y su disponibilidad por día y hora (RD4).
 */
public class DocenteMotor {

	private final int id;
	private final int topeHorasSemanales;
	private final Map<DayOfWeek, Set<Integer>> horasDisponibles;

	/**
	 * Crea un docente del motor.
	 *
	 * @param id                 identificador del docente
	 * @param topeHorasSemanales máximo de horas de clase por semana según su
	 *                           dedicación
	 * @param horasDisponibles   horas disponibles por día, o {@code null} si está
	 *                           siempre disponible
	 */
	public DocenteMotor(int id, int topeHorasSemanales, Map<DayOfWeek, Set<Integer>> horasDisponibles) {
		this.id = id;
		this.topeHorasSemanales = topeHorasSemanales;
		this.horasDisponibles = horasDisponibles;
	}

	/**
	 * Indica si el docente está disponible durante toda la sesión (RD4).
	 *
	 * @param f        franja de inicio de la sesión
	 * @param duracion duración de la sesión en horas
	 * @return {@code true} si todas las horas de la sesión están dentro de su
	 *         disponibilidad
	 */
	public boolean estaDisponibleEn(Franja f, int duracion) {
		if (horasDisponibles == null) {
			return true;
		}
		Set<Integer> horas = horasDisponibles.get(f.getDia());
		if (horas == null) {
			return false;
		}
		for (int h = f.getHoraInicio(); h < f.getHoraInicio() + duracion; h++) {
			if (!horas.contains(h)) {
				return false;
			}
		}
		return true;
	}

	/** @return identificador del docente */
	public int getId() {
		return id;
	}

	/** @return máximo de horas de clase por semana */
	public int getTopeHorasSemanales() {
		return topeHorasSemanales;
	}
}