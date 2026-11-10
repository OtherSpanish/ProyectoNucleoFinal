package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;

/**
 * Sede tal como la ve el motor: horas habilitadas para clase en cada día (los
 * huecos, como el almuerzo, quedan deshabilitados) y la regla de primer
 * semestre.
 *
 * <p>
 * Las clases solo pueden empezar al inicio de un bloque fijo de
 * {@value #DURACION_BLOQUE} horas. Los bloques salen de cada franja de la sede,
 * contados desde su hora de inicio: la franja 7–13 da 7–9, 9–11 y 11–13; la
 * franja 8–12 da 8–10 y 10–12. Una sesión de 4 horas ocupa dos bloques
 * seguidos de la misma franja.
 * </p>
 *
 * @version 1.1
 */
public class SedeMotor {

	private static final int DIAS = 7;
	private static final int HORAS = 24;

	/** Duración en horas de cada bloque de clase. */
	public static final int DURACION_BLOQUE = 2;

	private final long id;
	private final String nombre;
	private final boolean exclusivaPrimerSemestre;
	private final int diasPrimerSemestre;
	private final boolean[][] habilitado = new boolean[DIAS][HORAS];
	private final boolean[][] inicioBloque = new boolean[DIAS][HORAS];

	/**
	 * Crea una sede sin horas habilitadas.
	 *
	 * @param id                      id de la sede.
	 * @param nombre                  nombre de la sede.
	 * @param exclusivaPrimerSemestre si solo admite primer semestre.
	 * @param diasPrimerSemestre      días por semana que cada bloque de primer
	 *                                semestre debe tener aquí.
	 */
	public SedeMotor(long id, String nombre, boolean exclusivaPrimerSemestre, int diasPrimerSemestre) {
		this.id = id;
		this.nombre = nombre;
		this.exclusivaPrimerSemestre = exclusivaPrimerSemestre;
		this.diasPrimerSemestre = diasPrimerSemestre;
	}

	/**
	 * Habilita para clase el intervalo {@code [inicio, fin)} de un día y marca
	 * dónde empieza cada bloque de {@value #DURACION_BLOQUE} horas dentro de él.
	 *
	 * @param dia    día de la semana.
	 * @param inicio hora de inicio (incluida).
	 * @param fin    hora de fin (excluida).
	 */
	public void habilitar(DayOfWeek dia, int inicio, int fin) {
		int d = dia.getValue() - 1;
		for (int h = inicio; h < fin && h < HORAS; h++) {
			if (h >= 0) {
				habilitado[d][h] = true;
			}
		}
		for (int h = inicio; h + DURACION_BLOQUE <= fin && h < HORAS; h += DURACION_BLOQUE) {
			if (h >= 0) {
				inicioBloque[d][h] = true;
			}
		}
	}

	/**
	 * Indica si en la sede cabe una sesión que empieza a {@code inicio} y dura
	 * {@code duracion} horas: debe empezar al inicio de un bloque, cubrir
	 * bloques completos y seguidos, y no tocar horas deshabilitadas (como el
	 * almuerzo).
	 *
	 * @param dia      día de la semana.
	 * @param inicio   hora de inicio.
	 * @param duracion duración en horas.
	 * @return {@code true} si la sesión ocupa bloques completos de la sede.
	 */
	public boolean permite(DayOfWeek dia, int inicio, int duracion) {
		int d = dia.getValue() - 1;
		if (inicio < 0 || inicio + duracion > HORAS || duracion % DURACION_BLOQUE != 0) {
			return false;
		}
		for (int h = inicio; h < inicio + duracion; h += DURACION_BLOQUE) {
			if (!inicioBloque[d][h]) {
				return false;
			}
		}
		for (int h = inicio; h < inicio + duracion; h++) {
			if (!habilitado[d][h]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return id de la sede.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return nombre de la sede.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @return {@code true} si solo admite primer semestre.
	 */
	public boolean isExclusivaPrimerSemestre() {
		return exclusivaPrimerSemestre;
	}

	/**
	 * @return días por semana que cada bloque de primer semestre debe tener aquí.
	 */
	public int getDiasPrimerSemestre() {
		return diasPrimerSemestre;
	}
}
