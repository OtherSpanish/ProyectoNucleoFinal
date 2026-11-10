package unbosque.edu.co.aulasync.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import unbosque.edu.co.aulasync.motor.MotivoRechazo;

/**
 * Objeto de valor con un nodo del grafo de conflictos: una sesión de clase,
 * sus vecinos por cada tipo de conflicto, su componente en el DSU y el
 * resultado que obtuvo en DSatur.
 *
 * @version 1.0
 */
public class NodoTraza {

	private int indice;
	private long grupoId;
	private int numeroSesion;
	private long cursoId;
	private String cursoCodigo;
	private String cursoNombre;
	private int semestre;
	private int numeroGrupo;
	private boolean nucleoComun;
	private long docenteId;
	private String docenteNombre;
	private int estudiantes;
	private int duracionHoras;
	private TipoAula tipoAula;
	private List<Integer> vecinosDocente = new ArrayList<>();
	private List<Integer> vecinosGrupo = new ArrayList<>();
	private List<Integer> vecinosBloque = new ArrayList<>();
	private int grado;
	private int raizDsu;
	private List<Integer> miembrosComponente = new ArrayList<>();
	private int orden;
	private boolean ubicada;
	private DayOfWeek dia;
	private String diaNombre;
	private Integer horaInicio;
	private Integer horaFin;
	private String aulaNombre;
	private String sedeNombre;
	private MotivoRechazo causa;
	private String causaDescripcion;

	/**
	 * Crea un nodo vacío; el servicio lo llena con los setters.
	 */
	public NodoTraza() {
	}

	/**
	 * @return índice del nodo en la lista de sesiones del motor.
	 */
	public int getIndice() {
		return indice;
	}

	/**
	 * @param indice índice del nodo en la lista de sesiones del motor.
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}

	/**
	 * @return id del grupo.
	 */
	public long getGrupoId() {
		return grupoId;
	}

	/**
	 * @param grupoId id del grupo.
	 */
	public void setGrupoId(long grupoId) {
		this.grupoId = grupoId;
	}

	/**
	 * @return número de la sesión dentro de la semana del grupo.
	 */
	public int getNumeroSesion() {
		return numeroSesion;
	}

	/**
	 * @param numeroSesion número de la sesión dentro de la semana del grupo.
	 */
	public void setNumeroSesion(int numeroSesion) {
		this.numeroSesion = numeroSesion;
	}

	/**
	 * @return id del curso.
	 */
	public long getCursoId() {
		return cursoId;
	}

	/**
	 * @param cursoId id del curso.
	 */
	public void setCursoId(long cursoId) {
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
	 * @return número de grupo (bloque).
	 */
	public int getNumeroGrupo() {
		return numeroGrupo;
	}

	/**
	 * @param numeroGrupo número de grupo (bloque).
	 */
	public void setNumeroGrupo(int numeroGrupo) {
		this.numeroGrupo = numeroGrupo;
	}

	/**
	 * @return si el curso es de núcleo común.
	 */
	public boolean isNucleoComun() {
		return nucleoComun;
	}

	/**
	 * @param nucleoComun si el curso es de núcleo común.
	 */
	public void setNucleoComun(boolean nucleoComun) {
		this.nucleoComun = nucleoComun;
	}

	/**
	 * @return id del docente.
	 */
	public long getDocenteId() {
		return docenteId;
	}

	/**
	 * @param docenteId id del docente.
	 */
	public void setDocenteId(long docenteId) {
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
	 * @return duración de la sesión en horas.
	 */
	public int getDuracionHoras() {
		return duracionHoras;
	}

	/**
	 * @param duracionHoras duración de la sesión en horas.
	 */
	public void setDuracionHoras(int duracionHoras) {
		this.duracionHoras = duracionHoras;
	}

	/**
	 * @return tipo de aula que exige el curso.
	 */
	public TipoAula getTipoAula() {
		return tipoAula;
	}

	/**
	 * @param tipoAula tipo de aula que exige el curso.
	 */
	public void setTipoAula(TipoAula tipoAula) {
		this.tipoAula = tipoAula;
	}

	/**
	 * @return nodos con los que comparte docente.
	 */
	public List<Integer> getVecinosDocente() {
		return vecinosDocente;
	}

	/**
	 * @param vecinosDocente nodos con los que comparte docente.
	 */
	public void setVecinosDocente(List<Integer> vecinosDocente) {
		this.vecinosDocente = vecinosDocente;
	}

	/**
	 * @return nodos del mismo grupo.
	 */
	public List<Integer> getVecinosGrupo() {
		return vecinosGrupo;
	}

	/**
	 * @param vecinosGrupo nodos del mismo grupo.
	 */
	public void setVecinosGrupo(List<Integer> vecinosGrupo) {
		this.vecinosGrupo = vecinosGrupo;
	}

	/**
	 * @return nodos de otra materia del mismo bloque.
	 */
	public List<Integer> getVecinosBloque() {
		return vecinosBloque;
	}

	/**
	 * @param vecinosBloque nodos de otra materia del mismo bloque.
	 */
	public void setVecinosBloque(List<Integer> vecinosBloque) {
		this.vecinosBloque = vecinosBloque;
	}

	/**
	 * @return cantidad total de vecinos en el grafo.
	 */
	public int getGrado() {
		return grado;
	}

	/**
	 * @param grado cantidad total de vecinos en el grafo.
	 */
	public void setGrado(int grado) {
		this.grado = grado;
	}

	/**
	 * @return nodo raíz de su componente en el DSU.
	 */
	public int getRaizDsu() {
		return raizDsu;
	}

	/**
	 * @param raizDsu nodo raíz de su componente en el DSU.
	 */
	public void setRaizDsu(int raizDsu) {
		this.raizDsu = raizDsu;
	}

	/**
	 * @return nodos de su misma componente en el DSU (incluido él).
	 */
	public List<Integer> getMiembrosComponente() {
		return miembrosComponente;
	}

	/**
	 * @param miembrosComponente nodos de su misma componente en el DSU (incluido él).
	 */
	public void setMiembrosComponente(List<Integer> miembrosComponente) {
		this.miembrosComponente = miembrosComponente;
	}

	/**
	 * @return paso de DSatur en que el motor la eligió (empieza en 1).
	 */
	public int getOrden() {
		return orden;
	}

	/**
	 * @param orden paso de DSatur en que el motor la eligió (empieza en 1).
	 */
	public void setOrden(int orden) {
		this.orden = orden;
	}

	/**
	 * @return si quedó con franja y aula.
	 */
	public boolean isUbicada() {
		return ubicada;
	}

	/**
	 * @param ubicada si quedó con franja y aula.
	 */
	public void setUbicada(boolean ubicada) {
		this.ubicada = ubicada;
	}

	/**
	 * @return día asignado o null.
	 */
	public DayOfWeek getDia() {
		return dia;
	}

	/**
	 * @param dia día asignado o null.
	 */
	public void setDia(DayOfWeek dia) {
		this.dia = dia;
	}

	/**
	 * @return nombre del día asignado o null.
	 */
	public String getDiaNombre() {
		return diaNombre;
	}

	/**
	 * @param diaNombre nombre del día asignado o null.
	 */
	public void setDiaNombre(String diaNombre) {
		this.diaNombre = diaNombre;
	}

	/**
	 * @return hora de inicio asignada o null.
	 */
	public Integer getHoraInicio() {
		return horaInicio;
	}

	/**
	 * @param horaInicio hora de inicio asignada o null.
	 */
	public void setHoraInicio(Integer horaInicio) {
		this.horaInicio = horaInicio;
	}

	/**
	 * @return hora de fin asignada o null.
	 */
	public Integer getHoraFin() {
		return horaFin;
	}

	/**
	 * @param horaFin hora de fin asignada o null.
	 */
	public void setHoraFin(Integer horaFin) {
		this.horaFin = horaFin;
	}

	/**
	 * @return aula asignada o null.
	 */
	public String getAulaNombre() {
		return aulaNombre;
	}

	/**
	 * @param aulaNombre aula asignada o null.
	 */
	public void setAulaNombre(String aulaNombre) {
		this.aulaNombre = aulaNombre;
	}

	/**
	 * @return sede asignada o null.
	 */
	public String getSedeNombre() {
		return sedeNombre;
	}

	/**
	 * @param sedeNombre sede asignada o null.
	 */
	public void setSedeNombre(String sedeNombre) {
		this.sedeNombre = sedeNombre;
	}

	/**
	 * @return causa principal si no se ubicó.
	 */
	public MotivoRechazo getCausa() {
		return causa;
	}

	/**
	 * @param causa causa principal si no se ubicó.
	 */
	public void setCausa(MotivoRechazo causa) {
		this.causa = causa;
	}

	/**
	 * @return explicación de la causa principal.
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
}
