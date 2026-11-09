package unbosque.edu.co.aulasync.dto;

import java.util.ArrayList;
import java.util.List;

import unbosque.edu.co.aulasync.model.Dedicacion;

/**
 * DTO de entrada y salida de {@code Docente}, con sus mismos atributos.
 *
 * <p>
 * En las peticiones basta con enviar los ids de {@code usuario} y de cada
 * especialidad. En las respuestas ambos llegan completos (el usuario sin
 * contraseña).
 * </p>
 *
 * @version 1.0
 */
public class DocenteDTO {

	private Long id;
	private UsuarioDTO usuario;
	private Dedicacion dedicacion;
	private List<EspecialidadDTO> especialidades = new ArrayList<>();

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public DocenteDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id             identificador del docente.
	 * @param usuario        usuario vinculado.
	 * @param dedicacion     tipo de vinculación.
	 * @param especialidades especialidades del docente.
	 */
	public DocenteDTO(Long id, UsuarioDTO usuario, Dedicacion dedicacion, List<EspecialidadDTO> especialidades) {
		this.id = id;
		this.usuario = usuario;
		this.dedicacion = dedicacion;
		this.especialidades = especialidades;
	}

	/**
	 * @return identificador del docente.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del docente.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return usuario vinculado.
	 */
	public UsuarioDTO getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario usuario vinculado.
	 */
	public void setUsuario(UsuarioDTO usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return tipo de vinculación.
	 */
	public Dedicacion getDedicacion() {
		return dedicacion;
	}

	/**
	 * @param dedicacion tipo de vinculación.
	 */
	public void setDedicacion(Dedicacion dedicacion) {
		this.dedicacion = dedicacion;
	}

	/**
	 * @return especialidades del docente.
	 */
	public List<EspecialidadDTO> getEspecialidades() {
		return especialidades;
	}

	/**
	 * @param especialidades especialidades del docente.
	 */
	public void setEspecialidades(List<EspecialidadDTO> especialidades) {
		this.especialidades = especialidades;
	}
}
