package unbosque.edu.co.aulasync.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import unbosque.edu.co.aulasync.service.SemestreService;

/**
 * Crea los semestres 1 a {@value SemestreService#MAX_SEMESTRE} al arrancar la
 * aplicación, solo si todavía no existe ninguno.
 *
 * <p>
 * Después del primer arranque no hace nada: los cambios del administrador se
 * conservan.
 * </p>
 *
 * @version 1.1
 */
@Component
public class SemestresIniciales implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(SemestresIniciales.class);

	private final SemestreService semestreService;

	/**
	 * Crea el inicializador.
	 *
	 * @param semestreService servicio de semestres.
	 */
	public SemestresIniciales(SemestreService semestreService) {
		this.semestreService = semestreService;
	}

	/**
	 * Intenta crear los semestres por defecto y deja el resultado en el log.
	 *
	 * @param args argumentos de arranque (no se usan).
	 */
	@Override
	public void run(ApplicationArguments args) {
		if (semestreService.crearSemestresPorDefectoSiNoHay()) {
			LOG.info("Semestres por defecto creados: 1 a {}.", SemestreService.MAX_SEMESTRE);
		} else {
			LOG.info("Ya existen semestres: no se crean los de por defecto.");
		}
	}
}
