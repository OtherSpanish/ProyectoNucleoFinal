package unbosque.edu.co.aulasync.model;

import java.time.DayOfWeek;
import java.util.List;

import unbosque.edu.co.aulasync.dto.SesionHorarioDTO;

/**
 * Objeto de valor con una columna del calendario semanal: un día y sus
 * sesiones ubicadas, ordenadas por hora de inicio.
 *
 * @version 1.0
 */
public class DiaCalendario {

	private final DayOfWeek dia;
	private final String diaNombre;
	private final List<SesionHorarioDTO> sesiones;

	/**
	 * Crea la columna del día.
	 *
	 * @param dia       día de la semana.
	 * @param diaNombre nombre del día en español.
	 * @param sesiones  sesiones ubicadas ese día, ordenadas por hora.
	 */
	public DiaCalendario(DayOfWeek dia, String diaNombre, List<SesionHorarioDTO> sesiones) {
		this.dia = dia;
		this.diaNombre = diaNombre;
		this.sesiones = sesiones;
	}

	/**
	 * @return día de la semana.
	 */
	public DayOfWeek getDia() {
		return dia;
	}

	/**
	 * @return nombre del día en español.
	 */
	public String getDiaNombre() {
		return diaNombre;
	}

	/**
	 * @return sesiones ubicadas ese día, ordenadas por hora.
	 */
	public List<SesionHorarioDTO> getSesiones() {
		return sesiones;
	}
}
