package unbosque.edu.co.aulasync.dto;

import java.time.DayOfWeek;

/**
 * DTO de entrada y salida de {@code SesionHorario}.
 *
 * <p>
 * Tiene los mismos atributos que la entidad, con el grupo y el aula
 * representados por su id. En las respuestas incluye además datos
 * informativos del curso, del docente, del aula y de la sede, la hora de fin,
 * el nombre del día en español y si la sesión está ubicada.
 * </p>
 *
 * @version 1.0
 */
public class SesionHorarioDTO {

	private Long id;
	private Long grupoId;
	private Integer numeroSesion;
	private DayOfWeek dia;
	private Integer horaInicio;
	private Long aulaId;
	private String causa;
	private String detalleCausa;

	private Boolean ubicada;
	private String diaNombre;
	private Integer horaFin;
	private Integer duracionHoras;
	private Long cursoId;
	private String cursoCodigo;
	private String cursoNombre;
	private Integer semestre;
	private Integer numeroGrupo;
	private Long docenteId;
	private String docenteNombre;
	private String aulaNombre;
	private Long sedeId;
	private String sedeNombre;

	/**
	 * Constructor vacío requerido para la deserialización JSON; el servicio lo
	 * llena con setters.
	 */
	public SesionHorarioDTO() {
		// Requerido por Jackson.
	}

	/**
	 * @return identificador de la sesión.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador de la sesión.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return id del grupo.
	 */
	public Long getGrupoId() {
		return grupoId;
	}

	/**
	 * @param grupoId id del grupo.
	 */
	public void setGrupoId(Long grupoId) {
		this.grupoId = grupoId;
	}

	/**
	 * @return número de la sesión en la semana.
	 */
	public Integer getNumeroSesion() {
		return numeroSesion;
	}

	/**
	 * @param numeroSesion número de la sesión en la semana.
	 */
	public void setNumeroSesion(Integer numeroSesion) {
		this.numeroSesion = numeroSesion;
	}

	/**
	 * @return día asignado, o {@code null}.
	 */
	public DayOfWeek getDia() {
		return dia;
	}

	/**
	 * @param dia día asignado.
	 */
	public void setDia(DayOfWeek dia) {
		this.dia = dia;
	}

	/**
	 * @return hora de inicio, o {@code null}.
	 */
	public Integer getHoraInicio() {
		return horaInicio;
	}

	/**
	 * @param horaInicio hora de inicio.
	 */
	public void setHoraInicio(Integer horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * @return id del aula, o {@code null}.
	 */
	public Long getAulaId() {
		return aulaId;
	}

	/**
	 * @param aulaId id del aula.
	 */
	public void setAulaId(Long aulaId) {
		this.aulaId = aulaId;
	}

	/**
	 * @return causa por la que no está ubicada, o {@code null}.
	 */
	public String getCausa() {
		return causa;
	}

	/**
	 * @param causa causa por la que no está ubicada.
	 */
	public void setCausa(String causa) {
		this.causa = causa;
	}

	/**
	 * @return desglose de causas en texto, o {@code null}.
	 */
	public String getDetalleCausa() {
		return detalleCausa;
	}

	/**
	 * @param detalleCausa desglose de causas en texto.
	 */
	public void setDetalleCausa(String detalleCausa) {
		this.detalleCausa = detalleCausa;
	}

	/**
	 * @return si la sesión tiene día, hora y aula (informativo).
	 */
	public Boolean getUbicada() {
		return ubicada;
	}

	/**
	 * @param ubicada si la sesión está ubicada.
	 */
	public void setUbicada(Boolean ubicada) {
		this.ubicada = ubicada;
	}

	/**
	 * @return nombre del día en español (informativo).
	 */
	public String getDiaNombre() {
		return diaNombre;
	}

	/**
	 * @param diaNombre nombre del día en español.
	 */
	public void setDiaNombre(String diaNombre) {
		this.diaNombre = diaNombre;
	}

	/**
	 * @return hora de fin (informativo).
	 */
	public Integer getHoraFin() {
		return horaFin;
	}

	/**
	 * @param horaFin hora de fin.
	 */
	public void setHoraFin(Integer horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * @return duración en horas (informativo).
	 */
	public Integer getDuracionHoras() {
		return duracionHoras;
	}

	/**
	 * @param duracionHoras duración en horas.
	 */
	public void setDuracionHoras(Integer duracionHoras) {
		this.duracionHoras = duracionHoras;
	}

	/**
	 * @return id del curso (informativo).
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
	public Integer getSemestre() {
		return semestre;
	}

	/**
	 * @param semestre semestre del curso.
	 */
	public void setSemestre(Integer semestre) {
		this.semestre = semestre;
	}

	/**
	 * @return número del grupo (informativo).
	 */
	public Integer getNumeroGrupo() {
		return numeroGrupo;
	}

	/**
	 * @param numeroGrupo número del grupo.
	 */
	public void setNumeroGrupo(Integer numeroGrupo) {
		this.numeroGrupo = numeroGrupo;
	}

	/**
	 * @return id del docente (informativo).
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
	 * @return nombre del aula (informativo).
	 */
	public String getAulaNombre() {
		return aulaNombre;
	}

	/**
	 * @param aulaNombre nombre del aula.
	 */
	public void setAulaNombre(String aulaNombre) {
		this.aulaNombre = aulaNombre;
	}

	/**
	 * @return id de la sede (informativo).
	 */
	public Long getSedeId() {
		return sedeId;
	}

	/**
	 * @param sedeId id de la sede.
	 */
	public void setSedeId(Long sedeId) {
		this.sedeId = sedeId;
	}

	/**
	 * @return nombre de la sede (informativo).
	 */
	public String getSedeNombre() {
		return sedeNombre;
	}

	/**
	 * @param sedeNombre nombre de la sede.
	 */
	public void setSedeNombre(String sedeNombre) {
		this.sedeNombre = sedeNombre;
	}
}
