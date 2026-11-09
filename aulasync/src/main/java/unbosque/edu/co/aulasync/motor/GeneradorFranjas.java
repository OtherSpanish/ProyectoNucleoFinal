package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera las franjas candidatas (día, hora, sede) donde cabe una sesión de una
 * duración dada, según las horas habilitadas de cada sede.
 *
 * <p>
 * Las franjas salen ordenadas por día (lunes a sábado), luego por hora y luego
 * por sede, para que en caso de empate el motor prefiera la más temprana. Una
 * sesión nunca cruza un hueco de la sede (por ejemplo, el almuerzo).
 * </p>
 *
 * @version 2.0
 */
public final class GeneradorFranjas {

	private static final DayOfWeek[] DIAS_HABILES = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY };

	private static final int ULTIMA_HORA = 23;

	private GeneradorFranjas() {
		// Clase utilitaria: no se instancia.
	}

	/**
	 * Genera todas las franjas posibles para una duración.
	 *
	 * @param duracion duración de la sesión, en horas.
	 * @param sedes    sedes con sus horas habilitadas.
	 * @return franjas candidatas ordenadas por día, hora y sede.
	 */
	public static List<Franja> generar(int duracion, List<SedeMotor> sedes) {
		List<Franja> franjas = new ArrayList<>();
		for (DayOfWeek dia : DIAS_HABILES) {
			for (int hora = 0; hora + duracion <= ULTIMA_HORA + 1; hora++) {
				for (SedeMotor sede : sedes) {
					if (sede.permite(dia, hora, duracion)) {
						franjas.add(new Franja(dia, hora, sede.getId()));
					}
				}
			}
		}
		return franjas;
	}
}
