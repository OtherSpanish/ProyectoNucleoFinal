package unbosque.edu.co.aulasync.model;

import java.util.List;

import unbosque.edu.co.aulasync.dto.SesionHorarioDTO;

/**
 * Objeto de valor con un horario semanal listo para dibujarse como grilla.
 *
 * <p>
 * Trae una columna por día (lunes a sábado), el rango de horas de la jornada,
 * las sesiones sin ubicar y los totales, para que el front solo tenga que
 * pintar.
 * </p>
 *
 * @version 1.0
 */
public class CalendarioSemanal {

	private final String descripcion;
	private final String prefijo;
	private final int horaMinima;
	private final int horaMaxima;
	private final List<DiaCalendario> dias;
	private final List<SesionHorarioDTO> sinUbicar;
	private final int totalUbicadas;
	private final int totalSinUbicar;

	/**
	 * Crea el calendario.
	 *
	 * @param descripcion   qué muestra (por ejemplo "Semestre 1").
	 * @param prefijo       prefijo del índice usado, o {@code null} si es el
	 *                      horario completo.
	 * @param horaMinima    primera hora de la grilla.
	 * @param horaMaxima    última hora de la grilla.
	 * @param dias          columnas de lunes a sábado.
	 * @param sinUbicar     sesiones sin ubicar del mismo filtro.
	 * @param totalUbicadas cantidad de sesiones ubicadas.
	 */
	public CalendarioSemanal(String descripcion, String prefijo, int horaMinima, int horaMaxima,
			List<DiaCalendario> dias, List<SesionHorarioDTO> sinUbicar, int totalUbicadas) {
		this.descripcion = descripcion;
		this.prefijo = prefijo;
		this.horaMinima = horaMinima;
		this.horaMaxima = horaMaxima;
		this.dias = dias;
		this.sinUbicar = sinUbicar;
		this.totalUbicadas = totalUbicadas;
		this.totalSinUbicar = sinUbicar.size();
	}

	/**
	 * @return qué muestra el calendario.
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @return prefijo del índice usado, o {@code null}.
	 */
	public String getPrefijo() {
		return prefijo;
	}

	/**
	 * @return primera hora de la grilla.
	 */
	public int getHoraMinima() {
		return horaMinima;
	}

	/**
	 * @return última hora de la grilla.
	 */
	public int getHoraMaxima() {
		return horaMaxima;
	}

	/**
	 * @return columnas de lunes a sábado.
	 */
	public List<DiaCalendario> getDias() {
		return dias;
	}

	/**
	 * @return sesiones sin ubicar.
	 */
	public List<SesionHorarioDTO> getSinUbicar() {
		return sinUbicar;
	}

	/**
	 * @return cantidad de sesiones ubicadas.
	 */
	public int getTotalUbicadas() {
		return totalUbicadas;
	}

	/**
	 * @return cantidad de sesiones sin ubicar.
	 */
	public int getTotalSinUbicar() {
		return totalSinUbicar;
	}
}
