package unbosque.edu.co.aulasync.model;

import java.text.Normalizer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Árbol de prefijos (Trie) para buscar registros por el inicio de su texto.
 *
 * <p>
 * Cada registro se indexa con varias claves: el texto completo y cada una de
 * sus palabras. Así, "ISIS-1101 Programación I" se encuentra con "isis",
 * "prog" o "programacion i". Las claves se normalizan: minúsculas, sin tildes
 * y sin espacios repetidos.
 * </p>
 *
 * <p>
 * Costos, con {@code L} el largo de la clave:
 * </p>
 * <ul>
 * <li>Insertar o eliminar un registro: O(L) por cada clave.</li>
 * <li>Buscar por prefijo: O(L) para llegar al nodo, más el recorrido de su
 * subárbol hasta completar el límite de resultados.</li>
 * </ul>
 *
 * <p>
 * Los resultados salen en orden lexicográfico de sus claves. Los métodos son
 * {@code synchronized} porque el índice se comparte entre peticiones.
 * </p>
 *
 * @version 1.0
 */
public class Trie {

	private final NodoTrie raiz = new NodoTrie();
	private final Map<Long, List<String>> clavesPorId = new HashMap<>();

	/**
	 * Indexa un registro. Si ya estaba indexado, primero se quitan sus claves
	 * anteriores (sirve para actualizar).
	 *
	 * @param id    identificador del registro.
	 * @param texto texto a indexar (por ejemplo código y nombre).
	 */
	public synchronized void insertar(Long id, String texto) {
		eliminar(id);
		List<String> claves = generarClaves(texto);
		for (String clave : claves) {
			NodoTrie nodo = raiz;
			for (int i = 0; i < clave.length(); i++) {
				nodo = nodo.obtenerOCrearHijo(clave.charAt(i));
			}
			nodo.getIds().add(id);
		}
		clavesPorId.put(id, claves);
	}

	/**
	 * Quita un registro del índice. Si no estaba, no hace nada.
	 *
	 * @param id identificador del registro.
	 */
	public synchronized void eliminar(Long id) {
		List<String> claves = clavesPorId.remove(id);
		if (claves == null) {
			return;
		}
		for (String clave : claves) {
			NodoTrie nodo = buscarNodo(clave);
			if (nodo != null) {
				nodo.getIds().remove(id);
			}
		}
	}

	/**
	 * Busca los registros con alguna clave que empiece por el prefijo.
	 *
	 * @param prefijo texto buscado; se normaliza igual que las claves.
	 * @param limite  máximo de resultados.
	 * @return ids sin repetir, en orden lexicográfico de sus claves; vacía si el
	 *         prefijo es nulo, está vacío o no coincide con nada.
	 */
	public synchronized List<Long> buscarPorPrefijo(String prefijo, int limite) {
		List<Long> resultado = new ArrayList<>();
		String normalizado = normalizar(prefijo);
		if (normalizado.isEmpty() || limite <= 0) {
			return resultado;
		}
		NodoTrie inicio = buscarNodo(normalizado);
		if (inicio == null) {
			return resultado;
		}

		Set<Long> vistos = new HashSet<>();
		Deque<NodoTrie> pila = new ArrayDeque<>();
		pila.push(inicio);
		while (!pila.isEmpty() && resultado.size() < limite) {
			NodoTrie actual = pila.pop();
			for (Long id : actual.getIds()) {
				if (resultado.size() < limite && vistos.add(id)) {
					resultado.add(id);
				}
			}
			List<NodoTrie> hijos = new ArrayList<>(actual.getHijos().values());
			for (int i = hijos.size() - 1; i >= 0; i--) {
				pila.push(hijos.get(i));
			}
		}
		return resultado;
	}

	/**
	 * Vacía el índice.
	 */
	public synchronized void limpiar() {
		raiz.getHijos().clear();
		raiz.getIds().clear();
		clavesPorId.clear();
	}

	/**
	 * @return cantidad de registros indexados.
	 */
	public synchronized int tamanio() {
		return clavesPorId.size();
	}

	/**
	 * Normaliza un texto: minúsculas, sin tildes, sin espacios a los lados y con
	 * un solo espacio entre palabras.
	 *
	 * @param texto texto original (puede ser {@code null}).
	 * @return texto normalizado, o cadena vacía si era {@code null}.
	 */
	public static String normalizar(String texto) {
		if (texto == null) {
			return "";
		}
		String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return sinTildes.toLowerCase().trim().replaceAll("\\s+", " ");
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private NodoTrie buscarNodo(String clave) {
		NodoTrie nodo = raiz;
		for (int i = 0; i < clave.length() && nodo != null; i++) {
			nodo = nodo.obtenerHijo(clave.charAt(i));
		}
		return nodo;
	}

	private List<String> generarClaves(String texto) {
		List<String> claves = new ArrayList<>();
		String completo = normalizar(texto);
		if (completo.isEmpty()) {
			return claves;
		}
		claves.add(completo);
		String[] palabras = completo.split(" ");
		for (String palabra : palabras) {
			if (!palabra.isEmpty() && !claves.contains(palabra)) {
				claves.add(palabra);
			}
		}
		return claves;
	}
}
