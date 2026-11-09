package unbosque.edu.co.aulasync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Semestre del plan de estudios (1 a 12).
 *
 * <p>
 * Agrupa los grupos de estudiantes y los cursos. El horario se consulta por
 * semestre, y el primer semestre tiene la regla especial de la sede de Chía.
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "semestre")
public class Semestre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private int numero;

	@Column(nullable = false, length = 60)
	private String nombre;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Semestre() {
		// Requerido por JPA.
	}

	/**
	 * Crea un semestre con todos sus datos.
	 *
	 * @param numero número del semestre, único.
	 * @param nombre nombre para mostrar (por ejemplo "Primer semestre").
	 */
	public Semestre(int numero, String nombre) {
		this.numero = numero;
		this.nombre = nombre;
	}

	/**
	 * @return identificador del semestre.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del semestre.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return número del semestre.
	 */
	public int getNumero() {
		return numero;
	}

	/**
	 * @param numero número del semestre.
	 */
	public void setNumero(int numero) {
		this.numero = numero;
	}

	/**
	 * @return nombre para mostrar.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre para mostrar.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
