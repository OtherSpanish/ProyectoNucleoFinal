package unbosque.edu.co.aulasync.model;

/**
 * Evento que publican los servicios cuando cambian datos que afectan el
 * horario guardado (grupos, inscripciones, cursos, docentes, disponibilidad,
 * aulas, sedes o estudiantes).
 *
 * <p>
 * Lo escucha {@code SincronizacionHorarioService}, que revalida el horario
 * dentro de la misma transacción. Usar un evento evita dependencias
 * circulares entre servicios.
 * </p>
 *
 * @version 1.0
 */
public class CambioDatosMaestros {

	private final String origen;

	/**
	 * Crea el evento.
	 *
	 * @param origen descripción del cambio (por ejemplo "Grupo 5 actualizado").
	 */
	public CambioDatosMaestros(String origen) {
		this.origen = origen;
	}

	/**
	 * @return descripción del cambio.
	 */
	public String getOrigen() {
		return origen;
	}
}
