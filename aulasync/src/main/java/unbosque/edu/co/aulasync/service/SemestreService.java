package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.SemestreDTO;
import unbosque.edu.co.aulasync.entity.Semestre;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.SemestreDuplicadoException;
import unbosque.edu.co.aulasync.exception.SemestreEnUsoException;
import unbosque.edu.co.aulasync.repository.CursoRepository;
import unbosque.edu.co.aulasync.repository.SemestreRepository;

/**
 * Lógica de negocio de los semestres del plan de estudios.
 *
 * <p>
 * La carrera tiene {@value #MAX_SEMESTRE} semestres. Valida número único entre
 * {@value #MIN_SEMESTRE} y {@value #MAX_SEMESTRE} y nombre obligatorio, impide
 * eliminar semestres con cursos y crea los {@value #MAX_SEMESTRE} semestres la
 * primera vez que arranca el sistema.
 * </p>
 *
 * <p>
 * Los estudiantes no tienen semestre propio: pueden ver materias de semestres
 * distintos a la vez. El semestre pertenece al curso.
 * </p>
 *
 * @version 1.2
 */
@Service
public class SemestreService {

	/** Número mínimo de semestre. */
	public static final int MIN_SEMESTRE = 1;

	/** Número máximo de semestre: la carrera tiene 9. */
	public static final int MAX_SEMESTRE = 9;

	private static final String ENTIDAD = "Semestre";

	private static final String[] ORDINALES = { "Primer", "Segundo", "Tercer", "Cuarto", "Quinto", "Sexto",
			"Séptimo", "Octavo", "Noveno" };

	private final SemestreRepository semestreRepository;
	private final CursoRepository cursoRepository;

	/**
	 * Crea el servicio.
	 *
	 * @param semestreRepository acceso a la tabla de semestres.
	 * @param cursoRepository    consulta de cursos por semestre.
	 */
	public SemestreService(SemestreRepository semestreRepository, CursoRepository cursoRepository) {
		this.semestreRepository = semestreRepository;
		this.cursoRepository = cursoRepository;
	}

	// ------------------------------------------------------------------
	// CRUD
	// ------------------------------------------------------------------

	/**
	 * Lista los semestres en orden numérico.
	 *
	 * @return lista de semestres.
	 */
	@Transactional(readOnly = true)
	public List<SemestreDTO> listar() {
		List<SemestreDTO> resultado = new ArrayList<>();
		for (Semestre semestre : semestreRepository.findAllByOrderByNumeroAsc()) {
			resultado.add(convertirADTO(semestre));
		}
		return resultado;
	}

	/**
	 * Busca un semestre por id.
	 *
	 * @param id identificador.
	 * @return el semestre.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public SemestreDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea un semestre.
	 *
	 * @param dto número y nombre.
	 * @return el semestre creado.
	 * @throws CampoObligatorioException   si falta el número o el nombre.
	 * @throws DatoFueraDeRangoException   si el número no está entre 1 y 9.
	 * @throws SemestreDuplicadoException si el número ya existe.
	 */
	@Transactional
	public SemestreDTO crear(SemestreDTO dto)
			throws CampoObligatorioException, DatoFueraDeRangoException, SemestreDuplicadoException {
		validarDatos(dto);
		LanzadorException.verificarSemestreDisponible(semestreRepository.existsByNumero(dto.getNumero()),
				dto.getNumero());

		Semestre semestre = new Semestre(dto.getNumero(), dto.getNombre().trim());
		return convertirADTO(semestreRepository.save(semestre));
	}

	/**
	 * Actualiza número y nombre de un semestre.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return el semestre actualizado.
	 * @throws EntidadNoEncontradaException si no existe.
	 * @throws CampoObligatorioException    si falta el número o el nombre.
	 * @throws DatoFueraDeRangoException    si el número no está entre 1 y 9.
	 * @throws SemestreDuplicadoException  si el número es de otro semestre.
	 */
	@Transactional
	public SemestreDTO actualizar(Long id, SemestreDTO dto) throws EntidadNoEncontradaException,
			CampoObligatorioException, DatoFueraDeRangoException, SemestreDuplicadoException {
		Semestre semestre = obtenerEntidad(id);
		validarDatos(dto);
		LanzadorException.verificarSemestreDisponible(
				semestreRepository.existsByNumeroAndIdNot(dto.getNumero(), id), dto.getNumero());

		semestre.setNumero(dto.getNumero());
		semestre.setNombre(dto.getNombre().trim());
		return convertirADTO(semestreRepository.save(semestre));
	}

	/**
	 * Elimina un semestre que no tenga cursos.
	 *
	 * @param id identificador.
	 * @throws EntidadNoEncontradaException si no existe.
	 * @throws SemestreEnUsoException       si el semestre tiene cursos.
	 */
	@Transactional
	public void eliminar(Long id) throws EntidadNoEncontradaException, SemestreEnUsoException {
		Semestre semestre = obtenerEntidad(id);
		LanzadorException.verificarSemestreSinUso(cursoRepository.existsBySemestreId(id), semestre.getNumero());
		semestreRepository.delete(semestre);
	}

	// ------------------------------------------------------------------
	// Arranque y soporte para otros servicios
	// ------------------------------------------------------------------

	/**
	 * Crea los semestres 1 a {@value #MAX_SEMESTRE} si todavía no hay ninguno.
	 *
	 * @return {@code true} si se crearon; {@code false} si ya había semestres.
	 */
	@Transactional
	public boolean crearSemestresPorDefectoSiNoHay() {
		if (semestreRepository.count() > 0) {
			return false;
		}
		for (int i = 0; i < MAX_SEMESTRE; i++) {
			semestreRepository.save(new Semestre(i + 1, ORDINALES[i] + " semestre"));
		}
		return true;
	}

	/**
	 * Busca la entidad por id. La usa el servicio de cursos.
	 *
	 * @param id identificador.
	 * @return la entidad encontrada.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public Semestre obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Semestre semestre = semestreRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(semestre, ENTIDAD, id);
		return semestre;
	}

	/**
	 * Convierte una entidad en DTO.
	 *
	 * @param semestre entidad a convertir.
	 * @return DTO equivalente.
	 */
	public SemestreDTO convertirADTO(Semestre semestre) {
		return new SemestreDTO(semestre.getId(), semestre.getNumero(), semestre.getNombre());
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private void validarDatos(SemestreDTO dto) throws CampoObligatorioException, DatoFueraDeRangoException {
		LanzadorException.verificarObjetoObligatorio(dto.getNumero(), "numero");
		LanzadorException.verificarRango(dto.getNumero(), "numero", MIN_SEMESTRE, MAX_SEMESTRE);
		LanzadorException.verificarCampoObligatorio(dto.getNombre(), "nombre");
	}
}
