package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.FranjaDocenteDTO;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.FranjaDocente;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.FranjaInvalidaException;
import unbosque.edu.co.aulasync.exception.FranjasTraslapadasException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.model.DisponibilidadDocente;
import unbosque.edu.co.aulasync.repository.FranjaDocenteRepository;

/**
 * Lógica de negocio de la disponibilidad horaria de los docentes.
 *
 * <p>
 * La semana se reemplaza completa: se validan todas las franjas recibidas
 * (día hábil, dentro de la jornada, sin traslapes) y, si todas son válidas,
 * se borran las anteriores y se guardan las nuevas en una sola transacción.
 * Después se publica {@link CambioDatosMaestros}, porque el docente puede
 * dejar de estar disponible en horas donde ya tiene sesiones ubicadas.
 * </p>
 *
 * @version 1.1
 */
@Service
public class DisponibilidadService {

	private final FranjaDocenteRepository franjaDocenteRepository;
	private final DocenteService docenteService;
	private final ApplicationEventPublisher eventos;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param franjaDocenteRepository acceso a la tabla de franjas.
	 * @param docenteService          búsqueda de docentes.
	 * @param eventos                 publicador de eventos de Spring.
	 */
	public DisponibilidadService(FranjaDocenteRepository franjaDocenteRepository, DocenteService docenteService,
			ApplicationEventPublisher eventos) {
		this.franjaDocenteRepository = franjaDocenteRepository;
		this.docenteService = docenteService;
		this.eventos = eventos;
	}

	/**
	 * Consulta la disponibilidad semanal de un docente.
	 *
	 * @param docenteId id del docente.
	 * @return disponibilidad con franjas ordenadas y totales.
	 * @throws EntidadNoEncontradaException si el docente no existe.
	 */
	@Transactional(readOnly = true)
	public DisponibilidadDocente obtener(Long docenteId) throws EntidadNoEncontradaException {
		Docente docente = docenteService.obtenerEntidad(docenteId);
		List<FranjaDocente> franjas = listarEntidades(docenteId);
		return construir(docente, franjas);
	}

	/**
	 * Reemplaza la disponibilidad semanal completa de un docente y sincroniza
	 * el horario.
	 *
	 * @param docenteId id del docente.
	 * @param dtos      franjas nuevas; una lista vacía deja al docente sin
	 *                  disponibilidad.
	 * @return la disponibilidad guardada.
	 * @throws EntidadNoEncontradaException si el docente no existe.
	 * @throws CampoObligatorioException    si falta la lista o algún dato de una
	 *                                      franja.
	 * @throws FranjaInvalidaException      si una franja cae en domingo o fuera
	 *                                      de la jornada.
	 * @throws FranjasTraslapadasException  si dos franjas del mismo día se
	 *                                      cruzan.
	 */
	@Transactional
	public DisponibilidadDocente reemplazar(Long docenteId, List<FranjaDocenteDTO> dtos)
			throws EntidadNoEncontradaException, CampoObligatorioException, FranjaInvalidaException,
			FranjasTraslapadasException {
		Docente docente = docenteService.obtenerEntidad(docenteId);
		LanzadorException.verificarObjetoObligatorio(dtos, "franjas");

		List<FranjaDocente> nuevas = new ArrayList<>();
		for (FranjaDocenteDTO dto : dtos) {
			LanzadorException.verificarObjetoObligatorio(dto, "franjas");
			LanzadorException.verificarObjetoObligatorio(dto.getDia(), "dia");
			LanzadorException.verificarObjetoObligatorio(dto.getHoraInicio(), "horaInicio");
			LanzadorException.verificarObjetoObligatorio(dto.getHoraFin(), "horaFin");
			LanzadorException.verificarFranjaEnJornada(dto.getDia(), dto.getHoraInicio(), dto.getHoraFin());
			nuevas.add(new FranjaDocente(docente, dto.getDia(), dto.getHoraInicio(), dto.getHoraFin()));
		}

		ordenar(nuevas);
		verificarSinTraslapes(nuevas);

		franjaDocenteRepository.deleteByDocenteId(docenteId);
		franjaDocenteRepository.flush();
		List<FranjaDocente> guardadas = franjaDocenteRepository.saveAll(nuevas);
		franjaDocenteRepository.flush();
		eventos.publishEvent(new CambioDatosMaestros("Disponibilidad del docente " + docenteId + " reemplazada"));
		ordenar(guardadas);
		return construir(docente, guardadas);
	}

	/**
	 * Lista las franjas de un docente ordenadas de lunes a sábado y por hora.
	 * La usan el motor de horarios y las validaciones de grupos.
	 *
	 * @param docenteId id del docente.
	 * @return franjas ordenadas.
	 */
	@Transactional(readOnly = true)
	public List<FranjaDocente> listarEntidades(Long docenteId) {
		List<FranjaDocente> franjas = new ArrayList<>(
				franjaDocenteRepository.findByDocenteIdOrderByDiaAscHoraInicioAsc(docenteId));
		ordenar(franjas);
		return franjas;
	}

	/**
	 * Convierte una franja en DTO.
	 *
	 * @param franja entidad a convertir.
	 * @return DTO equivalente.
	 */
	public FranjaDocenteDTO convertirADTO(FranjaDocente franja) {
		return new FranjaDocenteDTO(franja.getId(), franja.getDocente().getId(), franja.getDia(),
				franja.getHoraInicio(), franja.getHoraFin());
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private DisponibilidadDocente construir(Docente docente, List<FranjaDocente> franjas) {
		List<FranjaDocenteDTO> dtos = new ArrayList<>();
		int horas = 0;
		for (FranjaDocente franja : franjas) {
			dtos.add(convertirADTO(franja));
			horas = horas + (franja.getHoraFin() - franja.getHoraInicio());
		}
		return new DisponibilidadDocente(docente.getId(), docente.getUsuario().getNombre(),
				docente.getDedicacion(), docente.getDedicacion().getHorasMaximas(), horas, dtos);
	}

	private void verificarSinTraslapes(List<FranjaDocente> ordenadas) throws FranjasTraslapadasException {
		for (int i = 1; i < ordenadas.size(); i++) {
			FranjaDocente anterior = ordenadas.get(i - 1);
			FranjaDocente actual = ordenadas.get(i);
			boolean mismoDia = anterior.getDia() == actual.getDia();
			boolean seCruzan = mismoDia && anterior.getHoraFin() > actual.getHoraInicio();
			LanzadorException.verificarSinTraslape(seCruzan, describir(anterior), describir(actual));
		}
	}

	private String describir(FranjaDocente franja) {
		return franja.getDia() + " " + franja.getHoraInicio() + "-" + franja.getHoraFin();
	}

	private void ordenar(List<FranjaDocente> franjas) {
		Collections.sort(franjas, new Comparator<FranjaDocente>() {
			@Override
			public int compare(FranjaDocente a, FranjaDocente b) {
				int porDia = a.getDia().compareTo(b.getDia());
				if (porDia != 0) {
					return porDia;
				}
				return Integer.compare(a.getHoraInicio(), b.getHoraInicio());
			}
		});
	}
}
