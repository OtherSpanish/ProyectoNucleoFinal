package unbosque.edu.co.aulasync.motor;

import java.util.EnumMap;
import java.util.Map;

import unbosque.edu.co.aulasync.model.TipoAula;

/**
 * Nodo del grafo de conflictos: una sesión semanal de un grupo.
 *
 * <p>
 * Un grupo con intensidad de 3 sesiones genera 3 objetos {@code Sesion} con el
 * mismo {@code grupoId} y {@code numeroSesion} 1, 2 y 3. Después de ejecutar el
 * motor guarda su franja y su aula, o la causa por la que quedó sin ubicar.
 * </p>
 *
 * @version 2.1
 */
public class Sesion {

	private final long grupoId;
	private final int numeroSesion;
	private final long cursoId;
	private final String cursoCodigo;
	private final int numeroGrupo;
	private final int semestre;
	private final boolean nucleoComun;
	private final long docenteId;
	private final int duracionHoras;
	private final TipoAula tipoAula;
	private final int estudiantes;

	private Franja franja;
	private AulaMotor aula;
	private MotivoRechazo causaSinUbicar;
	private Map<MotivoRechazo, Integer> desgloseCausas = new EnumMap<>(MotivoRechazo.class);

	/**
	 * Crea una sesión por ubicar.
	 *
	 * @param grupoId       id del grupo al que pertenece.
	 * @param numeroSesion  número de la sesión dentro de la semana (1, 2, 3).
	 * @param cursoId       id del curso.
	 * @param cursoCodigo   código del curso (para mensajes).
	 * @param numeroGrupo   número del grupo dentro del curso (define el bloque).
	 * @param semestre      semestre del curso.
	 * @param nucleoComun   si el curso es del núcleo común.
	 * @param docenteId     id del docente del grupo.
	 * @param duracionHoras duración de la sesión.
	 * @param tipoAula      tipo de aula que exige el curso.
	 * @param estudiantes   inscritos en el grupo (capacidad mínima del aula).
	 */
	public Sesion(long grupoId, int numeroSesion, long cursoId, String cursoCodigo, int numeroGrupo, int semestre,
			boolean nucleoComun, long docenteId, int duracionHoras, TipoAula tipoAula, int estudiantes) {
		this.grupoId = grupoId;
		this.numeroSesion = numeroSesion;
		this.cursoId = cursoId;
		this.cursoCodigo = cursoCodigo;
		this.numeroGrupo = numeroGrupo;
		this.semestre = semestre;
		this.nucleoComun = nucleoComun;
		this.docenteId = docenteId;
		this.duracionHoras = duracionHoras;
		this.tipoAula = tipoAula;
		this.estudiantes = estudiantes;
	}

	/**
	 * @return id del grupo.
	 */
	public long getGrupoId() {
		return grupoId;
	}

	/**
	 * @return número de la sesión dentro de la semana.
	 */
	public int getNumeroSesion() {
		return numeroSesion;
	}

	/**
	 * @return id del curso.
	 */
	public long getCursoId() {
		return cursoId;
	}

	/**
	 * @return código del curso.
	 */
	public String getCursoCodigo() {
		return cursoCodigo;
	}

	/**
	 * @return número del grupo dentro del curso.
	 */
	public int getNumeroGrupo() {
		return numeroGrupo;
	}

	/**
	 * @return semestre del curso.
	 */
	public int getSemestre() {
		return semestre;
	}

	/**
	 * @return {@code true} si el curso es del núcleo común.
	 */
	public boolean isNucleoComun() {
		return nucleoComun;
	}

	/**
	 * @return id del docente.
	 */
	public long getDocenteId() {
		return docenteId;
	}

	/**
	 * @return duración en horas.
	 */
	public int getDuracionHoras() {
		return duracionHoras;
	}

	/**
	 * @return tipo de aula requerido.
	 */
	public TipoAula getTipoAula() {
		return tipoAula;
	}

	/**
	 * @return inscritos del grupo.
	 */
	public int getEstudiantes() {
		return estudiantes;
	}

	/**
	 * @return franja asignada, o {@code null} si no se ha ubicado.
	 */
	public Franja getFranja() {
		return franja;
	}

	/**
	 * @param franja franja asignada.
	 */
	public void setFranja(Franja franja) {
		this.franja = franja;
	}

	/**
	 * @return aula asignada, o {@code null} si no se ha ubicado.
	 */
	public AulaMotor getAula() {
		return aula;
	}

	/**
	 * @param aula aula asignada.
	 */
	public void setAula(AulaMotor aula) {
		this.aula = aula;
	}

	/**
	 * @return motivo que descartó más franjas, si quedó sin ubicar.
	 */
	public MotivoRechazo getCausaSinUbicar() {
		return causaSinUbicar;
	}

	/**
	 * @param causaSinUbicar motivo principal.
	 */
	public void setCausaSinUbicar(MotivoRechazo causaSinUbicar) {
		this.causaSinUbicar = causaSinUbicar;
	}

	/**
	 * @return cuántas franjas descartó cada motivo, si quedó sin ubicar.
	 */
	public Map<MotivoRechazo, Integer> getDesgloseCausas() {
		return desgloseCausas;
	}

	/**
	 * @param desgloseCausas cuántas franjas descartó cada motivo.
	 */
	public void setDesgloseCausas(Map<MotivoRechazo, Integer> desgloseCausas) {
		this.desgloseCausas = desgloseCausas;
	}
}
