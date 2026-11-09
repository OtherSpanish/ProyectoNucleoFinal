package unbosque.edu.co.aulasync.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de entrada y salida de {@code Sede}, con sus mismos atributos.
 *
 * <p>
 * Al crear o actualizar la sede, {@code franjas} se ignora: las franjas se
 * reemplazan con su propio endpoint. En las respuestas llegan ordenadas de
 * lunes a sábado y por hora.
 * </p>
 *
 * @version 1.0
 */
public class SedeDTO {

	private Long id;
	private String nombre;
	private Boolean exclusivaPrimerSemestre;
	private Integer diasPrimerSemestre;
	private List<FranjaSedeDTO> franjas = new ArrayList<>();

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public SedeDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO con todos sus datos.
	 *
	 * @param id                      identificador de la sede.
	 * @param nombre                  nombre de la sede.
	 * @param exclusivaPrimerSemestre si solo admite grupos de primer semestre.
	 * @param diasPrimerSemestre      días exactos por semana para primer
	 *                                semestre.
	 * @param franjas                 franjas de clase de la sede.
	 */
	public SedeDTO(Long id, String nombre, Boolean exclusivaPrimerSemestre, Integer diasPrimerSemestre,
			List<FranjaSedeDTO> franjas) {
		this.id = id;
		this.nombre = nombre;
		this.exclusivaPrimerSemestre = exclusivaPrimerSemestre;
		this.diasPrimerSemestre = diasPrimerSemestre;
		this.franjas = franjas;
	}

	/**
	 * @return identificador de la sede.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador de la sede.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return nombre de la sede.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre de la sede.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return si solo admite grupos de primer semestre.
	 */
	public Boolean getExclusivaPrimerSemestre() {
		return exclusivaPrimerSemestre;
	}

	/**
	 * @param exclusivaPrimerSemestre si solo admite grupos de primer semestre.
	 */
	public void setExclusivaPrimerSemestre(Boolean exclusivaPrimerSemestre) {
		this.exclusivaPrimerSemestre = exclusivaPrimerSemestre;
	}

	/**
	 * @return días exactos por semana para primer semestre.
	 */
	public Integer getDiasPrimerSemestre() {
		return diasPrimerSemestre;
	}

	/**
	 * @param diasPrimerSemestre días exactos por semana para primer semestre.
	 */
	public void setDiasPrimerSemestre(Integer diasPrimerSemestre) {
		this.diasPrimerSemestre = diasPrimerSemestre;
	}

	/**
	 * @return franjas de clase de la sede.
	 */
	public List<FranjaSedeDTO> getFranjas() {
		return franjas;
	}

	/**
	 * @param franjas franjas de clase de la sede.
	 */
	public void setFranjas(List<FranjaSedeDTO> franjas) {
		this.franjas = franjas;
	}
}
