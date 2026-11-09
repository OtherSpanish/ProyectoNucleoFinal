package unbosque.edu.co.aulasync.motor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Grafo de conflictos entre sesiones, con listas de adyacencia.
 *
 * <p>
 * Dos sesiones son vecinas (no pueden cruzarse en el tiempo) si:
 * </p>
 * <ul>
 * <li>tienen el mismo docente,</li>
 * <li>son del mismo grupo, o</li>
 * <li>son de materias distintas del mismo bloque: mismo número de grupo y
 * semestre igual o consecutivo (por ejemplo Programación I G1 y Cálculo I
 * G1).</li>
 * </ul>
 *
 * <p>
 * Los grupos de la misma materia (G1 y G2 de Programación I) no son vecinos:
 * pueden ir a la misma hora en aulas distintas. Construcción O(n²).
 * </p>
 *
 * @version 2.0
 */
public class GrafoConflictos {

	private final List<Set<Integer>> adyacencia = new ArrayList<>();

	/**
	 * Construye el grafo comparando cada par de sesiones.
	 *
	 * @param sesiones nodos del grafo; el índice en la lista es el id del nodo.
	 */
	public GrafoConflictos(List<Sesion> sesiones) {
		for (int i = 0; i < sesiones.size(); i++) {
			adyacencia.add(new HashSet<Integer>());
		}
		for (int i = 0; i < sesiones.size(); i++) {
			for (int j = i + 1; j < sesiones.size(); j++) {
				if (enConflicto(sesiones.get(i), sesiones.get(j))) {
					adyacencia.get(i).add(j);
					adyacencia.get(j).add(i);
				}
			}
		}
	}

	/**
	 * Indica si dos sesiones no pueden cruzarse en el tiempo.
	 *
	 * @param a primera sesión.
	 * @param b segunda sesión.
	 * @return {@code true} si comparten docente, grupo o bloque (con materias
	 *         distintas).
	 */
	public static boolean enConflicto(Sesion a, Sesion b) {
		if (a.getDocenteId() == b.getDocenteId()) {
			return true;
		}
		if (a.getGrupoId() == b.getGrupoId()) {
			return true;
		}
		boolean materiasDistintas = a.getCursoId() != b.getCursoId();
		return materiasDistintas && mismoBloque(a, b);
	}

	/**
	 * Indica si dos sesiones pertenecen al mismo bloque de estudiantes: mismo
	 * número de grupo y semestre igual o consecutivo.
	 *
	 * @param a primera sesión.
	 * @param b segunda sesión.
	 * @return {@code true} si son del mismo bloque.
	 */
	public static boolean mismoBloque(Sesion a, Sesion b) {
		boolean mismoNumero = a.getNumeroGrupo() == b.getNumeroGrupo();
		boolean semestreCercano = Math.abs(a.getSemestre() - b.getSemestre()) <= 1;
		return mismoNumero && semestreCercano;
	}

	/**
	 * @param i índice del nodo.
	 * @return índices de sus vecinos.
	 */
	public Set<Integer> vecinosDe(int i) {
		return adyacencia.get(i);
	}

	/**
	 * @param i índice del nodo.
	 * @return cantidad de vecinos.
	 */
	public int grado(int i) {
		return adyacencia.get(i).size();
	}
}
