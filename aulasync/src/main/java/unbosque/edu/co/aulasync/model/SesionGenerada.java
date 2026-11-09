package unbosque.edu.co.aulasync.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import unbosque.edu.co.aulasync.motor.MotivoRechazo;

/**
 * Objeto de valor con una sesión tal como la devuelve el motor, lista para
 * mostrarse.
 *
 * <p>
 * Si quedó ubicada trae día, horas, aula y sede. Si no, trae la causa
 * principal y el desglose de causas ordenado de mayor a menor.
 * </p>
 *
 * @version 1.0
 */
public class SesionGenerada {

	private Long grupoId;
	private int numeroSesion;
	private Long cursoId;
	private String cursoCodigo;
	private String cursoNombre;
	private int semestre;
	private int numeroGrupo;
	private boolean nucleoComun;
	private Long docenteId;
	private String docenteNombre;
	private int estudiantes;
	private int duracionHoras;

	private DayOfWeek dia;
	private String diaNombre;
	private Integer horaInicio;
	private Integer horaFin;
	private Long aulaId;
	private String aulaNombre;
	private Long sedeId;
	private String sedeNombre;

	private MotivoRechazo causa;
	private String causaDescripcion;
	private List<CausaDescartada> desglose = new ArrayList<>();

	/**
	 * Crea una sesión vacía; se llena con los setters.
	 */
	public SesionGenerada() {
		// Se llena con setters desde el servicio.
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
	 * @return número de la sesión en la semana (1, 2, 3).
	 */
	public int getNumeroSesion() {
		return numeroSesion;
	}

	/**
	 * @param numeroSesion número de la sesión en la semana.
	 */
	public void setNumeroSesion(int numeroSesion) {
		this.numeroSesion = numeroSesion;
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
	 * @return código del curso.
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
	 * @return nombre del curso.
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
	 * @return semestre del curso.
	 */
	public int getSemestre() {
		return semestre;
	}

	/**
	 * @param semestre semestre del curso.
	 */
	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	/**
	 * @return número del grupo.
	 */
	public int getNumeroGrupo() {
		return numeroGrupo;
	}

	/**
	 * @param numeroGrupo número del grupo.
	 */
	public void setNumeroGrupo(int numeroGrupo) {
		this.numeroGrupo = numeroGrupo;
	}

	/**
	 * @return si el curso es del núcleo común.
	 */
	public boolean isNucleoComun() {
		return nucleoComun;
	}

	/**
	 * @param nucleoComun si el curso es del núcleo común.
	 */
	public void setNucleoComun(boolean nucleoComun) {
		this.nucleoComun = nucleoComun;
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
	 * @return nombre del docente.
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
	 * @return inscritos del grupo.
	 */
	public int getEstudiantes() {
		return estudiantes;
	}

	/**
	 * @param estudiantes inscritos del grupo.
	 */
	public void setEstudiantes(int estudiantes) {
		this.estudiantes = estudiantes;
	}

	/**
	 * @return duración en horas.
	 */
	public int getDuracionHoras() {
		return duracionHoras;
	}

	/**
	 * @param duracionHoras duración en horas.
	 */
	public void setDuracionHoras(int duracionHoras) {
		this.duracionHoras = duracionHoras;
	}

	/**
	 * @return día asignado, o {@code null} si quedó sin ubicar.
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
	 * @return nombre del día en español (por ejemplo "Lunes").
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
	 * @return hora de inicio, o {@code null} si quedó sin ubicar.
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
	 * @return hora de fin, o {@code null} si quedó sin ubicar.
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
	 * @return id del aula, o {@code null} si quedó sin ubicar.
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
	 * @return nombre del aula.
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
	 * @return id de la sede, o {@code null} si quedó sin ubicar.
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
	 * @return nombre de la sede.
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

	/**
	 * @return causa principal si quedó sin ubicar.
	 */
	public MotivoRechazo getCausa() {
		return causa;
	}

	/**
	 * @param causa causa principal.
	 */
	public void setCausa(MotivoRechazo causa) {
		this.causa = causa;
	}

	/**
	 * @return explicación legible de la causa principal.
	 */
	public String getCausaDescripcion() {
		return causaDescripcion;
	}

	/**
	 * @param causaDescripcion explicación de la causa principal.
	 */
	public void setCausaDescripcion(String causaDescripcion) {
		this.causaDescripcion = causaDescripcion;
	}

	/**
	 * @return desglose de causas, de mayor a menor cantidad de franjas.
	 */
	public List<CausaDescartada> getDesglose() {
		return desglose;
	}

	/**
	 * @param desglose desglose de causas.
	 */
	public void setDesglose(List<CausaDescartada> desglose) {
		this.desglose = desglose;
	}
}
