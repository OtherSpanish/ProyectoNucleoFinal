package unbosque.edu.co.aulasync.dto;

import java.util.Map;

/**
 * DTO de salida para una sesión del horario generado con los datos de la base
 * de datos ({@code POST /api/motor/generar}).
 *
 * <p>
 * A diferencia de {@link SesionDemoDto}, expone nombres legibles (curso,
 * docente, grupo, aula) para que el front pueda mostrarlos directamente. Si la
 * sesión no se ubicó, los campos de franja y aula son {@code null} y se informa
 * la causa principal junto con el desglose de franjas descartadas por cada
 * motivo.
 * </p>
 */
public class SesionGeneradaDto {

	private final int id;
	private final String curso;
	private final String docente;
	private final String grupo;
	private final String dia;
	private final Integer horaInicio;
	private final int duracion;
	private final String aula;
	private final String causa;
	private final Map<String, Integer> desglose;

	/**
	 * Crea el DTO de una sesión generada.
	 *
	 * @param id         identificador de la sesión dentro de la generación
	 * @param curso      nombre del curso
	 * @param docente    nombre del docente que la dicta
	 * @param grupo      nombre del grupo de estudiantes
	 * @param dia        día de la semana asignado, o {@code null} si no se ubicó
	 * @param horaInicio hora de inicio asignada, o {@code null} si no se ubicó
	 * @param duracion   duración de la sesión en horas
	 * @param aula       nombre del aula asignada, o {@code null} si no se ubicó
	 * @param causa      causa principal por la que no se ubicó, o {@code null} si
	 *                   se ubicó
	 * @param desglose   cantidad de franjas descartadas por cada motivo; vacío si
	 *                   la sesión se ubicó
	 */
	public SesionGeneradaDto(int id, String curso, String docente, String grupo, String dia, Integer horaInicio,
			int duracion, String aula, String causa, Map<String, Integer> desglose) {
		this.id = id;
		this.curso = curso;
		this.docente = docente;
		this.grupo = grupo;
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.duracion = duracion;
		this.aula = aula;
		this.causa = causa;
		this.desglose = desglose;
	}

	/** @return identificador de la sesión dentro de la generación */
	public int getId() {
		return id;
	}

	/** @return nombre del curso */
	public String getCurso() {
		return curso;
	}

	/** @return nombre del docente */
	public String getDocente() {
		return docente;
	}

	/** @return nombre del grupo */
	public String getGrupo() {
		return grupo;
	}

	/** @return día de la semana asignado, o {@code null} si no se ubicó */
	public String getDia() {
		return dia;
	}

	/** @return hora de inicio asignada, o {@code null} si no se ubicó */
	public Integer getHoraInicio() {
		return horaInicio;
	}

	/** @return duración de la sesión en horas */
	public int getDuracion() {
		return duracion;
	}

	/** @return nombre del aula asignada, o {@code null} si no se ubicó */
	public String getAula() {
		return aula;
	}

	/** @return causa principal de no ubicación, o {@code null} si se ubicó */
	public String getCausa() {
		return causa;
	}

	/**
	 * Devuelve cuántas franjas candidatas se descartaron por cada motivo (por
	 * ejemplo {@code DOCENTE_NO_DISPONIBLE: 60, MISMO_DIA_CURSO: 2}).
	 *
	 * @return desglose de causas; vacío si la sesión se ubicó
	 */
	public Map<String, Integer> getDesglose() {
		return desglose;
	}
}