package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Reglas del horario compartidas por el motor (generación automática) y el
 * editor (movimientos manuales con drag and drop).
 *
 * <p>
 * Tener las reglas en un solo lugar garantiza que una sesión que el motor
 * considera inválida también lo sea para el editor, y que una regla nueva se
 * agregue una sola vez. Todos los métodos son estáticos y no guardan estado.
 * </p>
 *
 * @version 1.0
 */
public final class ReglasHorario {

	private static final DayOfWeek[] ENTRE_SEMANA = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };

	private ReglasHorario() {
		// Clase utilitaria: no se instancia.
	}

	// ------------------------------------------------------------------
	// Sede de primer semestre
	// ------------------------------------------------------------------

	/**
	 * Busca la sede de primer semestre: la primera exclusiva con días
	 * asignados.
	 *
	 * @param sedes sedes del sistema.
	 * @return la sede, o {@code null} si no hay ninguna.
	 */
	public static SedeMotor buscarSedePrimerSemestre(Collection<SedeMotor> sedes) {
		for (SedeMotor sede : sedes) {
			if (sede.isExclusivaPrimerSemestre() && sede.getDiasPrimerSemestre() > 0) {
				return sede;
			}
		}
		return null;
	}

	/**
	 * Calcula los días de la sede de primer semestre para un bloque. Los
	 * bloques rotan: con 2 días, G1 lunes y miércoles, G2 martes y jueves, G3
	 * miércoles y viernes, y así.
	 *
	 * @param numeroGrupo número de grupo (bloque).
	 * @param cantidad    días por semana en la sede.
	 * @return días asignados, de lunes a viernes, ordenados.
	 */
	public static List<DayOfWeek> calcularDiasPrimerSemestre(int numeroGrupo, int cantidad) {
		List<DayOfWeek> dias = new ArrayList<>();
		int total = ENTRE_SEMANA.length;
		int limite = Math.min(cantidad, total);
		int inicio = Math.floorMod(numeroGrupo - 1, total);
		for (int i = 0; i < limite; i++) {
			dias.add(ENTRE_SEMANA[(inicio + 2 * i) % total]);
		}
		Collections.sort(dias);
		return dias;
	}

	/**
	 * Regla de sede:
	 * <ul>
	 * <li>Una sede exclusiva solo admite primer semestre.</li>
	 * <li>Las materias núcleo de primer semestre van solo a la sede de primer
	 * semestre y solo en los días de su bloque.</li>
	 * <li>Las propias de primer semestre van a otras sedes y nunca en esos
	 * días.</li>
	 * </ul>
	 *
	 * @param sesion             sesión a ubicar.
	 * @param sedeDestino        sede donde se dictaría.
	 * @param dia                día donde se dictaría.
	 * @param sedePrimerSemestre sede de primer semestre, o {@code null}.
	 * @param diasDelBloque      días de esa sede para el bloque de la sesión
	 *                           (vacío si no hay sede de primer semestre).
	 * @return {@code true} si la sede está permitida ese día para la sesión.
	 */
	public static boolean sedePermitida(Sesion sesion, SedeMotor sedeDestino, DayOfWeek dia,
			SedeMotor sedePrimerSemestre, List<DayOfWeek> diasDelBloque) {
		boolean esPrimerSemestre = sesion.getSemestre() == 1;
		if (sedeDestino.isExclusivaPrimerSemestre() && !esPrimerSemestre) {
			return false;
		}
		if (sedePrimerSemestre == null || !esPrimerSemestre) {
			return true;
		}
		boolean enSedeEspecial = sedeDestino.getId() == sedePrimerSemestre.getId();
		boolean esDiaEspecial = diasDelBloque.contains(dia);
		if (sesion.isNucleoComun()) {
			return enSedeEspecial && esDiaEspecial;
		}
		return !enSedeEspecial && !esDiaEspecial;
	}

	// ------------------------------------------------------------------
	// Docente
	// ------------------------------------------------------------------

	/**
	 * Regla de carga: las horas ya asignadas más la sesión no pueden superar el
	 * tope del docente.
	 *
	 * @param horasAsignadas horas que el docente ya tiene.
	 * @param duracion       duración de la sesión.
	 * @param tope           tope de su dedicación.
	 * @return {@code true} si la carga queda dentro del tope.
	 */
	public static boolean cargaPermitida(int horasAsignadas, int duracion, int tope) {
		return horasAsignadas + duracion <= tope;
	}

	// ------------------------------------------------------------------
	// Choques entre sesiones
	// ------------------------------------------------------------------

	/**
	 * Revisa si una sesión, puesta en una franja, choca con otra ya ubicada.
	 *
	 * @param sesion sesión a ubicar.
	 * @param franja franja candidata.
	 * @param otra   otra sesión.
	 * @return {@code CHOQUE_DOCENTE}, {@code CHOQUE_GRUPO} o
	 *         {@code CHOQUE_BLOQUE} si chocan; {@code null} si no.
	 */
	public static MotivoRechazo motivoChoque(Sesion sesion, Franja franja, Sesion otra) {
		if (otra == sesion || otra.getFranja() == null || !GrafoConflictos.enConflicto(sesion, otra)) {
			return null;
		}
		if (!Franja.traslapan(franja, sesion.getDuracionHoras(), otra.getFranja(), otra.getDuracionHoras())) {
			return null;
		}
		if (otra.getDocenteId() == sesion.getDocenteId()) {
			return MotivoRechazo.CHOQUE_DOCENTE;
		}
		if (otra.getGrupoId() == sesion.getGrupoId()) {
			return MotivoRechazo.CHOQUE_GRUPO;
		}
		return MotivoRechazo.CHOQUE_BLOQUE;
	}

	/**
	 * Regla de un día por grupo: indica si otra sesión del mismo grupo ya está
	 * ese día.
	 *
	 * @param sesion sesión a ubicar.
	 * @param franja franja candidata.
	 * @param otra   otra sesión.
	 * @return {@code true} si la otra es del mismo grupo y ya está ese día.
	 */
	public static boolean mismoDiaDelGrupo(Sesion sesion, Franja franja, Sesion otra) {
		return otra != sesion && otra.getGrupoId() == sesion.getGrupoId() && otra.getFranja() != null
				&& otra.getFranja().getDia() == franja.getDia();
	}

	// ------------------------------------------------------------------
	// Aulas
	// ------------------------------------------------------------------

	/**
	 * Indica si un aula sirve para la sesión en esa franja: misma sede, tipo
	 * exigido por el curso y capacidad para los inscritos.
	 *
	 * @param aula   aula candidata.
	 * @param sesion sesión a ubicar.
	 * @param franja franja candidata.
	 * @return {@code true} si el aula es compatible.
	 */
	public static boolean aulaCompatible(AulaMotor aula, Sesion sesion, Franja franja) {
		return aula.getSedeId() == franja.getSedeId() && aula.getTipo() == sesion.getTipoAula()
				&& aula.getCapacidad() >= sesion.getEstudiantes();
	}

	/**
	 * Indica si un aula está libre en esa franja.
	 *
	 * @param sesion   sesión a ubicar (se ignora si aparece en la lista).
	 * @param franja   franja candidata.
	 * @param ocupadas sesiones ya ubicadas en esa aula (puede ser {@code null}).
	 * @return {@code true} si ninguna se cruza con la franja.
	 */
	public static boolean aulaLibre(Sesion sesion, Franja franja, List<Sesion> ocupadas) {
		if (ocupadas == null) {
			return true;
		}
		for (Sesion otra : ocupadas) {
			if (otra != sesion && otra.getFranja() != null
					&& Franja.traslapan(franja, sesion.getDuracionHoras(), otra.getFranja(), otra.getDuracionHoras())) {
				return false;
			}
		}
		return true;
	}
}
