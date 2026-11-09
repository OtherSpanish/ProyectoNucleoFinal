package unbosque.edu.co.aulasync.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias del {@link Trie}. No usan base de datos ni Spring.
 */
class TrieTest {

	private Trie trie;

	@BeforeEach
	void preparar() {
		trie = new Trie();
		trie.insertar(1L, "ISIS-1101 Programación I");
		trie.insertar(2L, "MATE-1101 Cálculo I");
		trie.insertar(3L, "ISIS-2201 Programación II");
	}

	@Test
	void buscarPorPrefijoDeNombreTest() {
		assertEquals(Arrays.asList(1L, 3L), trie.buscarPorPrefijo("prog", 20));
	}

	@Test
	void buscarPorPrefijoDeCodigoTest() {
		assertEquals(Arrays.asList(1L, 3L), trie.buscarPorPrefijo("isis", 20));
		assertEquals(Arrays.asList(2L), trie.buscarPorPrefijo("MATE-11", 20));
	}

	@Test
	void buscarSinTildesNiMayusculasTest() {
		assertEquals(Arrays.asList(2L), trie.buscarPorPrefijo("calc", 20));
		assertEquals(Arrays.asList(2L), trie.buscarPorPrefijo("CÁL", 20));
	}

	@Test
	void buscarTextoCompletoTest() {
		assertEquals(Arrays.asList(3L), trie.buscarPorPrefijo("programacion ii", 20));
	}

	@Test
	void eliminarTest() {
		trie.eliminar(1L);
		assertEquals(Arrays.asList(3L), trie.buscarPorPrefijo("prog", 20));
		assertEquals(2, trie.tamanio());
	}

	@Test
	void reindexarTest() {
		trie.insertar(2L, "MATE-1101 Álgebra lineal");
		assertTrue(trie.buscarPorPrefijo("calc", 20).isEmpty());
		assertEquals(Arrays.asList(2L), trie.buscarPorPrefijo("alg", 20));
		assertEquals(3, trie.tamanio());
	}

	@Test
	void ordenLexicograficoTest() {
		Trie nombres = new Trie();
		nombres.insertar(1L, "Ana Torres");
		nombres.insertar(2L, "Andrés Ruiz");
		nombres.insertar(3L, "Anabel Paz");
		assertEquals(Arrays.asList(1L, 3L, 2L), nombres.buscarPorPrefijo("an", 20));
	}

	@Test
	void limiteDeResultadosTest() {
		Trie muchos = new Trie();
		for (long i = 1; i <= 30; i++) {
			muchos.insertar(i, "Curso " + i);
		}
		List<Long> resultado = muchos.buscarPorPrefijo("curso", 5);
		assertEquals(5, resultado.size());
	}

	@Test
	void prefijoVacioONuloTest() {
		assertTrue(trie.buscarPorPrefijo("", 20).isEmpty());
		assertTrue(trie.buscarPorPrefijo("   ", 20).isEmpty());
		assertTrue(trie.buscarPorPrefijo(null, 20).isEmpty());
	}

	@Test
	void prefijoSinCoincidenciasTest() {
		assertTrue(trie.buscarPorPrefijo("fisica", 20).isEmpty());
	}
}
