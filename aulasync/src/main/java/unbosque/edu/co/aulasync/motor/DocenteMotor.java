package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;

/**
 * Docente tal como lo ve el motor: tope de horas semanales y disponibilidad
 * por hora de cada día.
 *
 * <p>
 * Un docente sin franjas habilitadas no está disponible en ninguna hora.
 * </p>
 *
 * @version 2.0
 */
public class DocenteMotor {

	private static final int DIAS = 7;
	private static final int HORAS = 24;

	private final long id;
	private final int topeHorasSemanales;
	private final boolean[][] disponible = new boolean[DIAS][HORAS];

	/**
	 * Crea un docente sin disponibilidad.
	 *
	 * @param id                 id del docente.
	 * @param topeHorasSemanales tope de su dedicación.
	 */
	public DocenteMotor(long id, int topeHorasSemanales) {
		this.id = id;
		this.topeHorasSemanales = topeHorasSemanales;
	}

	/**
	 * Marca como disponible el intervalo {@code [inicio, fin)} de un día.
	 *
	 * @param dia    día de la semana.
	 * @param inicio hora de inicio (incluida).
	 * @param fin    hora de fin (excluida).
	 */
	public void habilitar(DayOfWeek dia, int inicio, int fin) {
		int d = dia.getValue() - 1;
		for (int h = inicio; h < fin && h < HORAS; h++) {
			if (h >= 0) {
				disponible[d][h] = true;
			}
		}
	}

	/**
	 * Indica si el docente puede dictar una sesión que empieza en la franja y
	 * dura {@code duracion} horas.
	 *
	 * @param franja   franja candidata.
	 * @param duracion duración de la sesión.
	 * @return {@code true} si todas las horas de la sesión están disponibles.
	 */
	public boolean estaDisponibleEn(Franja franja, int duracion) {
		int d = franja.getDia().getValue() - 1;
		int inicio = franja.getHoraInicio();
		if (inicio < 0 || inicio + duracion > HORAS) {
			return false;
		}
		for (int h = inicio; h < inicio + duracion; h++) {
			if (!disponible[d][h]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return id del docente.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return tope de horas semanales.
	 */
	public int getTopeHorasSemanales() {
		return topeHorasSemanales;
	}
}
