package unbosque.edu.co.aulasync.model;

import java.time.DayOfWeek;

import jakarta.persistence.*;

/**
 * Una sesión semanal de una sección dentro del horario guardado. Si {@code dia}
 * es {@code null}, la sesión está sin ubicar y {@code causa} explica por qué
 * (causa del motor o de una acción manual del Comité). La duración no se
 * guarda: sale del curso de la sección.
 */
@Entity
public class SesionHorario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private Seccion seccion;

	@Enumerated(EnumType.STRING)
	private DayOfWeek dia;

	private Integer horaInicio;

	@ManyToOne
	private Aula aula;

	private String causa;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Seccion getSeccion() {
		return seccion;
	}

	public void setSeccion(Seccion seccion) {
		this.seccion = seccion;
	}

	public DayOfWeek getDia() {
		return dia;
	}

	public void setDia(DayOfWeek dia) {
		this.dia = dia;
	}

	public Integer getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Integer horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Aula getAula() {
		return aula;
	}

	public void setAula(Aula aula) {
		this.aula = aula;
	}

	public String getCausa() {
		return causa;
	}

	public void setCausa(String causa) {
		this.causa = causa;
	}
}
