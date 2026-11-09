package unbosque.edu.co.aulasync.dto;

/**
 * DTO de entrada y salida de {@code Especialidad}, con sus mismos atributos.
 *
 * @version 1.0
 */
public class EspecialidadDTO {

	private Long id;
	private String nombre;
	private String descripcion;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public EspecialidadDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id          identificador de la especialidad.
	 * @param nombre      nombre de la especialidad.
	 * @param descripcion descripción opcional.
	 */
	public EspecialidadDTO(Long id, String nombre, String descripcion) {
		this.id = id;
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
