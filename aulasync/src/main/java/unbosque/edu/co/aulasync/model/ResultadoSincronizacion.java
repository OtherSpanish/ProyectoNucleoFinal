package unbosque.edu.co.aulasync.model;

import java.util.List;

/**
 * Objeto de valor con el resultado de sincronizar el horario guardado con los
 * datos maestros actuales.
 *
 * @version 1.0
 */
public class ResultadoSincronizacion {

	private final boolean hayHorario;
	private final int sesionesCreadas;
	private final int sesionesEliminadas;
	private final int sesionesDesubicadas;
	private final List<String> cambios;

	/**
	 * Crea el resultado.
	 *
	 * @param hayHorario          {@code false} si no había horario guardado (no
	 *                            se hizo nada).
	 * @param sesionesCreadas     sesiones nuevas pendientes de ubicar.
	 * @param sesionesEliminadas  sesiones quitadas.
	 * @param sesionesDesubicadas sesiones que dejaron de ser válidas.
	 * @param cambios             descripción de cada cambio hecho.
	 */
	public ResultadoSincronizacion(boolean hayHorario, int sesionesCreadas, int sesionesEliminadas,
			int sesionesDesubicadas, List<String> cambios) {
		this.hayHorario = hayHorario;
		this.sesionesCreadas = sesionesCreadas;
		this.sesionesEliminadas = sesionesEliminadas;
		this.sesionesDesubicadas = sesionesDesubicadas;
		this.cambios = cambios;
	}

	/**
	 * @return si había horario guardado.
	 */
	public boolean isHayHorario() {
		return hayHorario;
	}

	/**
	 * @return sesiones nuevas pendientes de ubicar.
	 */
	public int getSesionesCreadas() {
		return sesionesCreadas;
	}

	/**
	 * @return sesiones quitadas.
	 */
	public int getSesionesEliminadas() {
		return sesionesEliminadas;
	}

	/**
	 * @return sesiones que dejaron de ser válidas.
	 */
	public int getSesionesDesubicadas() {
		return sesionesDesubicadas;
	}

	/**
	 * @return descripción de cada cambio hecho.
	 */
	public List<String> getCambios() {
		return cambios;
	}
}
