package unbosque.edu.co.aulasync.model;

import java.util.List;

import unbosque.edu.co.aulasync.dto.FranjaDocenteDTO;

/**
 * Objeto de valor con la disponibilidad semanal de un docente, lista para
 * mostrarse.
 *
 * <p>
 * No es una tabla. Trae los totales ya calculados por el backend (horas
 * disponibles y tope según la dedicación) para que el front no tenga que
 * sumar nada.
 * </p>
 *
 * @version 1.0
 */
public class DisponibilidadDocente {

	private final Long docenteId;
	private final String nombreDocente;
	private final Dedicacion dedicacion;
	private final int horasMaximas;
	private final int horasDisponibles;
	private final List<FranjaDocenteDTO> franjas;

	/**
	 * Crea la disponibilidad.
	 *
	 * @param docenteId        id del docente.
	 * @param nombreDocente    nombre del docente.
	 * @param dedicacion       dedicación del docente.
	 * @param horasMaximas     tope de horas de clase semanales.
	 * @param horasDisponibles suma de horas de todas las franjas.
	 * @param franjas          franjas ordenadas de lunes a sábado y por hora.
	 */
	public DisponibilidadDocente(Long docenteId, String nombreDocente, Dedicacion dedicacion, int horasMaximas,
			int horasDisponibles, List<FranjaDocenteDTO> franjas) {
		this.docenteId = docenteId;
		this.nombreDocente = nombreDocente;
		this.dedicacion = dedicacion;
		this.horasMaximas = horasMaximas;
		this.horasDisponibles = horasDisponibles;
		this.franjas = franjas;
	}

	/**
	 * @return id del docente.
	 */
	public Long getDocenteId() {
		return docenteId;
	}

	/**
	 * @return nombre del docente.
	 */
	public String getNombreDocente() {
		return nombreDocente;
	}

	/**
	 * @return dedicación del docente.
	 */
	public Dedicacion getDedicacion() {
		return dedicacion;
	}

	/**
	 * @return tope de horas de clase semanales.
	 */
	public int getHorasMaximas() {
		return horasMaximas;
	}

	/**
	 * @return suma de horas de todas las franjas.
	 */
	public int getHorasDisponibles() {
		return horasDisponibles;
	}

	/**
	 * @return franjas ordenadas de lunes a sábado y por hora de inicio.
	 */
	public List<FranjaDocenteDTO> getFranjas() {
		return franjas;
	}
}
