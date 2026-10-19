package unbosque.edu.co.aulasync.dto;

/**
 * DTO de salida para una sesión del endpoint de demostración
 * ({@code GET /api/motor/demo}).
 *
 * <p>
 * Se usa con datos fijos en memoria, por eso identifica el curso por su número
 * y el aula por su identificador, sin nombres. Es inmutable: solo se construye
 * y se lee.
 * </p>
 */
public class SesionDemoDto {

	private final int id;
	private final int curso;
	private final String dia;
	private final Integer horaInicio;
	private final Integer aula;
	private final String causa;
	
	/**
	 * Crea el DTO de una sesión de demostración.
	 *
	 * @param id         identificador de la sesión
	 * @param curso      identificador del curso al que pertenece
	 * @param dia        día de la semana asignado (por ejemplo {@code MONDAY}), o
	 *                   {@code null} si no se ubicó
	 * @param horaInicio hora de inicio asignada (7 a 21), o {@code null} si no se
	 *                   ubicó
	 * @param aula       identificador del aula asignada, o {@code null} si no se
	 *                   ubicó
	 * @param causa      motivo por el que no se ubicó, o {@code null} si se ubicó
	 *                   correctamente
	 */
	public SesionDemoDto(int id, int curso, String dia, Integer horaInicio, Integer aula, String causa) {
		this.id = id;
		this.curso = curso;
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.aula = aula;
		this.causa = causa;
	}

	/** @return identificador de la sesión */
	public int getId() {
		return id;
	}

	/** @return identificador del curso */
	public int getCurso() {
		return curso;
	}

	/**
	 * @return día de la semana asignado, o {@code null} si la sesión no se ubicó
	 */
	public String getDia() {
		return dia;
	}

	/** @return hora de inicio asignada, o {@code null} si la sesión no se ubicó */
	public Integer getHoraInicio() {
		return horaInicio;
	}

	/**
	 * @return identificador del aula asignada, o {@code null} si la sesión no se
	 *         ubicó
	 */
	public Integer getAula() {
		return aula;
	}

	/** @return motivo por el que no se ubicó, o {@code null} si se ubicó */
	public String getCausa() {
		return causa;
	}
}