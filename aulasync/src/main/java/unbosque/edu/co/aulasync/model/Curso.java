package unbosque.edu.co.aulasync.model;

import jakarta.persistence.*;

@Entity
public class Curso {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private int sesionesSemana;
	private int duracionHoras;
	@Enumerated(EnumType.STRING)
	private unbosque.edu.co.aulasync.motor.TipoAula tipoAula;

	public Curso() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getSesionesSemana() {
		return sesionesSemana;
	}

	public void setSesionesSemana(int sesionesSemana) {
		this.sesionesSemana = sesionesSemana;
	}

	public int getDuracionHoras() {
		return duracionHoras;
	}

	public void setDuracionHoras(int duracionHoras) {
		this.duracionHoras = duracionHoras;
	}

	public unbosque.edu.co.aulasync.motor.TipoAula getTipoAula() {
		return tipoAula;
	}

	public void setTipoAula(unbosque.edu.co.aulasync.motor.TipoAula tipoAula) {
		this.tipoAula = tipoAula;
	}



	
}