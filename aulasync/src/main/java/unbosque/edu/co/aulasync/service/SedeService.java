package unbosque.edu.co.aulasync.service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.FranjaSedeDTO;
import unbosque.edu.co.aulasync.dto.SedeDTO;
import unbosque.edu.co.aulasync.entity.FranjaSede;
import unbosque.edu.co.aulasync.entity.Sede;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.DatoFueraDeRangoException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.FranjaInvalidaException;
import unbosque.edu.co.aulasync.exception.FranjasTraslapadasException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.exception.NombreDuplicadoException;
import unbosque.edu.co.aulasync.exception.SedeEnUsoException;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.SedeRepository;

/**
 * Lógica de negocio de las sedes y sus franjas de clase.
 *
 * <p>
 * Las franjas se reemplazan completas: se validan todas (día hábil, dentro de
 * la jornada institucional, sin traslapes) y solo si todas son válidas se
 * guardan. Una sede con aulas no se puede eliminar. También crea las sedes por
 * defecto (Usaquén y Chía) la primera vez que arranca el sistema.
 * </p>
 *
 * <p>
 * Al actualizar una sede o reemplazar sus franjas se publica
 * {@link CambioDatosMaestros}, porque las sesiones ubicadas pueden quedar
 * fuera de franja o romper la regla de primer semestre.
 * </p>
 *
 * @version 1.2
 */
@Service
public class SedeService {

	/** Máximo de días por semana que se pueden exigir a primer semestre. */
	public static final int MAX_DIAS_PRIMER_SEMESTRE = 6;

	private static final String ENTIDAD = "Sede";

	private static final DayOfWeek[] ENTRE_SEMANA = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };

	private final SedeRepository sedeRepository;
	private final AulaRepository aulaRepository;
	private final ApplicationEventPublisher eventos;

	/**
	 * Crea el servicio.
	 *
	 * @param sedeRepository acceso a la tabla de sedes.
	 * @param aulaRepository consulta de aulas por sede.
	 * @param eventos        publicador de eventos de Spring.
	 */
	public SedeService(SedeRepository sedeRepository, AulaRepository aulaRepository,
			ApplicationEventPublisher eventos) {
		this.sedeRepository = sedeRepository;
		this.aulaRepository = aulaRepository;
		this.eventos = eventos;
	}

	// ------------------------------------------------------------------
	// CRUD
	// ------------------------------------------------------------------

	/**
	 * Lista las sedes en orden alfabético, con sus franjas.
	 *
	 * @return lista de sedes.
	 */
	@Transactional(readOnly = true)
	public List<SedeDTO> listar() {
		List<SedeDTO> resultado = new ArrayList<>();
		for (Sede sede : sedeRepository.findAllByOrderByNombreAsc()) {
			resultado.add(convertirADTO(sede));
		}
		return resultado;
	}

	/**
	 * Busca una sede por id.
	 *
	 * @param id identificador.
	 * @return la sede con sus franjas.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public SedeDTO buscarPorId(Long id) throws EntidadNoEncontradaException {
		return convertirADTO(obtenerEntidad(id));
	}

	/**
	 * Crea una sede sin franjas. No afecta el horario guardado porque una sede
	 * nueva no tiene aulas.
	 *
	 * @param dto nombre, {@code exclusivaPrimerSemestre} y
	 *            {@code diasPrimerSemestre}.
	 * @return la sede creada.
	 * @throws CampoObligatorioException si falta algún dato.
	 * @throws DatoFueraDeRangoException si los días no están entre 0 y 6.
	 * @throws NombreDuplicadoException  si el nombre ya existe.
	 */
	@Transactional
	public SedeDTO crear(SedeDTO dto)
			throws CampoObligatorioException, DatoFueraDeRangoException, NombreDuplicadoException {
		validarDatos(dto);
		String nombre = dto.getNombre().trim();
		LanzadorException.verificarNombreDisponible(sedeRepository.existsByNombreIgnoreCase(nombre), ENTIDAD, nombre);

		Sede sede = new Sede(nombre, dto.getExclusivaPrimerSemestre(), dto.getDiasPrimerSemestre());
		return convertirADTO(sedeRepository.save(sede));
	}

	/**
	 * Actualiza nombre y regla de primer semestre y sincroniza el horario. Las
	 * franjas no cambian.
	 *
	 * @param id  identificador.
	 * @param dto datos nuevos.
	 * @return la sede actualizada.
	 * @throws EntidadNoEncontradaException si no existe.
	 * @throws CampoObligatorioException    si falta algún dato.
	 * @throws DatoFueraDeRangoException    si los días no están entre 0 y 6.
	 * @throws NombreDuplicadoException     si el nombre es de otra sede.
	 */
	@Transactional
	public SedeDTO actualizar(Long id, SedeDTO dto) throws EntidadNoEncontradaException, CampoObligatorioException,
			DatoFueraDeRangoException, NombreDuplicadoException {
		Sede sede = obtenerEntidad(id);
		validarDatos(dto);
		String nombre = dto.getNombre().trim();
		LanzadorException.verificarNombreDisponible(sedeRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id),
				ENTIDAD, nombre);

		sede.setNombre(nombre);
		sede.setExclusivaPrimerSemestre(dto.getExclusivaPrimerSemestre());
		sede.setDiasPrimerSemestre(dto.getDiasPrimerSemestre());
		Sede guardada = sedeRepository.saveAndFlush(sede);
		eventos.publishEvent(new CambioDatosMaestros("Sede " + id + " actualizada"));
		return convertirADTO(guardada);
	}

	/**
	 * Reemplaza todas las franjas de clase de una sede y sincroniza el horario.
	 *
	 * @param id   identificador de la sede.
	 * @param dtos franjas nuevas ({@code dia}, {@code horaInicio},
	 *             {@code horaFin}); vacía para dejar la sede sin clases.
	 * @return la sede con sus franjas nuevas.
	 * @throws EntidadNoEncontradaException si la sede no existe.
	 * @throws CampoObligatorioException    si falta la lista o algún dato.
	 * @throws FranjaInvalidaException      si una franja está fuera de la
	 *                                      jornada o cae en domingo.
	 * @throws FranjasTraslapadasException  si dos franjas del mismo día se
	 *                                      cruzan.
	 */
	@Transactional
	public SedeDTO reemplazarFranjas(Long id, List<FranjaSedeDTO> dtos) throws EntidadNoEncontradaException,
			CampoObligatorioException, FranjaInvalidaException, FranjasTraslapadasException {
		Sede sede = obtenerEntidad(id);
		LanzadorException.verificarObjetoObligatorio(dtos, "franjas");

		List<FranjaSede> nuevas = new ArrayList<>();
		for (FranjaSedeDTO dto : dtos) {
			LanzadorException.verificarObjetoObligatorio(dto, "franjas");
			LanzadorException.verificarObjetoObligatorio(dto.getDia(), "dia");
			LanzadorException.verificarObjetoObligatorio(dto.getHoraInicio(), "horaInicio");
			LanzadorException.verificarObjetoObligatorio(dto.getHoraFin(), "horaFin");
			LanzadorException.verificarFranjaEnJornada(dto.getDia(), dto.getHoraInicio(), dto.getHoraFin());
			nuevas.add(new FranjaSede(sede, dto.getDia(), dto.getHoraInicio(), dto.getHoraFin()));
		}
		ordenar(nuevas);
		verificarSinTraslapes(nuevas);

		sede.getFranjas().clear();
		sede.getFranjas().addAll(nuevas);
		Sede guardada = sedeRepository.saveAndFlush(sede);
		eventos.publishEvent(new CambioDatosMaestros("Franjas de la sede " + id + " reemplazadas"));
		return convertirADTO(guardada);
	}

	/**
	 * Elimina una sede sin aulas, junto con sus franjas. No afecta el horario
	 * guardado porque una sede sin aulas no tiene sesiones.
	 *
	 * @param id identificador.
	 * @throws EntidadNoEncontradaException si no existe.
	 * @throws SedeEnUsoException           si la sede tiene aulas.
	 */
	@Transactional
	public void eliminar(Long id) throws EntidadNoEncontradaException, SedeEnUsoException {
		Sede sede = obtenerEntidad(id);
		LanzadorException.verificarSedeSinAulas(aulaRepository.existsBySedeId(id), sede.getNombre());
		sedeRepository.delete(sede);
	}

	// ------------------------------------------------------------------
	// Arranque y soporte para otros servicios
	// ------------------------------------------------------------------

	/**
	 * Crea Usaquén y Chía con sus franjas si todavía no hay ninguna sede.
	 *
	 * @return {@code true} si se crearon; {@code false} si ya había sedes.
	 */
	@Transactional
	public boolean crearSedesPorDefectoSiNoHay() {
		if (sedeRepository.count() > 0) {
			return false;
		}

		Sede usaquen = new Sede("Usaquén", false, 0);
		for (DayOfWeek dia : ENTRE_SEMANA) {
			usaquen.getFranjas().add(new FranjaSede(usaquen, dia, 7, 13));
			usaquen.getFranjas().add(new FranjaSede(usaquen, dia, 14, 22));
		}
		usaquen.getFranjas().add(new FranjaSede(usaquen, DayOfWeek.SATURDAY, 7, 13));
		sedeRepository.save(usaquen);

		Sede chia = new Sede("Chía", true, 2);
		for (DayOfWeek dia : ENTRE_SEMANA) {
			chia.getFranjas().add(new FranjaSede(chia, dia, 8, 12));
			chia.getFranjas().add(new FranjaSede(chia, dia, 13, 15));
		}
		sedeRepository.save(chia);
		return true;
	}

	/**
	 * Busca la entidad por id. La usan el servicio de aulas y el motor.
	 *
	 * @param id identificador.
	 * @return la entidad encontrada.
	 * @throws EntidadNoEncontradaException si no existe.
	 */
	@Transactional(readOnly = true)
	public Sede obtenerEntidad(Long id) throws EntidadNoEncontradaException {
		Sede sede = sedeRepository.findById(id).orElse(null);
		LanzadorException.verificarEntidadExiste(sede, ENTIDAD, id);
		return sede;
	}

	/**
	 * Convierte una entidad en DTO, con las franjas ordenadas de lunes a sábado
	 * y por hora.
	 *
	 * @param sede entidad a convertir.
	 * @return DTO equivalente.
	 */
	public SedeDTO convertirADTO(Sede sede) {
		List<FranjaSede> ordenadas = new ArrayList<>(sede.getFranjas());
		ordenar(ordenadas);
		List<FranjaSedeDTO> franjas = new ArrayList<>();
		for (FranjaSede franja : ordenadas) {
			franjas.add(new FranjaSedeDTO(franja.getId(), sede.getId(), franja.getDia(), franja.getHoraInicio(),
					franja.getHoraFin()));
		}
		return new SedeDTO(sede.getId(), sede.getNombre(), sede.isExclusivaPrimerSemestre(),
				sede.getDiasPrimerSemestre(), franjas);
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private void validarDatos(SedeDTO dto) throws CampoObligatorioException, DatoFueraDeRangoException {
		LanzadorException.verificarCampoObligatorio(dto.getNombre(), "nombre");
		LanzadorException.verificarObjetoObligatorio(dto.getExclusivaPrimerSemestre(), "exclusivaPrimerSemestre");
		LanzadorException.verificarObjetoObligatorio(dto.getDiasPrimerSemestre(), "diasPrimerSemestre");
		LanzadorException.verificarRango(dto.getDiasPrimerSemestre(), "diasPrimerSemestre", 0,
				MAX_DIAS_PRIMER_SEMESTRE);
	}

	private void verificarSinTraslapes(List<FranjaSede> ordenadas) throws FranjasTraslapadasException {
		for (int i = 1; i < ordenadas.size(); i++) {
			FranjaSede anterior = ordenadas.get(i - 1);
			FranjaSede actual = ordenadas.get(i);
			boolean mismoDia = anterior.getDia() == actual.getDia();
			boolean seCruzan = mismoDia && anterior.getHoraFin() > actual.getHoraInicio();
			LanzadorException.verificarSinTraslape(seCruzan, describir(anterior), describir(actual));
		}
	}

	private String describir(FranjaSede franja) {
		return franja.getDia() + " " + franja.getHoraInicio() + "-" + franja.getHoraFin();
	}

	private void ordenar(List<FranjaSede> franjas) {
		Collections.sort(franjas, new Comparator<FranjaSede>() {
			@Override
			public int compare(FranjaSede a, FranjaSede b) {
				int porDia = a.getDia().compareTo(b.getDia());
				if (porDia != 0) {
					return porDia;
				}
				return Integer.compare(a.getHoraInicio(), b.getHoraInicio());
			}
		});
	}
}
