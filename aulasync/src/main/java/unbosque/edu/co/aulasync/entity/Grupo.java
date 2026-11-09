package unbosque.edu.co.aulasync.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Grupo de una asignatura: cada curso se dicta en uno o más grupos (Grupo 1,
 * Grupo 2...), cada uno con su docente y sus estudiantes inscritos.
 *
 * <p>
 * El número es único dentro del curso. Un estudiante no puede estar dos veces
 * en el mismo grupo (restricción única en {@code grupo_estudiante}). El grupo
 * solo se abre (y el motor solo lo programa) si tiene el mínimo de inscritos.
 * </p>
 *
 * @version 1.0
 */
@Entity
@Table(name = "grupo", uniqueConstraints = @UniqueConstraint(name = "uk_grupo_curso_numero",
		columnNames = { "curso_id", "numero" }))
public class Grupo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private int numero;

	@ManyToOne(optional = false)
	@JoinColumn(name = "curso_id", nullable = false)
	private Curso curso;

	@ManyToOne(optional = false)
	@JoinColumn(name = "docente_id", nullable = false)
	private Docente docente;

	@ManyToMany
	@JoinTable(name = "grupo_estudiante",
			joinColumns = @JoinColumn(name = "grupo_id"),
			inverseJoinColumns = @JoinColumn(name = "estudiante_id"),
			uniqueConstraints = @UniqueConstraint(name = "uk_grupo_estudiante",
					columnNames = { "grupo_id", "estudiante_id" }))
	private List<Estudiante> estudiantes = new ArrayList<>();

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Grupo() {
		// Requerido por JPA.
	}

	/**
	 * Crea un grupo sin estudiantes.
	 *
	 * @param numero  número del grupo, único dentro del curso.
	 * @param curso   curso al que pertenece.
	 * @param docente docente que lo dicta.
	 */
	public Grupo(int numero, Curso curso, Docente docente) {
		this.numero = numero;
		this.curso = curso;
		this.docente = docente;
	}

	/**
	 * @return identificador del grupo.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del grupo.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return número del grupo.
	 */
	public int getNumero() {
		return numero;
	}

	/**
	 * @param numero número del grupo.
	 */
	public void setNumero(int numero) {
		this.numero = numero;
	}

	/**
	 * @return curso al que pertenece.
	 */
	public Curso getCurso() {
		return curso;
	}

	/**
	 * @param curso curso al que pertenece.
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/**
	 * @return docente que lo dicta.
	 */
	public Docente getDocente() {
		return docente;
	}

	/**
	 * @param docente docente que lo dicta.
	 */
	public void setDocente(Docente docente) {
		this.docente = docente;
	}

	/**
	 * @return estudiantes inscritos.
	 */
	public List<Estudiante> getEstudiantes() {
		return estudiantes;
	}

	/**
	 * @param estudiantes estudiantes inscritos.
	 */
	public void setEstudiantes(List<Estudiante> estudiantes) {
		this.estudiantes = estudiantes;
	}
}
