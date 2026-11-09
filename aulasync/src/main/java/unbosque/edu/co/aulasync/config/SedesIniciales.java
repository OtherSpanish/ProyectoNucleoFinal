package unbosque.edu.co.aulasync.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import unbosque.edu.co.aulasync.service.SedeService;

/**
 * Crea las sedes por defecto (Usaquén y Chía, con sus franjas) al arrancar la
 * aplicación, solo si todavía no existe ninguna sede.
 *
 * <p>
 * Después del primer arranque no hace nada: los cambios del administrador se
 * conservan.
 * </p>
 *
 * @version 1.0
 */
@Component
public class SedesIniciales implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(SedesIniciales.class);

	private final SedeService sedeService;

	/**
	 * Crea el inicializador.
	 *
	 * @param sedeService servicio de sedes.
	 */
	public SedesIniciales(SedeService sedeService) {
		this.sedeService = sedeService;
	}

	/**
	 * Intenta crear las sedes por defecto y deja el resultado en el log.
	 *
	 * @param args argumentos de arranque (no se usan).
	 */
	@Override
	public void run(ApplicationArguments args) {
		if (sedeService.crearSedesPorDefectoSiNoHay()) {
			LOG.info("Sedes por defecto creadas: Usaquén y Chía.");
		} else {
			LOG.info("Ya existen sedes: no se crean las de por defecto.");
		}
	}
}
