package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera las franjas candidatas (día y hora de inicio) en las que se puede
 * ubicar una sesión, a partir de la franja horaria general parametrizable (RD7,
 * RNF3).
 */
public class GeneradorFranjas {

	/**
	 * Parámetros de la franja horaria general: hora de apertura, cierre entre
	 * semana y en sábado, y horario de almuerzo en el que no hay clases.
	 */
	public static class Parametros {

		/**
		 * Valores por defecto: lunes a viernes 7:00 a 22:00, sábado 7:00 a 13:00, sin
		 * clases de 12:00 a 13:00.
		 */
		public static final Parametros POR_DEFECTO = new Parametros(7, 22, 13, 12, 13);

		private final int apertura;
		private final int cierreSemana;
		private final int cierreSabado;
		private final int inicioAlmuerzo;
		private final int finAlmuerzo;

		/**
		 * Crea los parámetros de la franja general.
		 *
		 * @param apertura       hora de apertura (todos los días)
		 * @param cierreSemana   hora de cierre de lunes a viernes
		 * @param cierreSabado   hora de cierre del sábado
		 * @param inicioAlmuerzo hora en que empieza el receso sin clases
		 * @param finAlmuerzo    hora en que termina el receso sin clases
		 */
		public Parametros(int apertura, int cierreSemana, int cierreSabado, int inicioAlmuerzo, int finAlmuerzo) {
			this.apertura = apertura;
			this.cierreSemana = cierreSemana;
			this.cierreSabado = cierreSabado;
			this.inicioAlmuerzo = inicioAlmuerzo;
			this.finAlmuerzo = finAlmuerzo;
		}

		/** @return hora de apertura */
		public int getApertura() {
			return apertura;
		}

		/** @return hora de cierre de lunes a viernes */
		public int getCierreSemana() {
			return cierreSemana;
		}

		/** @return hora de cierre del sábado */
		public int getCierreSabado() {
			return cierreSabado;
		}

		/** @return hora en que empieza el receso sin clases */
		public int getInicioAlmuerzo() {
			return inicioAlmuerzo;
		}

		/** @return hora en que termina el receso sin clases */
		public int getFinAlmuerzo() {
			return finAlmuerzo;
		}
	}

	/**
	 * Genera todas las franjas posibles para una sesión de la duración dada,
	 * descartando las que se pasan del cierre o tocan el receso del almuerzo.
	 *
	 * @param duracion duración de la sesión en horas
	 * @param p        parámetros de la franja general
	 * @return franjas candidatas, ordenadas por día y hora
	 */
	public static List<Franja> generar(int duracion, Parametros p) {
		List<Franja> franjas = new ArrayList<>();
		for (DayOfWeek d : DayOfWeek.values()) {
			if (d == DayOfWeek.SUNDAY) {
				continue;
			}
			int cierre = (d == DayOfWeek.SATURDAY) ? p.getCierreSabado() : p.getCierreSemana();
			for (int h = p.getApertura(); h + duracion <= cierre; h++) {
				boolean tocaAlmuerzo = h < p.getFinAlmuerzo() && h + duracion > p.getInicioAlmuerzo();
				if (!tocaAlmuerzo) {
					franjas.add(new Franja(d, h));
				}
			}
		}
		return franjas;
	}
}