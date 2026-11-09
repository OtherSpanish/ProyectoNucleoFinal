package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;

/**
 * Sede tal como la ve el motor: horas habilitadas para clase en cada día (los
 * huecos, como el almuerzo, quedan deshabilitados) y la regla de primer
 * semestre.
 *
 * @version 1.0
 */
public class SedeMotor {

	private static final int DIAS = 7;
	private static final int HORAS = 24;

	private final long id;
	private final String nombre;
	private final boolean exclusivaPrimerSemestre;
	private final int diasPrimerSemestre;
	private final boolean[][] habilitado = new boolean[DIAS][HORAS];

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
	 * Habilita para clase el intervalo {@code [inicio, fin)} de un día.
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
	}

	/**
	 * Indica si en la sede cabe una sesión que empieza a {@code inicio} y dura
	 * {@code duracion} horas sin tocar horas deshabilitadas (como el almuerzo).
	 *
	 * @param dia      día de la semana.
	 * @param inicio   hora de inicio.
	 * @param duracion duración en horas.
	 * @return {@code true} si todas las horas están habilitadas.
	 */
	public boolean permite(DayOfWeek dia, int inicio, int duracion) {
		int d = dia.getValue() - 1;
		if (inicio < 0 || inicio + duracion > HORAS) {
			return false;
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
