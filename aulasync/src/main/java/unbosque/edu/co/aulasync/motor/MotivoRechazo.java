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
 * @version 2.1
 */
public enum MotivoRechazo {

	/** La franja cae fuera de la disponibilidad registrada del docente. */
	DOCENTE_NO_DISPONIBLE("El docente no tiene disponibilidad en esa franja."),

	/** Ubicar la sesión haría superar el tope de horas semanales del docente. */
	CARGA_DOCENTE("El docente alcanzó su tope de horas semanales."),

	/**
	 * La sede no corresponde: Chía es solo para las materias núcleo de primer
	 * semestre en los días asignados a su bloque, y esos días el bloque no puede
	 * ir a otra sede.
	 */
	SEDE_NO_PERMITIDA("La sede no corresponde a ese día para este grupo."),

	/** El docente ya tiene otra clase que se cruza con esa franja. */
	CHOQUE_DOCENTE("El docente ya tiene otra clase a esa hora."),

	/** El mismo grupo ya tiene otra sesión que se cruza con esa franja. */
	CHOQUE_GRUPO("El grupo ya tiene otra sesión a esa hora."),

	/**
	 * Se cruza con otra materia del mismo bloque: mismo número de grupo y
	 * semestre igual o consecutivo.
	 */
	CHOQUE_BLOQUE("Se cruza con otra materia del mismo bloque (mismo número de grupo, semestre igual o consecutivo)."),

	/** El grupo ya tiene una sesión ese día (sus sesiones van en días distintos). */
	MISMO_DIA_GRUPO("El grupo ya tiene una sesión ese día."),

	/** No hay aula libre del tipo y capacidad requeridos en la sede de la franja. */
	SIN_AULA("No hay un aula libre del tipo y la capacidad requeridos en esa sede."),

	/** Solo editor: la sede del aula no tiene clases a esa hora. */
	FUERA_DE_FRANJA_SEDE("La sede no tiene clases a esa hora (por ejemplo, el almuerzo o fuera de la jornada)."),

	/** Solo editor: el aula está marcada como no disponible. */
	AULA_NO_DISPONIBLE("El aula está marcada como no disponible."),

	/** Solo editor: el aula no es del tipo exigido o no tiene capacidad. */
	AULA_INCOMPATIBLE("El aula no es del tipo que exige el curso o no tiene capacidad para los inscritos."),

	/** Solo editor: el aula ya tiene otra clase que se cruza. */
	AULA_OCUPADA("El aula ya tiene otra clase a esa hora.");

	private final String descripcion;

	MotivoRechazo(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return explicación legible del motivo, para mostrar al comité.
	 */
	public String getDescripcion() {
		return descripcion;
	}
}
