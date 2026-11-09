package unbosque.edu.co.aulasync.entity;

import java.time.DayOfWeek;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Bloque de un día en que hay clases en una sede, por ejemplo lunes de 7 a 13
 * en Usaquén (la hora de fin no se incluye).
 *
 * @version 1.0
 */
@Entity
@Table(name = "franja_sede")
public class FranjaSede {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sede_id", nullable = false)
	private Sede sede;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private DayOfWeek dia;

	@Column(name = "hora_inicio", nullable = false)
	private int horaInicio;

	@Column(name = "hora_fin", nullable = false)
	private int horaFin;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public FranjaSede() {
		// Requerido por JPA.
	}

	/**
	 * Crea una franja con todos sus datos.
	 *
	 * @param sede       sede dueña de la franja.
	 * @param dia        día de la semana.
	 * @param horaInicio hora de inicio (incluida).
	 * @param horaFin    hora de fin (excluida).
	 */
	public FranjaSede(Sede sede, DayOfWeek dia, int horaInicio, int horaFin) {
		this.sede = sede;
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
	 * @return sede dueña de la franja.
	 */
	public Sede getSede() {
		return sede;
	}

	/**
	 * @param sede sede dueña de la franja.
	 */
	public void setSede(Sede sede) {
		this.sede = sede;
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
	public int getHoraInicio() {
		return horaInicio;
	}

	/**
	 * @param horaInicio hora de inicio (incluida).
	 */
	public void setHoraInicio(int horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * @return hora de fin (excluida).
	 */
	public int getHoraFin() {
		return horaFin;
	}

	/**
	 * @param horaFin hora de fin (excluida).
	 */
	public void setHoraFin(int horaFin) {
		this.horaFin = horaFin;
	}
}
