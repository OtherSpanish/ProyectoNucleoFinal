package unbosque.edu.co.aulasync.model;

import java.time.DayOfWeek;

/**
 * Límites generales de la jornada de la universidad, comunes a todas las
 * sedes.
 *
 * <ul>
 * <li>Lunes a viernes: de {@value #APERTURA}:00 a
 * {@value #CIERRE_ENTRE_SEMANA}:00.</li>
 * <li>Sábado: de {@value #APERTURA}:00 a {@value #CIERRE_SABADO}:00.</li>
 * <li>Domingo: no hay clases.</li>
 * </ul>
 *
 * <p>
 * Las franjas de cada sede (con sus horas de almuerzo) se definen aparte y
 * siempre caben dentro de estos límites.
 * </p>
 *
 * @version 1.0
 */
public final class JornadaInstitucional {

	/** Hora de apertura, cualquier día hábil. */
	public static final int APERTURA = 7;

	/** Hora de cierre de lunes a viernes. */
	public static final int CIERRE_ENTRE_SEMANA = 22;

	/** Hora de cierre del sábado. */
	public static final int CIERRE_SABADO = 13;

	private JornadaInstitucional() {
		// Clase de constantes: no se instancia.
	}

	/**
	 * Indica si el día tiene clases.
	 *
	 * @param dia día de la semana.
	 * @return {@code true} de lunes a sábado; {@code false} el domingo o si es
	 *         {@code null}.
	 */
	public static boolean esDiaHabil(DayOfWeek dia) {
		return dia != null && dia != DayOfWeek.SUNDAY;
	}

	/**
	 * Hora de cierre del día.
	 *
	 * @param dia día hábil.
	 * @return {@value #CIERRE_SABADO} el sábado, {@value #CIERRE_ENTRE_SEMANA}
	 *         los demás días.
	 */
	public static int horaCierre(DayOfWeek dia) {
		if (dia == DayOfWeek.SATURDAY) {
			return CIERRE_SABADO;
		}
		return CIERRE_ENTRE_SEMANA;
	}
}
