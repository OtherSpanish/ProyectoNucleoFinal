package unbosque.edu.co.aulasync.model;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Nodo de un {@link Trie}.
 *
 * <p>
 * Cada nodo representa un carácter. Sus hijos se guardan en un
 * {@link TreeMap}, así que se recorren en orden lexicográfico. En
 * {@code ids} están los registros cuya clave termina exactamente en este
 * nodo.
 * </p>
 *
 * @version 1.0
 */
public class NodoTrie {

	private final Map<Character, NodoTrie> hijos = new TreeMap<>();
	private final Set<Long> ids = new LinkedHashSet<>();

	/**
	 * Devuelve el hijo para un carácter, creándolo si no existe.
	 *
	 * @param caracter carácter del hijo.
	 * @return el hijo existente o el recién creado.
	 */
	public NodoTrie obtenerOCrearHijo(char caracter) {
		NodoTrie hijo = hijos.get(caracter);
		if (hijo == null) {
			hijo = new NodoTrie();
			hijos.put(caracter, hijo);
		}
		return hijo;
	}

	/**
	 * Devuelve el hijo para un carácter.
	 *
	 * @param caracter carácter del hijo.
	 * @return el hijo, o {@code null} si no existe.
	 */
	public NodoTrie obtenerHijo(char caracter) {
		return hijos.get(caracter);
	}

	/**
	 * @return hijos del nodo, ordenados por carácter.
	 */
	public Map<Character, NodoTrie> getHijos() {
		return hijos;
	}

	/**
	 * @return ids de los registros cuya clave termina en este nodo.
	 */
	public Set<Long> getIds() {
		return ids;
	}
}
