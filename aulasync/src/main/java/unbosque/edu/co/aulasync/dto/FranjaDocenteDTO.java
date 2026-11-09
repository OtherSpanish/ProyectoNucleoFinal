package unbosque.edu.co.aulasync.dto;

import java.time.DayOfWeek;

/**
 * DTO de entrada y salida de {@code FranjaDocente}.
 *
 * <p>
 * Tiene los mismos atributos que la entidad, salvo que el docente se
 * representa solo con su id. El día se envía en inglés como lo define
 * {@link DayOfWeek} ({@code MONDAY} ... {@code SATURDAY}).
 * </p>
 *
 * @version 1.0
 */
public class FranjaDocenteDTO {

	private Long id;
	private Long docenteId;
	private DayOfWeek dia;
	private Integer horaInicio;
	private Integer horaFin;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public FranjaDocenteDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id         identificador de la franja.
	 * @param docenteId  id del docente.
	 * @param dia        día de la semana.
	 * @param horaInicio hora de inicio (incluida).
	 * @param horaFin    hora de fin (excluida).
	 */
	public FranjaDocenteDTO(Long id, Long docenteId, DayOfWeek dia, Integer horaInicio, Integer horaFin) {
		this.id = id;
		this.docenteId = docenteId;
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	/**
	 * @return identificador de la franja.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador de la franja.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return id del docente.
	 */
	public Long getDocenteId() {
		return docenteId;
	}

	/**
	 * @param docenteId id del docente.
	 */
	public void setDocenteId(Long docenteId) {
		this.docenteId = docenteId;
	}

	/**
	 * @return día de la semana.
	 */
	public DayOfWeek getDia() {
		return dia;
	}

	/**
	 * @param dia día de la semana.
	 */
	public void setDia(DayOfWeek dia) {
		this.dia = dia;
	}

	/**
	 * @return hora de inicio (incluida).
	 */
	public Integer getHoraInicio() {
		return horaInicio;
	}

	/**
	 * @param horaInicio hora de inicio (incluida).
	 */
	public void setHoraInicio(Integer horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * @return hora de fin (excluida).
	 */
	public Integer getHoraFin() {
		return horaFin;
	}

	/**
	 * @param horaFin hora de fin (excluida).
	 */
	public void setHoraFin(Integer horaFin) {
		this.horaFin = horaFin;
	}
}
