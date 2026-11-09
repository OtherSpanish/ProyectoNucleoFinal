package unbosque.edu.co.aulasync.dto;

/**
 * DTO de entrada y salida de {@code Grupo}.
 *
 * <p>
 * Tiene los mismos atributos que la entidad, salvo que el curso y el docente
 * se representan con su id (obligatorios al crear o actualizar) y los
 * estudiantes con su cantidad. En las respuestas incluye además datos
 * informativos del curso y del docente, y si el grupo está abierto (tiene el
 * mínimo de inscritos).
 * </p>
 *
 * @version 1.0
 */
public class GrupoDTO {

	private Long id;
	private Integer numero;
	private Long cursoId;
	private String cursoCodigo;
	private String cursoNombre;
	private Integer semestreNumero;
	private Long docenteId;
	private String docenteNombre;
	private Integer cantidadEstudiantes;
	private Boolean abierto;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public GrupoDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO de salida con todos sus datos.
	 *
	 * @param id                  identificador del grupo.
	 * @param numero              número del grupo.
	 * @param cursoId             id del curso.
	 * @param cursoCodigo         código del curso.
	 * @param cursoNombre         nombre del curso.
	 * @param semestreNumero      semestre del curso.
	 * @param docenteId           id del docente.
	 * @param docenteNombre       nombre del docente.
	 * @param cantidadEstudiantes estudiantes inscritos.
	 * @param abierto             si tiene el mínimo de inscritos.
	 */
	public GrupoDTO(Long id, Integer numero, Long cursoId, String cursoCodigo, String cursoNombre,
			Integer semestreNumero, Long docenteId, String docenteNombre, Integer cantidadEstudiantes,
			Boolean abierto) {
		this.id = id;
		this.numero = numero;
		this.cursoId = cursoId;
		this.cursoCodigo = cursoCodigo;
		this.cursoNombre = cursoNombre;
		this.semestreNumero = semestreNumero;
		this.docenteId = docenteId;
		this.docenteNombre = docenteNombre;
		this.cantidadEstudiantes = cantidadEstudiantes;
		this.abierto = abierto;
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
	public Integer getNumero() {
		return numero;
	}

	/**
	 * @param numero número del grupo.
	 */
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	/**
	 * @return id del curso.
	 */
	public Long getCursoId() {
		return cursoId;
	}

	/**
	 * @param cursoId id del curso.
	 */
	public void setCursoId(Long cursoId) {
		this.cursoId = cursoId;
	}

	/**
	 * @return código del curso (informativo).
	 */
	public String getCursoCodigo() {
		return cursoCodigo;
	}

	/**
	 * @param cursoCodigo código del curso.
	 */
	public void setCursoCodigo(String cursoCodigo) {
		this.cursoCodigo = cursoCodigo;
	}

	/**
	 * @return nombre del curso (informativo).
	 */
	public String getCursoNombre() {
		return cursoNombre;
	}

	/**
	 * @param cursoNombre nombre del curso.
	 */
	public void setCursoNombre(String cursoNombre) {
		this.cursoNombre = cursoNombre;
	}

	/**
	 * @return semestre del curso (informativo).
	 */
	public Integer getSemestreNumero() {
		return semestreNumero;
	}

	/**
	 * @param semestreNumero semestre del curso.
	 */
	public void setSemestreNumero(Integer semestreNumero) {
		this.semestreNumero = semestreNumero;
	}

	/**
	 * @return id del docente.
	 */
	public Long getDocenteId() {
		return docenteId;
	}

	/**
	 * @param docenteId id del docente.
	 */
	public void setDocenteId(Long docenteId) {
		this.docenteId = docenteId;
	}

	/**
	 * @return nombre del docente (informativo).
	 */
	public String getDocenteNombre() {
		return docenteNombre;
	}

	/**
	 * @param docenteNombre nombre del docente.
	 */
	public void setDocenteNombre(String docenteNombre) {
		this.docenteNombre = docenteNombre;
	}

	/**
	 * @return estudiantes inscritos (informativo).
	 */
	public Integer getCantidadEstudiantes() {
		return cantidadEstudiantes;
	}

	/**
	 * @param cantidadEstudiantes estudiantes inscritos.
	 */
	public void setCantidadEstudiantes(Integer cantidadEstudiantes) {
		this.cantidadEstudiantes = cantidadEstudiantes;
	}

	/**
	 * @return {@code true} si tiene el mínimo de inscritos (informativo).
	 */
	public Boolean getAbierto() {
		return abierto;
	}

	/**
	 * @param abierto si tiene el mínimo de inscritos.
	 */
	public void setAbierto(Boolean abierto) {
		this.abierto = abierto;
	}
}
