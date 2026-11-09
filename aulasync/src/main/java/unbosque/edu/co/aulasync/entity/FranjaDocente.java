package unbosque.edu.co.aulasync.entity;

import java.time.DayOfWeek;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
 * Bloque de tiempo en que un docente puede dictar clase, por ejemplo lunes de
 * 7 a 13 (la hora de fin no se incluye).
 *
 * <p>
 * Si se elimina el docente, la base de datos borra sus franjas en cascada.
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "franja_docente")
public class FranjaDocente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "docente_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Docente docente;

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
	public FranjaDocente() {
		// Requerido por JPA.
	}

	/**
	 * Crea una franja con todos sus datos.
	 *
	 * @param docente    docente dueño de la franja.
	 * @param dia        día de la semana.
	 * @param horaInicio hora de inicio (incluida).
	 * @param horaFin    hora de fin (excluida).
	 */
	public FranjaDocente(Docente docente, DayOfWeek dia, int horaInicio, int horaFin) {
		this.docente = docente;
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
	 * @return docente dueño de la franja.
	 */
	public Docente getDocente() {
		return docente;
	}

	/**
	 * @param docente docente dueño de la franja.
	 */
	public void setDocente(Docente docente) {
		this.docente = docente;
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
