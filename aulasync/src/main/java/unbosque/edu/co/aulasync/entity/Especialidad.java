package unbosque.edu.co.aulasync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Área de conocimiento (por ejemplo "Programación" o "Bases de datos").
 *
 * <p>
 * Se asigna a los docentes, para indicar qué pueden dictar, y a los cursos,
 * para indicar qué área exigen. Una sección solo se abre si el docente tiene
 * la especialidad del curso. El nombre es único.
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "especialidad")
public class Especialidad {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String nombre;

	@Column(length = 255)
	private String descripcion;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Especialidad() {
		// Requerido por JPA.
	}

	/**
	 * Crea una especialidad con todos sus datos.
	 *
	 * @param nombre      nombre único de la especialidad.
	 * @param descripcion descripción opcional.
	 */
	public Especialidad(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	/**
	 * @return identificador de la especialidad.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador de la especialidad.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return nombre de la especialidad.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre de la especialidad.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return descripción de la especialidad.
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion descripción de la especialidad.
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
