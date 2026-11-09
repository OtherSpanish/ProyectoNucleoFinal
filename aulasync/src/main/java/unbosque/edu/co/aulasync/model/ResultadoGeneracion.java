package unbosque.edu.co.aulasync.model;

import java.util.List;

/**
 * Objeto de valor con el resultado de una ejecución del motor de horarios.
 *
 * <p>
 * Incluye los totales ya calculados, las sesiones ubicadas y sin ubicar, los
 * grupos que no entraron al motor por no tener el mínimo de inscritos y los
 * avisos (por ejemplo, días de Chía incompletos).
 * </p>
 *
 * @version 1.0
 */
public class ResultadoGeneracion {

	private final int gruposProgramados;
	private final List<String> gruposNoAbiertos;
	private final int totalSesiones;
	private final int sesionesUbicadas;
	private final int sesionesSinUbicar;
	private final List<SesionGenerada> ubicadas;
	private final List<SesionGenerada> sinUbicar;
	private final List<String> avisos;

	/**
	 * Crea el resultado. Los totales se calculan a partir de las listas.
	 *
	 * @param gruposProgramados grupos abiertos que entraron al motor.
	 * @param gruposNoAbiertos  descripción de los grupos que no entraron.
	 * @param ubicadas          sesiones ubicadas, ordenadas.
	 * @param sinUbicar         sesiones sin ubicar, ordenadas.
	 * @param avisos            advertencias del motor.
	 */
	public ResultadoGeneracion(int gruposProgramados, List<String> gruposNoAbiertos, List<SesionGenerada> ubicadas,
			List<SesionGenerada> sinUbicar, List<String> avisos) {
		this.gruposProgramados = gruposProgramados;
		this.gruposNoAbiertos = gruposNoAbiertos;
		this.ubicadas = ubicadas;
		this.sinUbicar = sinUbicar;
		this.avisos = avisos;
		this.sesionesUbicadas = ubicadas.size();
		this.sesionesSinUbicar = sinUbicar.size();
		this.totalSesiones = ubicadas.size() + sinUbicar.size();
	}

	/**
	 * @return grupos abiertos que entraron al motor.
	 */
	public int getGruposProgramados() {
		return gruposProgramados;
	}

	/**
	 * @return descripción de los grupos que no entraron por falta de inscritos.
	 */
	public List<String> getGruposNoAbiertos() {
		return gruposNoAbiertos;
	}

	/**
	 * @return total de sesiones procesadas.
	 */
	public int getTotalSesiones() {
		return totalSesiones;
	}

	/**
	 * @return cantidad de sesiones ubicadas.
	 */
	public int getSesionesUbicadas() {
		return sesionesUbicadas;
	}

	/**
	 * @return cantidad de sesiones sin ubicar.
	 */
	public int getSesionesSinUbicar() {
		return sesionesSinUbicar;
	}

	/**
	 * @return sesiones ubicadas, ordenadas por semestre, día y hora.
	 */
	public List<SesionGenerada> getUbicadas() {
		return ubicadas;
	}

	/**
	 * @return sesiones sin ubicar, ordenadas por semestre y curso.
	 */
	public List<SesionGenerada> getSinUbicar() {
		return sinUbicar;
	}

	/**
	 * @return advertencias del motor.
	 */
	public List<String> getAvisos() {
		return avisos;
	}
}
