package unbosque.edu.co.aulasync.model;

/**
 * Patrones de intensidad semanal permitidos para un curso.
 *
 * <p>
 * Cada valor fija cuántas sesiones tiene el curso por semana y cuántas horas
 * dura cada una. El motor crea una sesión por cada una y las reparte en días
 * distintos.
 * </p>
 *
 * @version 1.0
 */
public enum IntensidadHoraria {

	/** Una sesión semanal de 2 horas. */
	UNA_SESION_DOS_HORAS(1, 2),

	/** Dos sesiones semanales de 2 horas. */
	DOS_SESIONES_DOS_HORAS(2, 2),

	/** Tres sesiones semanales de 2 horas. */
	TRES_SESIONES_DOS_HORAS(3, 2),

	/** Una sesión semanal de 4 horas. */
	UNA_SESION_CUATRO_HORAS(1, 4);

	private final int sesionesSemana;
	private final int duracionHoras;

	IntensidadHoraria(int sesionesSemana, int duracionHoras) {
		this.sesionesSemana = sesionesSemana;
		this.duracionHoras = duracionHoras;
	}

	/**
	 * @return número de sesiones por semana.
	 */
	public int getSesionesSemana() {
		return sesionesSemana;
	}

	/**
	 * @return duración de cada sesión, en horas.
	 */
	public int getDuracionHoras() {
		return duracionHoras;
	}

	/**
	 * @return total de horas de clase por semana.
	 */
	public int getHorasSemanales() {
		return sesionesSemana * duracionHoras;
	}
}
