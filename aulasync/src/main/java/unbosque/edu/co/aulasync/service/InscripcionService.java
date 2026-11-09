package unbosque.edu.co.aulasync.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.dto.EstudianteDTO;
import unbosque.edu.co.aulasync.entity.Estudiante;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.exception.CampoObligatorioException;
import unbosque.edu.co.aulasync.exception.EntidadNoEncontradaException;
import unbosque.edu.co.aulasync.exception.EstudianteEnOtroGrupoException;
import unbosque.edu.co.aulasync.exception.GrupoLlenoException;
import unbosque.edu.co.aulasync.exception.LanzadorException;
import unbosque.edu.co.aulasync.model.CambioDatosMaestros;
import unbosque.edu.co.aulasync.model.InscritosGrupo;
import unbosque.edu.co.aulasync.repository.GrupoRepository;

/**
 * Lógica de negocio de la inscripción de estudiantes en grupos.
 *
 * <p>
 * Un estudiante no puede estar en dos grupos de la misma materia y un grupo
 * admite como máximo {@value GrupoService#MAX_INSCRITOS} inscritos. Las
 * respuestas incluyen cuántos faltan para abrir el grupo (mínimo
 * {@value GrupoService#MIN_INSCRITOS}) y cuántos cupos quedan. Cada cambio
 * publica {@link CambioDatosMaestros}, porque el grupo puede abrirse o
 * cerrarse y la capacidad que necesita en el aula cambia.
 * </p>
 *
 * @version 1.2
 */
@Service
public class InscripcionService {

	private final GrupoRepository grupoRepository;
	private final GrupoService grupoService;
	private final EstudianteService estudianteService;
	private final ApplicationEventPublisher eventos;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param grupoRepository   acceso a grupos e inscripciones.
	 * @param grupoService      búsqueda y conversión de grupos.
	 * @param estudianteService búsqueda y conversión de estudiantes.
	 * @param eventos           publicador de eventos de Spring.
	 */
	public InscripcionService(GrupoRepository grupoRepository, GrupoService grupoService,
			EstudianteService estudianteService, ApplicationEventPublisher eventos) {
		this.grupoRepository = grupoRepository;
		this.grupoService = grupoService;
		this.estudianteService = estudianteService;
		this.eventos = eventos;
	}

	/**
	 * Consulta los inscritos de un grupo.
	 *
	 * @param grupoId id del grupo.
	 * @return grupo, inscritos en orden alfabético, faltantes y cupos.
	 * @throws EntidadNoEncontradaException si el grupo no existe.
	 */
	@Transactional(readOnly = true)
	public InscritosGrupo obtener(Long grupoId) throws EntidadNoEncontradaException {
		return construir(grupoService.obtenerEntidad(grupoId));
	}

	/**
	 * Reemplaza la lista completa de inscritos de un grupo. Los ids repetidos se
	 * ignoran; una lista vacía deja el grupo sin inscritos.
	 *
	 * @param grupoId id del grupo.
	 * @param dtos    estudiantes a inscribir (basta con su {@code id}).
	 * @return la lista guardada.
	 * @throws EntidadNoEncontradaException   si el grupo o algún estudiante no
	 *                                        existen.
	 * @throws CampoObligatorioException      si falta la lista o algún id.
	 * @throws GrupoLlenoException            si la lista supera el máximo de
	 *                                        inscritos.
	 * @throws EstudianteEnOtroGrupoException si algún estudiante ya está en otro
	 *                                        grupo de la misma materia.
	 */
	@Transactional
	public InscritosGrupo reemplazar(Long grupoId, List<EstudianteDTO> dtos) throws EntidadNoEncontradaException,
			CampoObligatorioException, GrupoLlenoException, EstudianteEnOtroGrupoException {
		Grupo grupo = grupoService.obtenerEntidad(grupoId);
		LanzadorException.verificarObjetoObligatorio(dtos, "estudiantes");

		Set<Long> ids = new HashSet<>();
		for (EstudianteDTO dto : dtos) {
			LanzadorException.verificarObjetoObligatorio(dto, "estudiantes");
			LanzadorException.verificarObjetoObligatorio(dto.getId(), "estudiantes.id");
			ids.add(dto.getId());
		}
		LanzadorException.verificarCupoGrupo(ids.size(), GrupoService.MAX_INSCRITOS, describir(grupo));

		List<Estudiante> nuevos = new ArrayList<>();
		Set<Long> vistos = new HashSet<>();
		for (EstudianteDTO dto : dtos) {
			boolean esNuevo = vistos.add(dto.getId());
			if (esNuevo) {
				Estudiante estudiante = estudianteService.obtenerEntidad(dto.getId());
				verificarSinOtroGrupo(grupo, estudiante);
				nuevos.add(estudiante);
			}
		}

		grupo.getEstudiantes().clear();
		grupo.getEstudiantes().addAll(nuevos);
		Grupo guardado = grupoRepository.save(grupo);
		eventos.publishEvent(new CambioDatosMaestros("Inscritos del grupo " + grupoId + " reemplazados"));
		return construir(guardado);
	}

	/**
	 * Inscribe a un estudiante en un grupo. Si ya estaba inscrito, no hace nada.
	 *
	 * @param grupoId      id del grupo.
	 * @param estudianteId id del estudiante.
	 * @return la lista actualizada.
	 * @throws EntidadNoEncontradaException   si el grupo o el estudiante no
	 *                                        existen.
	 * @throws GrupoLlenoException            si el grupo ya tiene el máximo de
	 *                                        inscritos.
	 * @throws EstudianteEnOtroGrupoException si ya está en otro grupo de la
	 *                                        misma materia.
	 */
	@Transactional
	public InscritosGrupo agregar(Long grupoId, Long estudianteId)
			throws EntidadNoEncontradaException, GrupoLlenoException, EstudianteEnOtroGrupoException {
		Grupo grupo = grupoService.obtenerEntidad(grupoId);
		Estudiante estudiante = estudianteService.obtenerEntidad(estudianteId);
		if (!estaInscrito(grupo, estudianteId)) {
			LanzadorException.verificarCupoGrupo(grupo.getEstudiantes().size() + 1, GrupoService.MAX_INSCRITOS,
					describir(grupo));
			verificarSinOtroGrupo(grupo, estudiante);
			grupo.getEstudiantes().add(estudiante);
			grupo = grupoRepository.save(grupo);
			eventos.publishEvent(new CambioDatosMaestros("Estudiante inscrito en el grupo " + grupoId));
		}
		return construir(grupo);
	}

	/**
	 * Quita a un estudiante de un grupo. Si no estaba inscrito, no hace nada.
	 *
	 * @param grupoId      id del grupo.
	 * @param estudianteId id del estudiante.
	 * @return la lista actualizada.
	 * @throws EntidadNoEncontradaException si el grupo no existe.
	 */
	@Transactional
	public InscritosGrupo quitar(Long grupoId, Long estudianteId) throws EntidadNoEncontradaException {
		Grupo grupo = grupoService.obtenerEntidad(grupoId);
		boolean quitado = false;
		Iterator<Estudiante> iterador = grupo.getEstudiantes().iterator();
		while (iterador.hasNext()) {
			if (iterador.next().getId().equals(estudianteId)) {
				iterador.remove();
				quitado = true;
			}
		}
		Grupo guardado = grupoRepository.save(grupo);
		if (quitado) {
			eventos.publishEvent(new CambioDatosMaestros("Estudiante retirado del grupo " + grupoId));
		}
		return construir(guardado);
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	private void verificarSinOtroGrupo(Grupo grupo, Estudiante estudiante) throws EstudianteEnOtroGrupoException {
		Grupo otro = grupoRepository.findFirstByCursoIdAndEstudiantesIdAndIdNot(grupo.getCurso().getId(),
				estudiante.getId(), grupo.getId()).orElse(null);
		int numeroOtro = 0;
		if (otro != null) {
			numeroOtro = otro.getNumero();
		}
		LanzadorException.verificarEstudianteSinOtroGrupo(otro != null, estudiante.getNombre(),
				grupo.getCurso().getNombre(), numeroOtro);
	}

	private boolean estaInscrito(Grupo grupo, Long estudianteId) {
		for (Estudiante estudiante : grupo.getEstudiantes()) {
			if (estudiante.getId().equals(estudianteId)) {
				return true;
			}
		}
		return false;
	}

	private String describir(Grupo grupo) {
		return grupo.getCurso().getCodigo() + " G" + grupo.getNumero();
	}

	private InscritosGrupo construir(Grupo grupo) {
		List<Estudiante> ordenados = new ArrayList<>(grupo.getEstudiantes());
		Collections.sort(ordenados, new Comparator<Estudiante>() {
			@Override
			public int compare(Estudiante a, Estudiante b) {
				return a.getNombre().compareToIgnoreCase(b.getNombre());
			}
		});
		List<EstudianteDTO> estudiantes = new ArrayList<>();
		for (Estudiante estudiante : ordenados) {
			estudiantes.add(estudianteService.convertirADTO(estudiante));
		}
		int faltantes = GrupoService.MIN_INSCRITOS - estudiantes.size();
		if (faltantes < 0) {
			faltantes = 0;
		}
		int cupos = GrupoService.MAX_INSCRITOS - estudiantes.size();
		if (cupos < 0) {
			cupos = 0;
		}
		return new InscritosGrupo(grupoService.convertirADTO(grupo), estudiantes, GrupoService.MIN_INSCRITOS,
				faltantes, GrupoService.MAX_INSCRITOS, cupos);
	}
}
