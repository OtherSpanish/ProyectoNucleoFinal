package unbosque.edu.co.aulasync.model;

/**
 * Objeto de valor con una arista del grafo de conflictos: dos sesiones que no
 * pueden cruzarse en el tiempo y las razones por las que están unidas.
 *
 * <p>
 * Una misma arista puede tener varias razones a la vez (por ejemplo, dos
 * sesiones del mismo grupo también comparten docente).
 * </p>
 *
 * @version 1.0
 */
public class AristaTraza {

	private final int origen;
	private final int destino;
	private final boolean mismoDocente;
	private final boolean mismoGrupo;
	private final boolean mismoBloque;

	/**
	 * Crea la arista.
	 *
	 * @param origen       índice del primer nodo.
	 * @param destino      índice del segundo nodo.
	 * @param mismoDocente {@code true} si las dos sesiones tienen el mismo
	 *                     docente.
	 * @param mismoGrupo   {@code true} si las dos sesiones son del mismo grupo.
	 * @param mismoBloque  {@code true} si son materias distintas del mismo
	 *                     bloque (mismo número de grupo, semestre igual o
	 *                     consecutivo).
	 */
	public AristaTraza(int origen, int destino, boolean mismoDocente, boolean mismoGrupo, boolean mismoBloque) {
		this.origen = origen;
		this.destino = destino;
		this.mismoDocente = mismoDocente;
		this.mismoGrupo = mismoGrupo;
		this.mismoBloque = mismoBloque;
	}

	/**
	 * @return índice del primer nodo.
	 */
	public int getOrigen() {
		return origen;
	}

	/**
	 * @return índice del segundo nodo.
	 */
	public int getDestino() {
		return destino;
	}

	/**
	 * @return {@code true} si comparten docente.
	 */
	public boolean isMismoDocente() {
		return mismoDocente;
	}

	/**
	 * @return {@code true} si son del mismo grupo.
	 */
	public boolean isMismoGrupo() {
		return mismoGrupo;
	}

	/**
	 * @return {@code true} si son materias distintas del mismo bloque.
	 */
	public boolean isMismoBloque() {
		return mismoBloque;
	}
}
