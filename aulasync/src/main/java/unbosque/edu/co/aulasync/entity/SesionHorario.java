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
 * Sesión del horario guardado: una fila por cada sesión semanal de cada grupo
 * abierto.
 *
 * <p>
 * Si está ubicada tiene día, hora de inicio y aula (la sede sale del aula). Si
 * no, tiene {@code causa} y {@code detalleCausa} para que el comité sepa por
 * qué y la ubique a mano. Si se borra el grupo, sus sesiones se borran en
 * cascada; si se borra el aula, la sesión queda sin aula.
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "sesion_horario")
public class SesionHorario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "grupo_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Grupo grupo;

	@Column(name = "numero_sesion", nullable = false)
	private int numeroSesion;

	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private DayOfWeek dia;

	@Column(name = "hora_inicio")
	private Integer horaInicio;

	@ManyToOne
	@JoinColumn(name = "aula_id")
	@OnDelete(action = OnDeleteAction.SET_NULL)
	private Aula aula;

	@Column(length = 40)
	private String causa;

	@Column(name = "detalle_causa", length = 500)
	private String detalleCausa;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public SesionHorario() {
		// Requerido por JPA.
	}

	/**
	 * Crea una sesión con todos sus datos.
	 *
	 * @param grupo        grupo al que pertenece.
	 * @param numeroSesion número de la sesión en la semana.
	 * @param dia          día asignado, o {@code null} si no está ubicada.
	 * @param horaInicio   hora de inicio, o {@code null} si no está ubicada.
	 * @param aula         aula asignada, o {@code null} si no está ubicada.
	 * @param causa        causa por la que no está ubicada, o {@code null}.
	 * @param detalleCausa desglose de causas en texto, o {@code null}.
	 */
	public SesionHorario(Grupo grupo, int numeroSesion, DayOfWeek dia, Integer horaInicio, Aula aula, String causa,
			String detalleCausa) {
		this.grupo = grupo;
		this.numeroSesion = numeroSesion;
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.aula = aula;
		this.causa = causa;
		this.detalleCausa = detalleCausa;
	}

	/**
	 * @return identificador de la sesión.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador de la sesión.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return grupo al que pertenece.
	 */
	public Grupo getGrupo() {
		return grupo;
	}

	/**
	 * @param grupo grupo al que pertenece.
	 */
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	/**
	 * @return número de la sesión en la semana.
	 */
	public int getNumeroSesion() {
		return numeroSesion;
	}

	/**
	 * @param numeroSesion número de la sesión en la semana.
	 */
	public void setNumeroSesion(int numeroSesion) {
		this.numeroSesion = numeroSesion;
	}

	/**
	 * @return día asignado, o {@code null}.
	 */
	public DayOfWeek getDia() {
		return dia;
	}

	/**
	 * @param dia día asignado.
	 */
	public void setDia(DayOfWeek dia) {
		this.dia = dia;
	}

	/**
	 * @return hora de inicio, o {@code null}.
	 */
	public Integer getHoraInicio() {
		return horaInicio;
	}

	/**
	 * @param horaInicio hora de inicio.
	 */
	public void setHoraInicio(Integer horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * @return aula asignada, o {@code null}.
	 */
	public Aula getAula() {
		return aula;
	}

	/**
	 * @param aula aula asignada.
	 */
	public void setAula(Aula aula) {
		this.aula = aula;
	}

	/**
	 * @return causa por la que no está ubicada, o {@code null}.
	 */
	public String getCausa() {
		return causa;
	}

	/**
	 * @param causa causa por la que no está ubicada.
	 */
	public void setCausa(String causa) {
		this.causa = causa;
	}

	/**
	 * @return desglose de causas en texto, o {@code null}.
	 */
	public String getDetalleCausa() {
		return detalleCausa;
	}

	/**
	 * @param detalleCausa desglose de causas en texto.
	 */
	public void setDetalleCausa(String detalleCausa) {
		this.detalleCausa = detalleCausa;
	}
}
