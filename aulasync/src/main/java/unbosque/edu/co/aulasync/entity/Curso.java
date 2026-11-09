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
import unbosque.edu.co.aulasync.model.IntensidadHoraria;
import unbosque.edu.co.aulasync.model.TipoAula;

/**
 * Asignatura del plan de estudios.
 *
 * <p>
 * Pertenece a un {@link Semestre}, exige una {@link Especialidad} a sus
 * docentes y un {@link TipoAula} a sus salones. Su
 * {@link IntensidadHoraria intensidad} define cuántas sesiones semanales tiene
 * y cuánto dura cada una. Se dicta en uno o más grupos. El código es único.
 * </p>
 *
 * <p>
 * {@code nucleoComun} marca las materias del núcleo común (por ejemplo
 * Matemáticas I o Ciencias para la Vida). Las núcleo de primer semestre se
 * dictan en la sede de primer semestre (Chía); las propias de la carrera, en
 * Usaquén.
 * </p>
 *
 * @version 1.1
 */
@Entity
@Table(name = "curso")
public class Curso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String codigo;

	@Column(nullable = false, length = 120)
	private String nombre;

	@ManyToOne(optional = false)
	@JoinColumn(name = "semestre_id", nullable = false)
	private Semestre semestre;

	@ManyToOne(optional = false)
	@JoinColumn(name = "especialidad_id", nullable = false)
	private Especialidad especialidad;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_aula", nullable = false, length = 20)
	private TipoAula tipoAula;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private IntensidadHoraria intensidad;

	@Column(name = "nucleo_comun", nullable = false)
	private boolean nucleoComun;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Curso() {
		// Requerido por JPA.
	}

	/**
	 * Crea un curso con todos sus datos.
	 *
	 * @param codigo       código único.
	 * @param nombre       nombre de la asignatura.
	 * @param semestre     semestre al que pertenece.
	 * @param especialidad especialidad que debe tener el docente.
	 * @param tipoAula     tipo de aula que necesita.
	 * @param intensidad   patrón de sesiones semanales.
	 * @param nucleoComun  si es materia del núcleo común.
	 */
	public Curso(String codigo, String nombre, Semestre semestre, Especialidad especialidad, TipoAula tipoAula,
			IntensidadHoraria intensidad, boolean nucleoComun) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.semestre = semestre;
		this.especialidad = especialidad;
		this.tipoAula = tipoAula;
		this.intensidad = intensidad;
		this.nucleoComun = nucleoComun;
	}

	/**
	 * @return identificador del curso.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del curso.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return código único.
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo código único.
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return nombre de la asignatura.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre de la asignatura.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return semestre al que pertenece.
	 */
	public Semestre getSemestre() {
		return semestre;
	}

	/**
	 * @param semestre semestre al que pertenece.
	 */
	public void setSemestre(Semestre semestre) {
		this.semestre = semestre;
	}

	/**
	 * @return especialidad que debe tener el docente.
	 */
	public Especialidad getEspecialidad() {
		return especialidad;
	}

	/**
	 * @param especialidad especialidad que debe tener el docente.
	 */
	public void setEspecialidad(Especialidad especialidad) {
		this.especialidad = especialidad;
	}

	/**
	 * @return tipo de aula que necesita.
	 */
	public TipoAula getTipoAula() {
		return tipoAula;
	}

	/**
	 * @param tipoAula tipo de aula que necesita.
	 */
	public void setTipoAula(TipoAula tipoAula) {
		this.tipoAula = tipoAula;
	}

	/**
	 * @return patrón de sesiones semanales.
	 */
	public IntensidadHoraria getIntensidad() {
		return intensidad;
	}

	/**
	 * @param intensidad patrón de sesiones semanales.
	 */
	public void setIntensidad(IntensidadHoraria intensidad) {
		this.intensidad = intensidad;
	}

	/**
	 * @return {@code true} si es materia del núcleo común.
	 */
	public boolean isNucleoComun() {
		return nucleoComun;
	}

	/**
	 * @param nucleoComun si es materia del núcleo común.
	 */
	public void setNucleoComun(boolean nucleoComun) {
		this.nucleoComun = nucleoComun;
	}
}
