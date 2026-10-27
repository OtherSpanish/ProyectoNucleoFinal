package unbosque.edu.co.aulasync.exception;

/**
 * Dato faltante o que incumple una regla de negocio (respuesta 400). Se usa
 * para las validaciones de usuarios, docentes, aulas, grupos, secciones y
 * movimientos de horario.
 */
public class DatoInvalidoException extends ValidacionException {

	private static final long serialVersionUID = 1L;

	public DatoInvalidoException(String mensaje) {
		super(mensaje);
	}
}
