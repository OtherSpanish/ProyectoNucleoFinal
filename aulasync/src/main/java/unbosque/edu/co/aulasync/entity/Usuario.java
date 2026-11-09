package unbosque.edu.co.aulasync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import unbosque.edu.co.aulasync.model.Rol;

/**
 * Usuario con acceso al sistema.
 *
 * <p>
 * El correo es único y sirve como nombre de usuario para iniciar sesión. La
 * contraseña se guarda siempre cifrada con BCrypt, nunca en texto plano.
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(nullable = false, unique = true, length = 150)
	private String correo;

	@Column(nullable = false, length = 100)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Rol rol;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Usuario() {
		// Requerido por JPA.
	}

	/**
	 * Crea un usuario con todos sus datos.
	 *
	 * @param nombre   nombre completo.
	 * @param correo   correo institucional, único.
	 * @param password contraseña ya cifrada.
	 * @param rol      rol en el sistema.
	 */
	public Usuario(String nombre, String correo, String password, Rol rol) {
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
	 * @return contraseña cifrada.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password contraseña cifrada.
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
