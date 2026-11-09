package unbosque.edu.co.aulasync.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import unbosque.edu.co.aulasync.model.Dedicacion;

/**
 * Docente del programa.
 *
 * <p>
 * Está vinculado a exactamente un {@link Usuario} con rol {@code DOCENTE}, de
 * donde salen su nombre y su correo, y con el que inicia sesión para consultar
 * sus clases. Un usuario solo puede estar vinculado a un docente.
 * </p>
 *
 * <p>
 * Sus {@link Especialidad especialidades} determinan qué cursos puede dictar.
 * Varios docentes pueden compartir una especialidad, pero un mismo docente no
 * puede tenerla dos veces (restricción única en {@code docente_especialidad}).
 * La disponibilidad horaria se registra aparte.
 * </p>
 *
 * @version 1.1
 */
@Entity
@Table(name = "docente")
public class Docente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(optional = false)
	@JoinColumn(name = "usuario_id", nullable = false, unique = true)
	private Usuario usuario;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Dedicacion dedicacion;

	@ManyToMany
	@JoinTable(name = "docente_especialidad",
			joinColumns = @JoinColumn(name = "docente_id"),
			inverseJoinColumns = @JoinColumn(name = "especialidad_id"),
			uniqueConstraints = @UniqueConstraint(name = "uk_docente_especialidad",
					columnNames = { "docente_id", "especialidad_id" }))
	private List<Especialidad> especialidades = new ArrayList<>();

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Docente() {
		// Requerido por JPA.
	}

	/**
	 * Crea un docente con todos sus datos.
	 *
	 * @param usuario        usuario con rol DOCENTE.
	 * @param dedicacion     tipo de vinculación.
	 * @param especialidades especialidades del docente, sin repetidas.
	 */
	public Docente(Usuario usuario, Dedicacion dedicacion, List<Especialidad> especialidades) {
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
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario usuario vinculado.
	 */
	public void setUsuario(Usuario usuario) {
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
	public List<Especialidad> getEspecialidades() {
		return especialidades;
	}

	/**
	 * @param especialidades especialidades del docente, sin repetidas.
	 */
	public void setEspecialidades(List<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}
}
