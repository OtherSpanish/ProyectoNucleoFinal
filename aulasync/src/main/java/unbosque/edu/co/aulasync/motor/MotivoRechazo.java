package unbosque.edu.co.aulasync.motor;

/**
 * Motivos por los que una franja se descarta para una sesión.
 *
 * <p>
 * El motor evalúa los primeros ocho en el orden declarado y devuelve el
 * primero que falla. Los últimos cuatro solo los produce el editor manual,
 * cuando el comité elige un día, una hora y un aula concretos.
 * </p>
 *
 * @version 2.2
 */
public enum MotivoRechazo {

	/** La franja cae fuera de la disponibilidad registrada del docente. */
	DOCENTE_NO_DISPONIBLE("Disponibilidad del docente", "El docente no tiene disponibilidad en esa franja."),

	/** Ubicar la sesión haría superar el tope de horas semanales del docente. */
	CARGA_DOCENTE("Tope de horas del docente", "El docente alcanzó su tope de horas semanales."),

	/**
	 * La sede no corresponde: Chía es solo para las materias núcleo de primer
	 * semestre en los días asignados a su bloque, y esos días el bloque no puede
	 * ir a otra sede.
	 */
	SEDE_NO_PERMITIDA("Sede permitida ese día", "La sede no corresponde a ese día para este grupo."),

	/** El docente ya tiene otra clase que se cruza con esa franja. */
	CHOQUE_DOCENTE("Choque con el docente", "El docente ya tiene otra clase a esa hora."),

	/** El mismo grupo ya tiene otra sesión que se cruza con esa franja. */
	CHOQUE_GRUPO("Choque con su grupo", "El grupo ya tiene otra sesión a esa hora."),

	/**
	 * Se cruza con otra materia del mismo bloque: mismo número de grupo y
	 * semestre igual o consecutivo.
	 */
	CHOQUE_BLOQUE("Choque con su bloque", "Se cruza con otra materia del mismo bloque (mismo número de grupo, semestre igual o consecutivo)."),

	/** El grupo ya tiene una sesión ese día (sus sesiones van en días distintos). */
	MISMO_DIA_GRUPO("Un día por grupo (DSU)", "El grupo ya tiene una sesión ese día."),

	/** No hay aula libre del tipo y capacidad requeridos en la sede de la franja. */
	SIN_AULA("Aula libre y compatible", "No hay un aula libre del tipo y la capacidad requeridos en esa sede."),

	/** Solo editor: la sede del aula no tiene clases a esa hora. */
	FUERA_DE_FRANJA_SEDE("Bloque de la sede",
			"La clase debe empezar al inicio de un bloque de 2 horas de la sede y no puede caer en horas sin clase (como el almuerzo) ni fuera de la jornada."),

	/** Solo editor: el aula está marcada como no disponible. */
	AULA_NO_DISPONIBLE("Aula disponible", "El aula está marcada como no disponible."),

	/** Solo editor: el aula no es del tipo exigido o no tiene capacidad. */
	AULA_INCOMPATIBLE("Aula compatible", "El aula no es del tipo que exige el curso o no tiene capacidad para los inscritos."),

	/** Solo editor: el aula ya tiene otra clase que se cruza. */
	AULA_OCUPADA("Aula libre", "El aula ya tiene otra clase a esa hora.");

	private final String etiqueta;
	private final String descripcion;

	MotivoRechazo(String etiqueta, String descripcion) {
		this.etiqueta = etiqueta;
		this.descripcion = descripcion;
	}

	/**
	 * @return nombre corto de la regla que produce el motivo (para listas y
	 *         gráficas).
	 */
	public String getEtiqueta() {
		return etiqueta;
	}

	/**
	 * @return explicación legible del motivo, para mostrar al comité.
	 */
	public String getDescripcion() {
		return descripcion;
	}
}
