package unbosque.edu.co.aulasync.dto;

import unbosque.edu.co.aulasync.model.TipoAula;

/**
 * DTO de entrada y salida de {@code Aula}.
 *
 * <p>
 * Tiene los mismos atributos que la entidad, salvo que la sede se representa
 * con {@code sedeId} (obligatorio al crear o actualizar) y {@code sedeNombre}
 * (solo informativo en las respuestas).
 * </p>
 *
 * @version 1.0
 */
public class AulaDTO {

	private Long id;
	private String nombre;
	private Integer capacidad;
	private TipoAula tipo;
	private Boolean disponible;
	private Long sedeId;
	private String sedeNombre;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public AulaDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id         identificador del aula.
	 * @param nombre     nombre del aula.
	 * @param capacidad  número máximo de estudiantes.
	 * @param tipo       tipo de espacio.
	 * @param disponible si el motor puede usarla.
	 * @param sedeId     id de la sede.
	 * @param sedeNombre nombre de la sede.
	 */
	public AulaDTO(Long id, String nombre, Integer capacidad, TipoAula tipo, Boolean disponible, Long sedeId,
			String sedeNombre) {
		this.id = id;
		this.nombre = nombre;
		this.capacidad = capacidad;
		this.tipo = tipo;
		this.disponible = disponible;
		this.sedeId = sedeId;
		this.sedeNombre = sedeNombre;
	}

	/**
	 * @return identificador del aula.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del aula.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return nombre del aula.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre del aula.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return número máximo de estudiantes.
	 */
	public Integer getCapacidad() {
		return capacidad;
	}

	/**
	 * @param capacidad número máximo de estudiantes.
	 */
	public void setCapacidad(Integer capacidad) {
		this.capacidad = capacidad;
	}

	/**
	 * @return tipo de espacio.
	 */
	public TipoAula getTipo() {
		return tipo;
	}

	/**
	 * @param tipo tipo de espacio.
	 */
	public void setTipo(TipoAula tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return si el motor puede usarla.
	 */
	public Boolean getDisponible() {
		return disponible;
	}

	/**
	 * @param disponible si el motor puede usarla.
	 */
	public void setDisponible(Boolean disponible) {
		this.disponible = disponible;
	}

	/**
	 * @return id de la sede.
	 */
	public Long getSedeId() {
		return sedeId;
	}

	/**
	 * @param sedeId id de la sede.
	 */
	public void setSedeId(Long sedeId) {
		this.sedeId = sedeId;
	}

	/**
	 * @return nombre de la sede.
	 */
	public String getSedeNombre() {
		return sedeNombre;
	}

	/**
	 * @param sedeNombre nombre de la sede.
	 */
	public void setSedeNombre(String sedeNombre) {
		this.sedeNombre = sedeNombre;
	}
}
