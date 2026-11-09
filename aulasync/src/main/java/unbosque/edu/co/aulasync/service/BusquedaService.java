package unbosque.edu.co.aulasync.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.entity.Curso;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.Estudiante;
import unbosque.edu.co.aulasync.entity.SesionHorario;
import unbosque.edu.co.aulasync.model.Trie;
import unbosque.edu.co.aulasync.repository.CursoRepository;
import unbosque.edu.co.aulasync.repository.DocenteRepository;
import unbosque.edu.co.aulasync.repository.EstudianteRepository;
import unbosque.edu.co.aulasync.repository.SesionHorarioRepository;

/**
 * Mantiene los índices de búsqueda por prefijo ({@link Trie}).
 *
 * <ul>
 * <li>Cursos: código y nombre.</li>
 * <li>Docentes: nombre (de su usuario).</li>
 * <li>Estudiantes: código y nombre.</li>
 * <li>Horario: clave {@code semestre/curso/grupo} de cada sesión guardada
 * (por ejemplo {@code 1/ISIS-1101/G1}), para consultar un semestre, un curso
 * o un grupo por prefijo.</li>
 * </ul>
 *
 * <p>
 * Los índices viven en memoria: se reconstruyen desde la base de datos al
 * arrancar y se actualizan cuando cambian los datos. Solo guardan ids; cada
 * servicio convierte después los resultados a su DTO.
 * </p>
 *
 * @version 1.1
 */
@Service
public class BusquedaService {

	/** Máximo de resultados por búsqueda de autocompletado. */
	public static final int LIMITE_RESULTADOS = 20;

	private final Trie trieCursos = new Trie();
	private final Trie trieDocentes = new Trie();
	private final Trie trieEstudiantes = new Trie();
	private final Trie trieHorario = new Trie();

	private final CursoRepository cursoRepository;
	private final DocenteRepository docenteRepository;
	private final EstudianteRepository estudianteRepository;
	private final SesionHorarioRepository sesionHorarioRepository;

	/**
	 * Crea el servicio con los repositorios que necesita para la carga inicial.
	 *
	 * @param cursoRepository         lectura de cursos.
	 * @param docenteRepository       lectura de docentes.
	 * @param estudianteRepository    lectura de estudiantes.
	 * @param sesionHorarioRepository lectura del horario guardado.
	 */
	public BusquedaService(CursoRepository cursoRepository, DocenteRepository docenteRepository,
			EstudianteRepository estudianteRepository, SesionHorarioRepository sesionHorarioRepository) {
		this.cursoRepository = cursoRepository;
		this.docenteRepository = docenteRepository;
		this.estudianteRepository = estudianteRepository;
		this.sesionHorarioRepository = sesionHorarioRepository;
	}

	/**
	 * Vacía los cuatro índices y los vuelve a llenar desde la base de datos.
	 */
	@Transactional(readOnly = true)
	public void reconstruirIndices() {
		trieCursos.limpiar();
		trieDocentes.limpiar();
		trieEstudiantes.limpiar();
		for (Curso curso : cursoRepository.findAll()) {
			indexarCurso(curso);
		}
		for (Docente docente : docenteRepository.findAll()) {
			indexarDocente(docente);
		}
		for (Estudiante estudiante : estudianteRepository.findAll()) {
			indexarEstudiante(estudiante);
		}
		reconstruirIndiceHorario(sesionHorarioRepository.findAll());
	}

	// ------------------------------------------------------------------
	// Cursos
	// ------------------------------------------------------------------

	/**
	 * Indexa (o reindexa) un curso por código y nombre.
	 *
	 * @param curso curso guardado.
	 */
	public void indexarCurso(Curso curso) {
		trieCursos.insertar(curso.getId(), curso.getCodigo() + " " + curso.getNombre());
	}

	/**
	 * Quita un curso del índice.
	 *
	 * @param id id del curso.
	 */
	public void quitarCurso(Long id) {
		trieCursos.eliminar(id);
	}

	/**
	 * Busca cursos por prefijo de código o de alguna palabra del nombre.
	 *
	 * @param prefijo texto buscado.
	 * @return ids de los cursos encontrados, en orden lexicográfico.
	 */
	public List<Long> buscarCursos(String prefijo) {
		return trieCursos.buscarPorPrefijo(prefijo, LIMITE_RESULTADOS);
	}

	// ------------------------------------------------------------------
	// Docentes
	// ------------------------------------------------------------------

	/**
	 * Indexa (o reindexa) un docente por el nombre de su usuario.
	 *
	 * @param docente docente guardado.
	 */
	public void indexarDocente(Docente docente) {
		trieDocentes.insertar(docente.getId(), docente.getUsuario().getNombre());
	}

	/**
	 * Quita un docente del índice.
	 *
	 * @param id id del docente.
	 */
	public void quitarDocente(Long id) {
		trieDocentes.eliminar(id);
	}

	/**
	 * Busca docentes por prefijo de alguna palabra de su nombre.
	 *
	 * @param prefijo texto buscado.
	 * @return ids de los docentes encontrados, en orden lexicográfico.
	 */
	public List<Long> buscarDocentes(String prefijo) {
		return trieDocentes.buscarPorPrefijo(prefijo, LIMITE_RESULTADOS);
	}

	// ------------------------------------------------------------------
	// Estudiantes
	// ------------------------------------------------------------------

	/**
	 * Indexa (o reindexa) un estudiante por código y nombre.
	 *
	 * @param estudiante estudiante guardado.
	 */
	public void indexarEstudiante(Estudiante estudiante) {
		trieEstudiantes.insertar(estudiante.getId(), estudiante.getCodigo() + " " + estudiante.getNombre());
	}

	/**
	 * Quita un estudiante del índice.
	 *
	 * @param id id del estudiante.
	 */
	public void quitarEstudiante(Long id) {
		trieEstudiantes.eliminar(id);
	}

	/**
	 * Busca estudiantes por prefijo de código o de alguna palabra del nombre.
	 *
	 * @param prefijo texto buscado.
	 * @return ids de los estudiantes encontrados, en orden lexicográfico.
	 */
	public List<Long> buscarEstudiantes(String prefijo) {
		return trieEstudiantes.buscarPorPrefijo(prefijo, LIMITE_RESULTADOS);
	}

	// ------------------------------------------------------------------
	// Horario
	// ------------------------------------------------------------------

	/**
	 * Clave de una sesión en el índice del horario:
	 * {@code semestre/codigoCurso/Gnumero} (por ejemplo {@code 1/ISIS-1101/G1}).
	 *
	 * @param sesion sesión guardada.
	 * @return clave de la sesión.
	 */
	public static String claveSesion(SesionHorario sesion) {
		Curso curso = sesion.getGrupo().getCurso();
		return curso.getSemestre().getNumero() + "/" + curso.getCodigo() + "/G" + sesion.getGrupo().getNumero();
	}

	/**
	 * Vacía el índice del horario y lo llena con las sesiones dadas.
	 *
	 * @param sesiones sesiones del horario guardado.
	 */
	public void reconstruirIndiceHorario(List<SesionHorario> sesiones) {
		trieHorario.limpiar();
		for (SesionHorario sesion : sesiones) {
			trieHorario.insertar(sesion.getId(), claveSesion(sesion));
		}
	}

	/**
	 * Busca las sesiones guardadas cuya clave empieza por el prefijo (por
	 * ejemplo {@code "1/"} para todo primer semestre). No tiene límite de
	 * resultados.
	 *
	 * @param prefijo prefijo de la clave.
	 * @return ids de las sesiones encontradas.
	 */
	public List<Long> buscarSesiones(String prefijo) {
		return trieHorario.buscarPorPrefijo(prefijo, Integer.MAX_VALUE);
	}

	// ------------------------------------------------------------------
	// Información
	// ------------------------------------------------------------------

	/**
	 * @return cantidad de cursos indexados.
	 */
	public int totalCursos() {
		return trieCursos.tamanio();
	}

	/**
	 * @return cantidad de docentes indexados.
	 */
	public int totalDocentes() {
		return trieDocentes.tamanio();
	}

	/**
	 * @return cantidad de estudiantes indexados.
	 */
	public int totalEstudiantes() {
		return trieEstudiantes.tamanio();
	}

	/**
	 * @return cantidad de sesiones del horario indexadas.
	 */
	public int totalSesiones() {
		return trieHorario.tamanio();
	}
}
