package unbosque.edu.co.aulasync.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import unbosque.edu.co.aulasync.service.UsuarioService;

/**
 * Crea el primer administrador al arrancar la aplicación, a partir de
 * {@code aulasync.admin.correo} y {@code aulasync.admin.password}.
 *
 * <p>
 * Solo actúa si no existe ningún administrador, así que es seguro dejarlo
 * activo: en los siguientes arranques no hace nada.
 * </p>
 *
 * @version 1.0
 */
@Component
public class AdminInicial implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(AdminInicial.class);

	private final UsuarioService usuarioService;
	private final String correo;
	private final String password;

	/**
	 * Crea el inicializador con el servicio y los datos configurados.
	 *
	 * @param usuarioService servicio de usuarios.
	 * @param correo         correo del primer administrador.
	 * @param password       contraseña del primer administrador.
	 */
	public AdminInicial(UsuarioService usuarioService, @Value("${aulasync.admin.correo:}") String correo,
			@Value("${aulasync.admin.password:}") String password) {
		this.usuarioService = usuarioService;
		this.correo = correo;
		this.password = password;
	}

	/**
	 * Intenta crear el administrador inicial y deja el resultado en el log.
	 *
	 * @param args argumentos de arranque (no se usan).
	 */
	@Override
	public void run(ApplicationArguments args) {
		if (correo == null || correo.isBlank()) {
			LOG.warn("No se definió aulasync.admin.correo: no se crea administrador inicial.");
			return;
		}
		boolean creado = usuarioService.crearAdminInicialSiNoExiste("Administrador", correo, password);
		if (creado) {
			LOG.info("Administrador inicial creado: {}", correo);
		} else {
			LOG.info("Ya existe un administrador o faltan datos: no se crea otro.");
		}
	}
}
