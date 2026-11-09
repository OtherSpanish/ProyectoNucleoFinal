package unbosque.edu.co.aulasync.dto;

/**
 * DTO de entrada y salida de {@code Estudiante}, con sus mismos atributos.
 *
 * @version 1.0
 */
public class EstudianteDTO {

	private Long id;
	private String codigo;
	private String nombre;
	private String correo;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public EstudianteDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id     identificador del estudiante.
	 * @param codigo código institucional.
	 * @param nombre nombre completo.
	 * @param correo correo opcional.
	 */
	public EstudianteDTO(Long id, String codigo, String nombre, String correo) {
		this.id = id;
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
