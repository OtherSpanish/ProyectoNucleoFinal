package unbosque.edu.co.aulasync.model;

/**
 * Roles de los usuarios del sistema.
 *
 * <p>
 * Se guarda como texto en la tabla {@code usuario} y Spring Security lo usa
 * como autoridad con el prefijo {@code ROLE_} (por ejemplo
 * {@code ROLE_COMITE}).
 * </p>
 *
 * @version 1.0
 */
public enum Rol {

	/** Registra docentes, disponibilidad, especialidades, estudiantes, semestres y espacios físicos. */
	ADMIN,

	/** Comité táctico: registra materias, carga horaria, genera y ajusta el horario. */
	COMITE,

	/** Consulta sus materias, grupos, estudiantes y horario. */
	DOCENTE
}
