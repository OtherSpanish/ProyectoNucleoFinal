package unbosque.edu.co.aulasync.model;

/**
 * Tipo de vinculación del docente con la universidad.
 *
 * <p>
 * Cada dedicación fija el máximo de horas de clase semanales que el motor de
 * horarios le puede asignar al docente.
 * </p>
 *
 * @version 1.0
 */
public enum Dedicacion {

	/** Tiempo completo: hasta 40 horas semanales. */
	TIEMPO_COMPLETO(40),

	/** Tres cuartos de tiempo: hasta 30 horas semanales. */
	TRES_CUARTOS(30),

	/** Medio tiempo: hasta 20 horas semanales. */
	MEDIO_TIEMPO(20),

	/** Un cuarto de tiempo: hasta 10 horas semanales. */
	UN_CUARTO(10);

	private final int horasMaximas;

	Dedicacion(int horasMaximas) {
		this.horasMaximas = horasMaximas;
	}

	/**
	 * @return máximo de horas de clase semanales para esta dedicación.
	 */
	public int getHorasMaximas() {
		return horasMaximas;
	}
}
