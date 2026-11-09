package unbosque.edu.co.aulasync.model;

import java.time.DayOfWeek;
import java.util.List;

import unbosque.edu.co.aulasync.motor.MotivoRechazo;

/**
 * Objeto de valor con un paso de DSatur: qué sesión eligió el motor, por qué
 * la eligió (saturación y grado), cuántas franjas descartó cada regla y dónde
 * la ubicó o por qué no pudo.
 *
 * @version 1.0
 */
public class PasoTraza {

	private final int numero;
	private final int nodo;
	private final int saturacion;
	private final int grado;
	private final int franjasCandidatas;
	private final int franjasViables;
	private final List<CausaDescartada> bloqueos;
	private final boolean ubicada;
	private DayOfWeek dia;
	private Integer horaInicio;
	private String aulaNombre;
	private String sedeNombre;
	private Integer cargaDia;
	private MotivoRechazo causa;
	private String causaDescripcion;
	private String diaNombre;

	/**
	 * Crea el paso con los datos de la elección.
	 *
	 * @param numero            número del paso (empieza en 1).
	 * @param nodo              índice de la sesión elegida en la lista de nodos.
	 * @param saturacion        franjas bloqueadas al momento de elegirla.
	 * @param grado             cantidad de vecinos en el grafo.
	 * @param franjasCandidatas franjas que se evaluaron para su duración.
	 * @param franjasViables    franjas que pasaron todas las reglas.
	 * @param bloqueos          franjas descartadas por cada regla, en el orden en
	 *                          que el motor evalúa las reglas.
	 * @param ubicada           {@code true} si se le asignó franja y aula.
	 */
	public PasoTraza(int numero, int nodo, int saturacion, int grado, int franjasCandidatas, int franjasViables,
			List<CausaDescartada> bloqueos, boolean ubicada) {
		this.numero = numero;
		this.nodo = nodo;
		this.saturacion = saturacion;
		this.grado = grado;
		this.franjasCandidatas = franjasCandidatas;
		this.franjasViables = franjasViables;
		this.bloqueos = bloqueos;
		this.ubicada = ubicada;
	}

	/**
	 * Registra dónde quedó la sesión.
	 *
	 * @param dia        día asignado.
	 * @param horaInicio hora de inicio asignada.
	 * @param aulaNombre aula asignada.
	 * @param sedeNombre sede del aula.
	 * @param cargaDia   carga del día elegido (sesiones del mismo docente o
	 *                   bloque ese día, más la penalización del sábado).
	 */
	public void registrarUbicacion(DayOfWeek dia, int horaInicio, String aulaNombre, String sedeNombre,
			int cargaDia) {
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.aulaNombre = aulaNombre;
		this.sedeNombre = sedeNombre;
		this.cargaDia = cargaDia;
	}

	/**
	 * Registra por qué la sesión no se pudo ubicar.
	 *
	 * @param causa motivo que descartó más franjas.
	 */
	public void registrarCausa(MotivoRechazo causa) {
		this.causa = causa;
		if (causa != null) {
			this.causaDescripcion = causa.getDescripcion();
		}
	}

	/**
	 * @param diaNombre nombre del día en español.
	 */
	public void setDiaNombre(String diaNombre) {
		this.diaNombre = diaNombre;
	}

	/**
	 * @return número del paso.
	 */
	public int getNumero() {
		return numero;
	}

	/**
	 * @return índice de la sesión elegida.
	 */
	public int getNodo() {
		return nodo;
	}

	/**
	 * @return franjas bloqueadas al momento de elegirla.
	 */
	public int getSaturacion() {
		return saturacion;
	}

	/**
	 * @return vecinos en el grafo.
	 */
	public int getGrado() {
		return grado;
	}

	/**
	 * @return franjas evaluadas.
	 */
	public int getFranjasCandidatas() {
		return franjasCandidatas;
	}

	/**
	 * @return franjas que pasaron todas las reglas.
	 */
	public int getFranjasViables() {
		return franjasViables;
	}

	/**
	 * @return franjas descartadas por cada regla.
	 */
	public List<CausaDescartada> getBloqueos() {
		return bloqueos;
	}

	/**
	 * @return {@code true} si se ubicó.
	 */
	public boolean isUbicada() {
		return ubicada;
	}

	/**
	 * @return día asignado o {@code null}.
	 */
	public DayOfWeek getDia() {
		return dia;
	}

	/**
	 * @return nombre del día asignado o {@code null}.
	 */
	public String getDiaNombre() {
		return diaNombre;
	}

	/**
	 * @return hora de inicio asignada o {@code null}.
	 */
	public Integer getHoraInicio() {
		return horaInicio;
	}

	/**
	 * @return aula asignada o {@code null}.
	 */
	public String getAulaNombre() {
		return aulaNombre;
	}

	/**
	 * @return sede asignada o {@code null}.
	 */
	public String getSedeNombre() {
		return sedeNombre;
	}

	/**
	 * @return carga del día elegido o {@code null}.
	 */
	public Integer getCargaDia() {
		return cargaDia;
	}

	/**
	 * @return causa principal si no se ubicó.
	 */
	public MotivoRechazo getCausa() {
		return causa;
	}

	/**
	 * @return explicación de la causa principal.
	 */
	public String getCausaDescripcion() {
		return causaDescripcion;
	}
}
