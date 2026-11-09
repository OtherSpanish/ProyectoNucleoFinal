package unbosque.edu.co.aulasync.dto;

import java.time.DayOfWeek;

/**
 * DTO de entrada y salida de {@code FranjaSede}.
 *
 * <p>
 * Tiene los mismos atributos que la entidad, salvo que la sede se representa
 * solo con su id. El día se envía como {@code MONDAY} ... {@code SATURDAY}.
 * </p>
 *
 * @version 1.0
 */
public class FranjaSedeDTO {

	private Long id;
	private Long sedeId;
	private DayOfWeek dia;
	private Integer horaInicio;
	private Integer horaFin;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public FranjaSedeDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id         identificador de la franja.
	 * @param sedeId     id de la sede.
	 * @param dia        día de la semana.
	 * @param horaInicio hora de inicio (incluida).
	 * @param horaFin    hora de fin (excluida).
	 */
	public FranjaSedeDTO(Long id, Long sedeId, DayOfWeek dia, Integer horaInicio, Integer horaFin) {
		this.id = id;
		this.sedeId = sedeId;
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
	 * @return id de la sede.
	 */
	public Long getSedeId() {
		return sedeId;
	}

	/**
	 * @param sedeId id de la sede.
	 */
	public void setSedeId(Long sedeId) {
		this.sedeId = sedeId;
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
