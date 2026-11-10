package unbosque.edu.co.aulasync.model;

/**
 * Objeto de valor con una opción de un catálogo: el valor que viaja en la API
 * (el nombre del enum), su nombre para mostrar y, según el catálogo, sus datos
 * propios (horas de una dedicación, sesiones y duración de una intensidad,
 * explicación de un motivo). Los datos que no aplican van en {@code null}.
 *
 * @version 1.0
 */
public class OpcionCatalogo {

	private final String valor;
	private final String etiqueta;
	private String descripcion;
	private Integer horasMaximas;
	private Integer sesionesSemana;
	private Integer duracionHoras;
	private Integer horaCierre;

	/**
	 * Crea la opción.
	 *
	 * @param valor    valor que viaja en la API.
	 * @param etiqueta nombre para mostrar.
	 */
	public OpcionCatalogo(String valor, String etiqueta) {
		this.valor = valor;
		this.etiqueta = etiqueta;
	}

	/**
	 * @return valor que viaja en la API.
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @return nombre para mostrar.
	 */
	public String getEtiqueta() {
		return etiqueta;
	}

	/**
	 * @return explicación (motivos de rechazo) o {@code null}.
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion explicación de la opción.
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return tope semanal de horas (dedicaciones) o {@code null}.
	 */
	public Integer getHorasMaximas() {
		return horasMaximas;
	}

	/**
	 * @param horasMaximas tope semanal de horas.
	 */
	public void setHorasMaximas(Integer horasMaximas) {
		this.horasMaximas = horasMaximas;
	}

	/**
	 * @return sesiones por semana (intensidades) o {@code null}.
	 */
	public Integer getSesionesSemana() {
		return sesionesSemana;
	}

	/**
	 * @param sesionesSemana sesiones por semana.
	 */
	public void setSesionesSemana(Integer sesionesSemana) {
		this.sesionesSemana = sesionesSemana;
	}

	/**
	 * @return horas de cada sesión (intensidades) o {@code null}.
	 */
	public Integer getDuracionHoras() {
		return duracionHoras;
	}

	/**
	 * @param duracionHoras horas de cada sesión.
	 */
	public void setDuracionHoras(Integer duracionHoras) {
		this.duracionHoras = duracionHoras;
	}

	/**
	 * @return hora de cierre de la jornada (días) o {@code null}.
	 */
	public Integer getHoraCierre() {
		return horaCierre;
	}

	/**
	 * @param horaCierre hora de cierre de la jornada ese día.
	 */
	public void setHoraCierre(Integer horaCierre) {
		this.horaCierre = horaCierre;
	}
}
