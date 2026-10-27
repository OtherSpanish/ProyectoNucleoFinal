package unbosque.edu.co.aulasync.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import unbosque.edu.co.aulasync.exception.NoAutenticadoException;

/** Pruebas de cifrado de contraseñas y de tokens (sin contexto de Spring). */
class SeguridadTest {

	private static final String SECRETO = "un-secreto-de-prueba-con-mas-de-32-caracteres";

	@Test
	void contrasenaCifradaCoincideSoloConLaCorrecta() {
		ContrasenaService servicio = new ContrasenaService();
		String guardada = servicio.cifrar("clave123");

		assertTrue(servicio.estaCifrada(guardada));
		assertTrue(servicio.coincide("clave123", guardada));
		assertFalse(servicio.coincide("otra", guardada));
		assertNotEquals(guardada, servicio.cifrar("clave123")); // la sal cambia el resultado
	}

	@Test
	void contrasenaAntiguaEnTextoPlanoSigueFuncionando() {
		ContrasenaService servicio = new ContrasenaService();

		assertFalse(servicio.estaCifrada("admin123"));
		assertTrue(servicio.coincide("admin123", "admin123"));
		assertFalse(servicio.coincide("admin124", "admin123"));
	}

	@Test
	void tokenEmitidoSeValida() throws Exception {
		TokenService tokens = new TokenService(SECRETO, 8);
		SesionUsuario sesion = tokens.validar(tokens.emitir(7L, Rol.COMITE));

		assertEquals(7L, sesion.getId());
		assertEquals(Rol.COMITE, sesion.getRol());
	}

	@Test
	void tokenAlteradoOVencidoSeRechaza() {
		TokenService tokens = new TokenService(SECRETO, 8);
		String token = tokens.emitir(1L, Rol.ADMIN);
		String alterado = (token.charAt(0) == 'X' ? "Y" : "X") + token.substring(1); // cambia el cuerpo firmado

		assertThrows(NoAutenticadoException.class, () -> tokens.validar(alterado));
		assertThrows(NoAutenticadoException.class, () -> tokens.validar("basura"));
		assertThrows(NoAutenticadoException.class, () -> tokens.validar(null));
		assertThrows(NoAutenticadoException.class,
				() -> new TokenService(SECRETO, -1).validar(new TokenService(SECRETO, -1).emitir(1L, Rol.ADMIN)));
	}

	@Test
	void tokenFirmadoConOtroSecretoSeRechaza() {
		String token = new TokenService(SECRETO, 8).emitir(1L, Rol.ADMIN);
		TokenService otro = new TokenService("otro-secreto-distinto-de-mas-de-32-caracteres", 8);

		assertThrows(NoAutenticadoException.class, () -> otro.validar(token));
	}

	@Test
	void rolDesdeTextoIgnoraMayusculasYRechazaDesconocidos() {
		assertEquals(Rol.DIRECCION, Rol.desde(" direccion "));
		assertEquals(null, Rol.desde("PROFESOR"));
		assertEquals(null, Rol.desde(null));
	}
}
