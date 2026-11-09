package unbosque.edu.co.aulasync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Estudiante del programa.
 *
 * <p>
 * No tiene semestre propio: puede estar inscrito en grupos de materias de
 * semestres distintos. Sus inscripciones sirven para la regla de mínimo de
 * inscritos por grupo y para que el docente consulte sus listas. El código es
 * único.
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "estudiante")
public class Estudiante {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String codigo;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(length = 150)
	private String correo;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Estudiante() {
		// Requerido por JPA.
	}

	/**
	 * Crea un estudiante con todos sus datos.
	 *
	 * @param codigo código institucional, único.
	 * @param nombre nombre completo.
	 * @param correo correo opcional.
	 */
	public Estudiante(String codigo, String nombre, String correo) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.correo = correo;
	}

	/**
	 * @return identificador del estudiante.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del estudiante.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return código institucional.
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo código institucional.
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return nombre completo.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre completo.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return correo, o {@code null} si no tiene.
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * @param correo correo opcional.
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}
}
