package unbosque.edu.co.aulasync.exception;

import unbosque.edu.co.aulasync.motor.TipoAula;

/**
 * Clase utilitaria que centraliza la validación de los datos del sistema y
 * lanza las excepciones personalizadas correspondientes cuando los datos no
 * cumplen los criterios requeridos.
 *
 * <p>
 * Es el único lugar donde se instancian las excepciones de negocio: los
 * servicios llaman a estos métodos en lugar de usar {@code throw new}. Todos
 * los métodos son estáticos y no requieren instanciar la clase.
 * </p>
 *
 * @version 1.0
 */
public class LanzadorException {

	/** Mínimo de sesiones por semana de un curso (RD8). */
	public static final int MIN_SESIONES_SEMANA = 1;

	/** Máximo de sesiones por semana de un curso (RD8). */
	public static final int MAX_SESIONES_SEMANA = 4;

	/** Duración estándar de una sesión, en horas (RD8). */
	public static final int DURACION_ESTANDAR = 2;

	/** Duración extendida de una sesión, en horas (RD8). */
	public static final int DURACION_EXTENDIDA = 4;

	private LanzadorException() {
		// Clase utilitaria: no se instancia.
	}

	/**
	 * Verifica que el nombre de un curso sea válido. Un nombre válido no puede ser
	 * nulo ni estar vacío o compuesto solo por espacios.
	 *
	 * @param nombre nombre del curso a validar.
	 * @throws NombreCursoException si el nombre es nulo o está vacío.
	 */
	public static void verificarNombreCurso(String nombre) throws NombreCursoException {
		if (nombre == null || nombre.isBlank()) {
			throw new NombreCursoException();
		}
	}

	/**
	 * Verifica que las sesiones por semana de un curso estén dentro del rango
	 * permitido, de {@value #MIN_SESIONES_SEMANA} a {@value #MAX_SESIONES_SEMANA}.
	 *
	 * @param sesiones cantidad de sesiones semanales a validar.
	 * @throws SesionesSemanaException si el valor es nulo, menor que el mínimo o
	 *                                 mayor que el máximo.
	 */
	public static void verificarSesionesSemana(Integer sesiones) throws SesionesSemanaException {
		if (sesiones == null || sesiones < MIN_SESIONES_SEMANA || sesiones > MAX_SESIONES_SEMANA) {
			throw new SesionesSemanaException(MIN_SESIONES_SEMANA, MAX_SESIONES_SEMANA);
		}
	}

	/**
	 * Verifica que la duración de una sesión sea una de las permitidas: 2 horas
	 * (estándar) o 4 horas (cursos específicos).
	 *
	 * @param horas duración de la sesión, en horas.
	 * @throws DuracionSesionException si la duración es nula o distinta de 2 y 4.
	 */
	public static void verificarDuracionSesion(Integer horas) throws DuracionSesionException {
		if (horas == null || (horas != DURACION_ESTANDAR && horas != DURACION_EXTENDIDA)) {
			throw new DuracionSesionException(DURACION_ESTANDAR, DURACION_EXTENDIDA);
		}
	}

	/**
	 * Verifica que se haya indicado el tipo de aula que requiere un curso.
	 *
	 * @param tipo tipo de aula a validar.
	 * @throws TipoAulaException si el tipo es nulo.
	 */
	public static void verificarTipoAula(TipoAula tipo) throws TipoAulaException {
		if (tipo == null) {
			throw new TipoAulaException();
		}
	}

	/**
	 * Verifica que un registro buscado exista.
	 *
	 * @param entidad       registro obtenido de la búsqueda, o {@code null} si no
	 *                      se encontró.
	 * @param nombreEntidad nombre legible de la entidad, usado en el mensaje (por
	 *                      ejemplo "Curso").
	 * @param id            identificador que se buscó.
	 * @throws EntidadNoEncontradaException si {@code entidad} es nula.
	 */
	public static void verificarEntidadExiste(Object entidad, String nombreEntidad, Long id)
			throws EntidadNoEncontradaException {
		if (entidad == null) {
			throw new EntidadNoEncontradaException(nombreEntidad, id);
		}
	}

	/**
	 * Verifica que un curso pueda eliminarse, es decir, que no tenga secciones
	 * asociadas.
	 *
	 * @param tieneSecciones {@code true} si el curso tiene al menos una sección.
	 * @throws CursoConSeccionesException si el curso tiene secciones asociadas.
	 */
	public static void verificarCursoSinSecciones(boolean tieneSecciones) throws CursoConSeccionesException {
		if (tieneSecciones) {
			throw new CursoConSeccionesException();
		}
	}
}