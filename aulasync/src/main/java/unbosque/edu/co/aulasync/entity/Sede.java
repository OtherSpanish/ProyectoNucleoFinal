package unbosque.edu.co.aulasync.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Sede física de la universidad (por ejemplo Usaquén o Chía).
 *
 * <p>
 * Sus {@link FranjaSede franjas} indican en qué bloques de cada día hay clase;
 * los huecos entre franjas (como el almuerzo) no se usan. Las franjas se
 * guardan y se borran junto con la sede.
 * </p>
 *
 * <p>
 * Regla de primer semestre:
 * </p>
 * <ul>
 * <li>{@code exclusivaPrimerSemestre}: si es {@code true}, solo los grupos de
 * primer semestre pueden tener clase aquí.</li>
 * <li>{@code diasPrimerSemestre}: número exacto de días a la semana en que los
 * grupos de primer semestre deben tener clase en esta sede (Chía = 2,
 * Usaquén = 0).</li>
 * </ul>
 *
 * @version 1.0
 */
@Entity
@Table(name = "sede")
public class Sede {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 60)
	private String nombre;

	@Column(name = "exclusiva_primer_semestre", nullable = false)
	private boolean exclusivaPrimerSemestre;

	@Column(name = "dias_primer_semestre", nullable = false)
	private int diasPrimerSemestre;

	@OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FranjaSede> franjas = new ArrayList<>();

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Sede() {
		// Requerido por JPA.
	}

	/**
	 * Crea una sede sin franjas.
	 *
	 * @param nombre                  nombre único de la sede.
	 * @param exclusivaPrimerSemestre si solo admite grupos de primer semestre.
	 * @param diasPrimerSemestre      días exactos por semana que primer semestre
	 *                                debe tener clase aquí.
	 */
	public Sede(String nombre, boolean exclusivaPrimerSemestre, int diasPrimerSemestre) {
		this.nombre = nombre;
		this.exclusivaPrimerSemestre = exclusivaPrimerSemestre;
		this.diasPrimerSemestre = diasPrimerSemestre;
	}

	/**
	 * @return identificador de la sede.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador de la sede.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return nombre de la sede.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre de la sede.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return {@code true} si solo admite grupos de primer semestre.
	 */
	public boolean isExclusivaPrimerSemestre() {
		return exclusivaPrimerSemestre;
	}

	/**
	 * @param exclusivaPrimerSemestre si solo admite grupos de primer semestre.
	 */
	public void setExclusivaPrimerSemestre(boolean exclusivaPrimerSemestre) {
		this.exclusivaPrimerSemestre = exclusivaPrimerSemestre;
	}

	/**
	 * @return días exactos por semana que primer semestre debe tener clase aquí.
	 */
	public int getDiasPrimerSemestre() {
		return diasPrimerSemestre;
	}

	/**
	 * @param diasPrimerSemestre días exactos por semana que primer semestre debe
	 *                           tener clase aquí.
	 */
	public void setDiasPrimerSemestre(int diasPrimerSemestre) {
		this.diasPrimerSemestre = diasPrimerSemestre;
	}

	/**
	 * @return franjas de clase de la sede.
	 */
	public List<FranjaSede> getFranjas() {
		return franjas;
	}

	/**
	 * @param franjas franjas de clase de la sede.
	 */
	public void setFranjas(List<FranjaSede> franjas) {
		this.franjas = franjas;
	}
}
