package unbosque.edu.co.aulasync.model;

import jakarta.persistence.*;

@Entity
public class FranjaDocente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(optional = false)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Docente docente;
	private java.time.DayOfWeek dia;
	private int horaInicio;
	private int horaFin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Docente getDocente() {
		return docente;
	}

	public void setDocente(Docente docente) {
		this.docente = docente;
	}

	public java.time.DayOfWeek getDia() {
		return dia;
	}

	public void setDia(java.time.DayOfWeek dia) {
		this.dia = dia;
	}

	public int getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(int horaInicio) {
		this.horaInicio = horaInicio;
	}

	public int getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(int horaFin) {
		this.horaFin = horaFin;
	}

}