package unbosque.edu.co.aulasync.model;

import jakarta.persistence.*;

@Entity
public class Aula {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private int capacidad;
	@Enumerated(EnumType.STRING)
	private unbosque.edu.co.aulasync.motor.TipoAula tipo;

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

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public unbosque.edu.co.aulasync.motor.TipoAula getTipo() {
		return tipo;
	}

	public void setTipo(unbosque.edu.co.aulasync.motor.TipoAula tipo) {
		this.tipo = tipo;
	}

}