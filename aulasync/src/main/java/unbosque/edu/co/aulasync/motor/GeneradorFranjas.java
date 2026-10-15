package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class GeneradorFranjas {

	// RNF3: estos valores deben poder venir de la parametrización
	public record Parametros(int apertura, int cierreSemana, int cierreSabado, int inicioAlmuerzo, int finAlmuerzo) {
		public static final Parametros POR_DEFECTO = new Parametros(7, 22, 13, 12, 13);
	}

	public static List<Franja> generar(int duracion, Parametros p) {
		List<Franja> franjas = new ArrayList<>();
		for (DayOfWeek d : DayOfWeek.values()) {
			if (d == DayOfWeek.SUNDAY)
				continue;
			int cierre = (d == DayOfWeek.SATURDAY) ? p.cierreSabado() : p.cierreSemana();
			for (int h = p.apertura(); h + duracion <= cierre; h++) {
				boolean tocaAlmuerzo = h < p.finAlmuerzo() && h + duracion > p.inicioAlmuerzo();
				if (!tocaAlmuerzo)
					franjas.add(new Franja(d, h));
			}
		}
		return franjas;
	}
}