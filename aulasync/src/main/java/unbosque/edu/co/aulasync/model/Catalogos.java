package unbosque.edu.co.aulasync.model;

import java.util.List;

/**
 * Objeto de valor con los catálogos que el frontend necesita para llenar
 * listas y mostrar nombres, para que no tenga datos del dominio escritos a
 * mano: roles, tipos de aula, dedicaciones, intensidades, días hábiles, horas
 * de la jornada y reglas (motivos de rechazo) del motor.
 *
 * @version 1.0
 */
public class Catalogos {

	private final List<OpcionCatalogo> roles;
	private final List<OpcionCatalogo> tiposAula;
	private final List<OpcionCatalogo> dedicaciones;
	private final List<OpcionCatalogo> intensidades;
	private final List<OpcionCatalogo> dias;
	private final List<Integer> horasJornada;
	private final List<OpcionCatalogo> motivos;

	/**
	 * Crea los catálogos.
	 *
	 * @param roles        roles del sistema.
	 * @param tiposAula    tipos de aula.
	 * @param dedicaciones dedicaciones docentes con su tope de horas.
	 * @param intensidades intensidades horarias con sesiones y duración.
	 * @param dias         días hábiles con su hora de cierre.
	 * @param horasJornada horas desde la apertura hasta el cierre de la jornada.
	 * @param motivos      motivos de rechazo, en el orden en que se evalúan.
	 */
	public Catalogos(List<OpcionCatalogo> roles, List<OpcionCatalogo> tiposAula, List<OpcionCatalogo> dedicaciones,
			List<OpcionCatalogo> intensidades, List<OpcionCatalogo> dias, List<Integer> horasJornada,
			List<OpcionCatalogo> motivos) {
		this.roles = roles;
		this.tiposAula = tiposAula;
		this.dedicaciones = dedicaciones;
		this.intensidades = intensidades;
		this.dias = dias;
		this.horasJornada = horasJornada;
		this.motivos = motivos;
	}

	/**
	 * @return roles del sistema.
	 */
	public List<OpcionCatalogo> getRoles() {
		return roles;
	}

	/**
	 * @return tipos de aula.
	 */
	public List<OpcionCatalogo> getTiposAula() {
		return tiposAula;
	}

	/**
	 * @return dedicaciones docentes.
	 */
	public List<OpcionCatalogo> getDedicaciones() {
		return dedicaciones;
	}

	/**
	 * @return intensidades horarias.
	 */
	public List<OpcionCatalogo> getIntensidades() {
		return intensidades;
	}

	/**
	 * @return días hábiles.
	 */
	public List<OpcionCatalogo> getDias() {
		return dias;
	}

	/**
	 * @return horas de la jornada (de la apertura al cierre, ambas incluidas).
	 */
	public List<Integer> getHorasJornada() {
		return horasJornada;
	}

	/**
	 * @return motivos de rechazo.
	 */
	public List<OpcionCatalogo> getMotivos() {
		return motivos;
	}
}
