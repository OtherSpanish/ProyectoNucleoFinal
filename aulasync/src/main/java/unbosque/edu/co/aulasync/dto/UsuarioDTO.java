package unbosque.edu.co.aulasync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import unbosque.edu.co.aulasync.model.Rol;

/**
 * DTO de entrada y salida de {@code Usuario}.
 *
 * <p>
 * Tiene los mismos atributos que la entidad. La contraseña es de solo
 * escritura: se recibe en el login o al crear un usuario, pero nunca se
 * incluye en las respuestas JSON.
 * </p>
 *
 * @version 1.0
 */
public class UsuarioDTO {

	private Long id;
	private String nombre;
	private String correo;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	private Rol rol;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public UsuarioDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id       identificador del usuario.
	 * @param nombre   nombre completo.
	 * @param correo   correo institucional.
	 * @param password contraseña en texto plano (solo en peticiones).
	 * @param rol      rol en el sistema.
	 */
	public UsuarioDTO(Long id, String nombre, String correo, String password, Rol rol) {
		this.id = id;
		this.nombre = nombre;
		this.correo = correo;
		this.password = password;
		this.rol = rol;
	}

	/**
	 * @return identificador del usuario.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del usuario.
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return correo institucional.
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * @param correo correo institucional.
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	/**
	 * @return contraseña en texto plano (solo en peticiones).
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password contraseña en texto plano.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return rol en el sistema.
	 */
	public Rol getRol() {
		return rol;
	}

	/**
	 * @param rol rol en el sistema.
	 */
	public void setRol(Rol rol) {
		this.rol = rol;
	}
}
