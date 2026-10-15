package unbosque.edu.co.aulasync.model;

import jakarta.persistence.*;

@Entity
public class Grupo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private int semestre;
	private int estudiantes;

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

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public int getEstudiantes() {
		return estudiantes;
	}

	public void setEstudiantes(int estudiantes) {
		this.estudiantes = estudiantes;
	}

}