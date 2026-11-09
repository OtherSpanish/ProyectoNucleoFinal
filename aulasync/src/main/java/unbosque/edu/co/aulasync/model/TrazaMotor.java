package unbosque.edu.co.aulasync.model;

import java.util.List;

/**
 * Objeto de valor con la traza completa de una ejecución del motor, pensada
 * para explicar visualmente cómo se arma el horario: los nodos (sesiones), las
 * aristas del grafo de conflictos, las componentes del DSU y cada paso de
 * DSatur. No guarda nada en la base de datos.
 *
 * @version 1.0
 */
public class TrazaMotor {

	private int gruposProgramados;
	private List<String> gruposNoAbiertos;
	private int totalDocentes;
	private int totalSedes;
	private int totalAulas;
	private int totalAristas;
	private int aristasPorDocente;
	private int aristasPorGrupo;
	private int aristasPorBloque;
	private int totalComponentes;
	private int sesionesUbicadas;
	private int sesionesSinUbicar;
	private List<NodoTraza> nodos;
	private List<AristaTraza> aristas;
	private List<PasoTraza> pasos;
	private List<String> avisos;

	/**
	 * Crea una traza vacía; el servicio la llena con los setters.
	 */
	public TrazaMotor() {
	}

	/**
	 * @return grupos abiertos que entraron al motor.
	 */
	public int getGruposProgramados() {
		return gruposProgramados;
	}

	/**
	 * @param gruposProgramados grupos abiertos que entraron al motor.
	 */
	public void setGruposProgramados(int gruposProgramados) {
		this.gruposProgramados = gruposProgramados;
	}

	/**
	 * @return grupos que no entraron por no tener el mínimo de inscritos.
	 */
	public List<String> getGruposNoAbiertos() {
		return gruposNoAbiertos;
	}

	/**
	 * @param gruposNoAbiertos grupos que no entraron al motor.
	 */
	public void setGruposNoAbiertos(List<String> gruposNoAbiertos) {
		this.gruposNoAbiertos = gruposNoAbiertos;
	}

	/**
	 * @return docentes que dictan las sesiones.
	 */
	public int getTotalDocentes() {
		return totalDocentes;
	}

	/**
	 * @param totalDocentes docentes que dictan las sesiones.
	 */
	public void setTotalDocentes(int totalDocentes) {
		this.totalDocentes = totalDocentes;
	}

	/**
	 * @return sedes consideradas.
	 */
	public int getTotalSedes() {
		return totalSedes;
	}

	/**
	 * @param totalSedes sedes consideradas.
	 */
	public void setTotalSedes(int totalSedes) {
		this.totalSedes = totalSedes;
	}

	/**
	 * @return aulas disponibles consideradas.
	 */
	public int getTotalAulas() {
		return totalAulas;
	}

	/**
	 * @param totalAulas aulas disponibles consideradas.
	 */
	public void setTotalAulas(int totalAulas) {
		this.totalAulas = totalAulas;
	}

	/**
	 * @return aristas del grafo.
	 */
	public int getTotalAristas() {
		return totalAristas;
	}

	/**
	 * @param totalAristas aristas del grafo.
	 */
	public void setTotalAristas(int totalAristas) {
		this.totalAristas = totalAristas;
	}

	/**
	 * @return aristas en las que las sesiones comparten docente.
	 */
	public int getAristasPorDocente() {
		return aristasPorDocente;
	}

	/**
	 * @param aristasPorDocente aristas por docente compartido.
	 */
	public void setAristasPorDocente(int aristasPorDocente) {
		this.aristasPorDocente = aristasPorDocente;
	}

	/**
	 * @return aristas entre sesiones del mismo grupo.
	 */
	public int getAristasPorGrupo() {
		return aristasPorGrupo;
	}

	/**
	 * @param aristasPorGrupo aristas por mismo grupo.
	 */
	public void setAristasPorGrupo(int aristasPorGrupo) {
		this.aristasPorGrupo = aristasPorGrupo;
	}

	/**
	 * @return aristas entre materias distintas del mismo bloque.
	 */
	public int getAristasPorBloque() {
		return aristasPorBloque;
	}

	/**
	 * @param aristasPorBloque aristas por mismo bloque.
	 */
	public void setAristasPorBloque(int aristasPorBloque) {
		this.aristasPorBloque = aristasPorBloque;
	}

	/**
	 * @return componentes del DSU (una por grupo).
	 */
	public int getTotalComponentes() {
		return totalComponentes;
	}

	/**
	 * @param totalComponentes componentes del DSU.
	 */
	public void setTotalComponentes(int totalComponentes) {
		this.totalComponentes = totalComponentes;
	}

	/**
	 * @return sesiones que quedaron ubicadas.
	 */
	public int getSesionesUbicadas() {
		return sesionesUbicadas;
	}

	/**
	 * @param sesionesUbicadas sesiones ubicadas.
	 */
	public void setSesionesUbicadas(int sesionesUbicadas) {
		this.sesionesUbicadas = sesionesUbicadas;
	}

	/**
	 * @return sesiones que no se pudieron ubicar.
	 */
	public int getSesionesSinUbicar() {
		return sesionesSinUbicar;
	}

	/**
	 * @param sesionesSinUbicar sesiones sin ubicar.
	 */
	public void setSesionesSinUbicar(int sesionesSinUbicar) {
		this.sesionesSinUbicar = sesionesSinUbicar;
	}

	/**
	 * @return nodos del grafo, en el orden de sus índices.
	 */
	public List<NodoTraza> getNodos() {
		return nodos;
	}

	/**
	 * @param nodos nodos del grafo.
	 */
	public void setNodos(List<NodoTraza> nodos) {
		this.nodos = nodos;
	}

	/**
	 * @return aristas del grafo.
	 */
	public List<AristaTraza> getAristas() {
		return aristas;
	}

	/**
	 * @param aristas aristas del grafo.
	 */
	public void setAristas(List<AristaTraza> aristas) {
		this.aristas = aristas;
	}

	/**
	 * @return pasos de DSatur en orden.
	 */
	public List<PasoTraza> getPasos() {
		return pasos;
	}

	/**
	 * @param pasos pasos de DSatur.
	 */
	public void setPasos(List<PasoTraza> pasos) {
		this.pasos = pasos;
	}

	/**
	 * @return avisos de la generación.
	 */
	public List<String> getAvisos() {
		return avisos;
	}

	/**
	 * @param avisos avisos de la generación.
	 */
	public void setAvisos(List<String> avisos) {
		this.avisos = avisos;
	}
}
