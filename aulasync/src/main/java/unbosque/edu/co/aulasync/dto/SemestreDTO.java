package unbosque.edu.co.aulasync.dto;

/**
 * DTO de entrada y salida de {@code Semestre}, con sus mismos atributos.
 *
 * @version 1.0
 */
public class SemestreDTO {

	private Long id;
	private Integer numero;
	private String nombre;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public SemestreDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id     identificador del semestre.
	 * @param numero número del semestre.
	 * @param nombre nombre para mostrar.
	 */
	public SemestreDTO(Long id, Integer numero, String nombre) {
		this.id = id;
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
	public Integer getNumero() {
		return numero;
	}

	/**
	 * @param numero número del semestre.
	 */
	public void setNumero(Integer numero) {
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
