package unbosque.edu.co.aulasync.model;

import jakarta.persistence.*;

@Entity
public class Docente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private String dedicacion; // TIEMPO_COMPLETO, TRES_CUARTOS, MEDIO_TIEMPO, UN_CUARTO

	@OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, orphanRemoval = true)
	private java.util.List<FranjaDocente> franjas = new java.util.ArrayList<>();

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

	public String getDedicacion() {
		return dedicacion;
	}

	public void setDedicacion(String dedicacion) {
		this.dedicacion = dedicacion;
	}

	public java.util.List<FranjaDocente> getFranjas() {
		return franjas;
	}

	public void setFranjas(java.util.List<FranjaDocente> franjas) {
		this.franjas = franjas;
	}

}