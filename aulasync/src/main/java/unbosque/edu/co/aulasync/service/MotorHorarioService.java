package unbosque.edu.co.aulasync.service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unbosque.edu.co.aulasync.entity.Aula;
import unbosque.edu.co.aulasync.entity.Curso;
import unbosque.edu.co.aulasync.entity.Docente;
import unbosque.edu.co.aulasync.entity.FranjaDocente;
import unbosque.edu.co.aulasync.entity.FranjaSede;
import unbosque.edu.co.aulasync.entity.Grupo;
import unbosque.edu.co.aulasync.entity.Sede;
import unbosque.edu.co.aulasync.model.AristaTraza;
import unbosque.edu.co.aulasync.model.CausaDescartada;
import unbosque.edu.co.aulasync.model.NodoTraza;
import unbosque.edu.co.aulasync.model.PasoTraza;
import unbosque.edu.co.aulasync.model.ResultadoGeneracion;
import unbosque.edu.co.aulasync.model.SesionGenerada;
import unbosque.edu.co.aulasync.model.TrazaMotor;
import unbosque.edu.co.aulasync.motor.AulaMotor;
import unbosque.edu.co.aulasync.motor.DocenteMotor;
import unbosque.edu.co.aulasync.motor.GrafoConflictos;
import unbosque.edu.co.aulasync.motor.MotivoRechazo;
import unbosque.edu.co.aulasync.motor.MotorHorario;
import unbosque.edu.co.aulasync.motor.SedeMotor;
import unbosque.edu.co.aulasync.motor.Sesion;
import unbosque.edu.co.aulasync.repository.AulaRepository;
import unbosque.edu.co.aulasync.repository.GrupoRepository;
import unbosque.edu.co.aulasync.repository.SedeRepository;

/**
 * Puente entre la base de datos y el {@link MotorHorario}.
 *
 * <p>
 * Lee los datos maestros, los convierte a los objetos planos del motor (que no
 * conoce JPA), lo ejecuta y traduce el resultado a {@link ResultadoGeneracion}
 * para la API. Solo entran al motor los grupos abiertos (al menos
 * {@value GrupoService#MIN_INSCRITOS} inscritos) y las aulas disponibles.
 * </p>
 *
 * <p>
 * También arma la {@link TrazaMotor}: el grafo, el DSU y cada paso de DSatur
 * de una ejecución, para explicarla visualmente.
 * </p>
 *
 * @version 1.1
 */
@Service
public class MotorHorarioService {

	private static final Locale ESPANOL = Locale.of("es", "CO");

	/**
	 * Datos ya leídos de la base de datos y convertidos al formato del motor.
	 */
	private static class EntradaMotor {

		private final Map<Long, Grupo> gruposPorId = new HashMap<>();
		private final Map<Long, DocenteMotor> docentes = new HashMap<>();
		private final List<String> noAbiertos = new ArrayList<>();
		private final List<Sesion> sesiones = new ArrayList<>();
		private final Map<Long, String> nombresSedes = new HashMap<>();
		private List<SedeMotor> sedes;
		private List<AulaMotor> aulas;
	}

	private final GrupoRepository grupoRepository;
	private final SedeRepository sedeRepository;
	private final AulaRepository aulaRepository;
	private final DisponibilidadService disponibilidadService;

	/**
	 * Crea el servicio con sus dependencias.
	 *
	 * @param grupoRepository       lectura de grupos.
	 * @param sedeRepository        lectura de sedes y sus franjas.
	 * @param aulaRepository        lectura de aulas.
	 * @param disponibilidadService franjas de disponibilidad de los docentes.
	 */
	public MotorHorarioService(GrupoRepository grupoRepository, SedeRepository sedeRepository,
			AulaRepository aulaRepository, DisponibilidadService disponibilidadService) {
		this.grupoRepository = grupoRepository;
		this.sedeRepository = sedeRepository;
		this.aulaRepository = aulaRepository;
		this.disponibilidadService = disponibilidadService;
	}

	/**
	 * Ejecuta el motor con los datos actuales y devuelve el resultado sin
	 * guardar nada.
	 *
	 * @return sesiones ubicadas y sin ubicar, grupos no abiertos y avisos.
	 */
	@Transactional(readOnly = true)
	public ResultadoGeneracion generar() {
		EntradaMotor entrada = prepararEntrada();
		MotorHorario motor = new MotorHorario(entrada.sesiones, new ArrayList<>(entrada.docentes.values()),
				entrada.sedes, entrada.aulas);
		MotorHorario.Resultado resultado = motor.generar();

		List<SesionGenerada> ubicadas = new ArrayList<>();
		for (Sesion sesion : resultado.getUbicadas()) {
			ubicadas.add(convertir(sesion, entrada.gruposPorId, entrada.nombresSedes));
		}
		List<SesionGenerada> sinUbicar = new ArrayList<>();
		for (Sesion sesion : resultado.getSinUbicar()) {
			sinUbicar.add(convertir(sesion, entrada.gruposPorId, entrada.nombresSedes));
		}
		ordenarUbicadas(ubicadas);
		ordenarSinUbicar(sinUbicar);

		return new ResultadoGeneracion(entrada.gruposPorId.size(), entrada.noAbiertos, ubicadas, sinUbicar,
				resultado.getAvisos());
	}

	/**
	 * Ejecuta el motor con los datos actuales y devuelve su traza completa: los
	 * nodos (sesiones) con sus vecinos por tipo de conflicto, las aristas del
	 * grafo, la componente de cada nodo en el DSU y cada paso de DSatur. No
	 * guarda nada.
	 *
	 * @return traza de la ejecución.
	 */
	@Transactional(readOnly = true)
	public TrazaMotor trazar() {
		EntradaMotor entrada = prepararEntrada();
		List<Sesion> sesiones = entrada.sesiones;
		MotorHorario motor = new MotorHorario(sesiones, new ArrayList<>(entrada.docentes.values()), entrada.sedes,
				entrada.aulas);
		MotorHorario.Resultado resultado = motor.generar();
		GrafoConflictos grafo = motor.getGrafo();

		List<NodoTraza> nodos = new ArrayList<>();
		for (int i = 0; i < sesiones.size(); i++) {
			nodos.add(crearNodo(i, sesiones.get(i), motor, entrada.gruposPorId, entrada.nombresSedes));
		}

		List<AristaTraza> aristas = new ArrayList<>();
		int porDocente = 0;
		int porGrupo = 0;
		int porBloque = 0;
		for (int i = 0; i < sesiones.size(); i++) {
			Sesion a = sesiones.get(i);
			NodoTraza nodo = nodos.get(i);
			for (int j : grafo.vecinosDe(i)) {
				Sesion b = sesiones.get(j);
				boolean mismoDocente = a.getDocenteId() == b.getDocenteId();
				boolean mismoGrupo = a.getGrupoId() == b.getGrupoId();
				boolean mismoBloque = a.getCursoId() != b.getCursoId() && GrafoConflictos.mismoBloque(a, b);
				if (mismoDocente) {
					nodo.getVecinosDocente().add(j);
				}
				if (mismoGrupo) {
					nodo.getVecinosGrupo().add(j);
				}
				if (mismoBloque) {
					nodo.getVecinosBloque().add(j);
				}
				if (i < j) {
					aristas.add(new AristaTraza(i, j, mismoDocente, mismoGrupo, mismoBloque));
					if (mismoDocente) {
						porDocente++;
					}
					if (mismoGrupo) {
						porGrupo++;
					}
					if (mismoBloque) {
						porBloque++;
					}
				}
			}
		}

		for (PasoTraza paso : motor.getPasos()) {
			nodos.get(paso.getNodo()).setOrden(paso.getNumero());
			if (paso.getDia() != null) {
				paso.setDiaNombre(nombreDia(paso.getDia()));
			}
		}

		TrazaMotor traza = new TrazaMotor();
		traza.setGruposProgramados(entrada.gruposPorId.size());
		traza.setGruposNoAbiertos(entrada.noAbiertos);
		traza.setTotalDocentes(entrada.docentes.size());
		traza.setTotalSedes(entrada.sedes.size());
		traza.setTotalAulas(entrada.aulas.size());
		traza.setTotalAristas(aristas.size());
		traza.setAristasPorDocente(porDocente);
		traza.setAristasPorGrupo(porGrupo);
		traza.setAristasPorBloque(porBloque);
		traza.setTotalComponentes(motor.totalComponentes());
		traza.setSesionesUbicadas(resultado.getUbicadas().size());
		traza.setSesionesSinUbicar(resultado.getSinUbicar().size());
		traza.setNodos(nodos);
		traza.setAristas(aristas);
		traza.setPasos(motor.getPasos());
		traza.setAvisos(resultado.getAvisos());
		return traza;
	}

	/**
	 * Nombre en español de un día, con la primera letra en mayúscula.
	 *
	 * @param dia día de la semana.
	 * @return por ejemplo "Lunes".
	 */
	public static String nombreDia(DayOfWeek dia) {
		String nombre = dia.getDisplayName(TextStyle.FULL, ESPANOL);
		return nombre.substring(0, 1).toUpperCase(ESPANOL) + nombre.substring(1);
	}

	// ------------------------------------------------------------------
	// Construcción de la entrada del motor
	// ------------------------------------------------------------------

	/**
	 * Lee grupos, sedes y aulas y los convierte a los objetos del motor. Los
	 * grupos con menos de {@value GrupoService#MIN_INSCRITOS} inscritos quedan
	 * por fuera y se reportan en la lista de no abiertos.
	 */
	private EntradaMotor prepararEntrada() {
		EntradaMotor entrada = new EntradaMotor();
		for (Grupo grupo : grupoRepository.findAllByOrderByCursoSemestreNumeroAscCursoNombreAscNumeroAsc()) {
			int inscritos = grupo.getEstudiantes().size();
			Curso curso = grupo.getCurso();
			if (inscritos < GrupoService.MIN_INSCRITOS) {
				entrada.noAbiertos.add(curso.getCodigo() + " " + curso.getNombre() + " G" + grupo.getNumero() + ": "
						+ inscritos + " inscritos (mínimo " + GrupoService.MIN_INSCRITOS + ").");
				continue;
			}
			entrada.gruposPorId.put(grupo.getId(), grupo);
			agregarDocente(grupo.getDocente(), entrada.docentes);
			int cantidad = curso.getIntensidad().getSesionesSemana();
			for (int i = 1; i <= cantidad; i++) {
				entrada.sesiones.add(new Sesion(grupo.getId(), i, curso.getId(), curso.getCodigo(), grupo.getNumero(),
						curso.getSemestre().getNumero(), curso.isNucleoComun(), grupo.getDocente().getId(),
						curso.getIntensidad().getDuracionHoras(), curso.getTipoAula(), inscritos));
			}
		}
		entrada.sedes = construirSedes(entrada.nombresSedes);
		entrada.aulas = construirAulas();
		return entrada;
	}

	private void agregarDocente(Docente docente, Map<Long, DocenteMotor> docentes) {
		if (docentes.containsKey(docente.getId())) {
			return;
		}
		DocenteMotor motor = new DocenteMotor(docente.getId(), docente.getDedicacion().getHorasMaximas());
		for (FranjaDocente franja : disponibilidadService.listarEntidades(docente.getId())) {
			motor.habilitar(franja.getDia(), franja.getHoraInicio(), franja.getHoraFin());
		}
		docentes.put(docente.getId(), motor);
	}

	private List<SedeMotor> construirSedes(Map<Long, String> nombres) {
		List<SedeMotor> sedes = new ArrayList<>();
		for (Sede sede : sedeRepository.findAllByOrderByNombreAsc()) {
			SedeMotor motor = new SedeMotor(sede.getId(), sede.getNombre(), sede.isExclusivaPrimerSemestre(),
					sede.getDiasPrimerSemestre());
			for (FranjaSede franja : sede.getFranjas()) {
				motor.habilitar(franja.getDia(), franja.getHoraInicio(), franja.getHoraFin());
			}
			sedes.add(motor);
			nombres.put(sede.getId(), sede.getNombre());
		}
		return sedes;
	}

	private List<AulaMotor> construirAulas() {
		List<AulaMotor> aulas = new ArrayList<>();
		for (Aula aula : aulaRepository.findAllByOrderBySedeNombreAscNombreAsc()) {
			if (aula.isDisponible()) {
				aulas.add(new AulaMotor(aula.getId(), aula.getNombre(), aula.getCapacidad(), aula.getTipo(),
						aula.getSede().getId()));
			}
		}
		return aulas;
	}

	// ------------------------------------------------------------------
	// Conversión del resultado
	// ------------------------------------------------------------------

	private SesionGenerada convertir(Sesion sesion, Map<Long, Grupo> grupos, Map<Long, String> nombresSedes) {
		Grupo grupo = grupos.get(sesion.getGrupoId());
		SesionGenerada salida = new SesionGenerada();
		salida.setGrupoId(sesion.getGrupoId());
		salida.setNumeroSesion(sesion.getNumeroSesion());
		salida.setCursoId(sesion.getCursoId());
		salida.setCursoCodigo(sesion.getCursoCodigo());
		salida.setCursoNombre(grupo.getCurso().getNombre());
		salida.setSemestre(sesion.getSemestre());
		salida.setNumeroGrupo(sesion.getNumeroGrupo());
		salida.setNucleoComun(sesion.isNucleoComun());
		salida.setDocenteId(sesion.getDocenteId());
		salida.setDocenteNombre(grupo.getDocente().getUsuario().getNombre());
		salida.setEstudiantes(sesion.getEstudiantes());
		salida.setDuracionHoras(sesion.getDuracionHoras());

		if (sesion.getFranja() != null) {
			salida.setDia(sesion.getFranja().getDia());
			salida.setDiaNombre(nombreDia(sesion.getFranja().getDia()));
			salida.setHoraInicio(sesion.getFranja().getHoraInicio());
			salida.setHoraFin(sesion.getFranja().getHoraInicio() + sesion.getDuracionHoras());
			salida.setAulaId(sesion.getAula().getId());
			salida.setAulaNombre(sesion.getAula().getNombre());
			salida.setSedeId(sesion.getFranja().getSedeId());
			salida.setSedeNombre(nombresSedes.get(sesion.getFranja().getSedeId()));
		} else {
			MotivoRechazo causa = sesion.getCausaSinUbicar();
			salida.setCausa(causa);
			if (causa != null) {
				salida.setCausaDescripcion(causa.getDescripcion());
			}
			salida.setDesglose(construirDesglose(sesion.getDesgloseCausas()));
		}
		return salida;
	}

	private NodoTraza crearNodo(int indice, Sesion sesion, MotorHorario motor, Map<Long, Grupo> grupos,
			Map<Long, String> nombresSedes) {
		Grupo grupo = grupos.get(sesion.getGrupoId());
		NodoTraza nodo = new NodoTraza();
		nodo.setIndice(indice);
		nodo.setGrupoId(sesion.getGrupoId());
		nodo.setNumeroSesion(sesion.getNumeroSesion());
		nodo.setCursoId(sesion.getCursoId());
		nodo.setCursoCodigo(sesion.getCursoCodigo());
		nodo.setCursoNombre(grupo.getCurso().getNombre());
		nodo.setSemestre(sesion.getSemestre());
		nodo.setNumeroGrupo(sesion.getNumeroGrupo());
		nodo.setNucleoComun(sesion.isNucleoComun());
		nodo.setDocenteId(sesion.getDocenteId());
		nodo.setDocenteNombre(grupo.getDocente().getUsuario().getNombre());
		nodo.setEstudiantes(sesion.getEstudiantes());
		nodo.setDuracionHoras(sesion.getDuracionHoras());
		nodo.setTipoAula(sesion.getTipoAula());
		nodo.setGrado(motor.getGrafo().grado(indice));
		nodo.setRaizDsu(motor.raizDsu(indice));
		nodo.setMiembrosComponente(motor.miembrosComponente(indice));
		if (sesion.getFranja() != null) {
			nodo.setUbicada(true);
			nodo.setDia(sesion.getFranja().getDia());
			nodo.setDiaNombre(nombreDia(sesion.getFranja().getDia()));
			nodo.setHoraInicio(sesion.getFranja().getHoraInicio());
			nodo.setHoraFin(sesion.getFranja().getHoraInicio() + sesion.getDuracionHoras());
			nodo.setAulaNombre(sesion.getAula().getNombre());
			nodo.setSedeNombre(nombresSedes.get(sesion.getFranja().getSedeId()));
		} else {
			MotivoRechazo causa = sesion.getCausaSinUbicar();
			nodo.setCausa(causa);
			if (causa != null) {
				nodo.setCausaDescripcion(causa.getDescripcion());
			}
		}
		return nodo;
	}

	private List<CausaDescartada> construirDesglose(Map<MotivoRechazo, Integer> causas) {
		List<CausaDescartada> desglose = new ArrayList<>();
		for (Map.Entry<MotivoRechazo, Integer> entrada : causas.entrySet()) {
			desglose.add(new CausaDescartada(entrada.getKey(), entrada.getKey().getDescripcion(),
					entrada.getValue()));
		}
		Collections.sort(desglose, new Comparator<CausaDescartada>() {
			@Override
			public int compare(CausaDescartada a, CausaDescartada b) {
				return Integer.compare(b.getFranjas(), a.getFranjas());
			}
		});
		return desglose;
	}

	private void ordenarUbicadas(List<SesionGenerada> sesiones) {
		Collections.sort(sesiones, new Comparator<SesionGenerada>() {
			@Override
			public int compare(SesionGenerada a, SesionGenerada b) {
				int porSemestre = Integer.compare(a.getSemestre(), b.getSemestre());
				if (porSemestre != 0) {
					return porSemestre;
				}
				int porDia = a.getDia().compareTo(b.getDia());
				if (porDia != 0) {
					return porDia;
				}
				return Integer.compare(a.getHoraInicio(), b.getHoraInicio());
			}
		});
	}

	private void ordenarSinUbicar(List<SesionGenerada> sesiones) {
		Collections.sort(sesiones, new Comparator<SesionGenerada>() {
			@Override
			public int compare(SesionGenerada a, SesionGenerada b) {
				int porSemestre = Integer.compare(a.getSemestre(), b.getSemestre());
				if (porSemestre != 0) {
					return porSemestre;
				}
				int porCurso = a.getCursoCodigo().compareTo(b.getCursoCodigo());
				if (porCurso != 0) {
					return porCurso;
				}
				return Integer.compare(a.getNumeroGrupo(), b.getNumeroGrupo());
			}
		});
	}
}
