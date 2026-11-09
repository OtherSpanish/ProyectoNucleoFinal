package unbosque.edu.co.aulasync.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import unbosque.edu.co.aulasync.service.BusquedaService;

/**
 * Construye los índices de búsqueda por prefijo (Tries) al arrancar la
 * aplicación: cursos, docentes, estudiantes y horario guardado.
 *
 * @version 1.1
 */
@Component
public class IndicesBusqueda implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(IndicesBusqueda.class);

	private final BusquedaService busquedaService;

	/**
	 * Crea el inicializador.
	 *
	 * @param busquedaService servicio de búsqueda.
	 */
	public IndicesBusqueda(BusquedaService busquedaService) {
		this.busquedaService = busquedaService;
	}

	/**
	 * Reconstruye los índices y deja en el log cuántos registros quedaron.
	 *
	 * @param args argumentos de arranque (no se usan).
	 */
	@Override
	public void run(ApplicationArguments args) {
		busquedaService.reconstruirIndices();
		LOG.info("Índices de búsqueda listos: {} cursos, {} docentes, {} estudiantes, {} sesiones de horario.",
				busquedaService.totalCursos(), busquedaService.totalDocentes(), busquedaService.totalEstudiantes(),
				busquedaService.totalSesiones());
	}
}
