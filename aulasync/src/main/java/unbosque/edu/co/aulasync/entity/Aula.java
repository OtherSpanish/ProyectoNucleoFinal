package unbosque.edu.co.aulasync.entity;

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
import jakarta.persistence.UniqueConstraint;
import unbosque.edu.co.aulasync.model.TipoAula;

/**
 * Espacio físico donde se dicta clase.
 *
 * <p>
 * Pertenece a una {@link Sede}; su nombre es único dentro de esa sede. Si
 * {@code disponible} es {@code false}, el aula se conserva pero el motor no la
 * usa (por ejemplo, durante una remodelación).
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "aula", uniqueConstraints = @UniqueConstraint(name = "uk_aula_sede_nombre",
		columnNames = { "sede_id", "nombre" }))
public class Aula {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 40)
	private String nombre;

	@Column(nullable = false)
	private int capacidad;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoAula tipo;

	@Column(nullable = false)
	private boolean disponible;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sede_id", nullable = false)
	private Sede sede;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Aula() {
		// Requerido por JPA.
	}

	/**
	 * Crea un aula con todos sus datos.
	 *
	 * @param nombre     nombre del aula, único dentro de la sede.
	 * @param capacidad  número máximo de estudiantes.
	 * @param tipo       tipo de espacio.
	 * @param disponible si el motor puede usarla.
	 * @param sede       sede a la que pertenece.
	 */
	public Aula(String nombre, int capacidad, TipoAula tipo, boolean disponible, Sede sede) {
		this.nombre = nombre;
		this.capacidad = capacidad;
		this.tipo = tipo;
		this.disponible = disponible;
		this.sede = sede;
	}

	/**
	 * @return identificador del aula.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del aula.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return nombre del aula.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre del aula.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return número máximo de estudiantes.
	 */
	public int getCapacidad() {
		return capacidad;
	}

	/**
	 * @param capacidad número máximo de estudiantes.
	 */
	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	/**
	 * @return tipo de espacio.
	 */
	public TipoAula getTipo() {
		return tipo;
	}

	/**
	 * @param tipo tipo de espacio.
	 */
	public void setTipo(TipoAula tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return {@code true} si el motor puede usarla.
	 */
	public boolean isDisponible() {
		return disponible;
	}

	/**
	 * @param disponible si el motor puede usarla.
	 */
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	/**
	 * @return sede a la que pertenece.
	 */
	public Sede getSede() {
		return sede;
	}

	/**
	 * @param sede sede a la que pertenece.
	 */
	public void setSede(Sede sede) {
		this.sede = sede;
	}
}
