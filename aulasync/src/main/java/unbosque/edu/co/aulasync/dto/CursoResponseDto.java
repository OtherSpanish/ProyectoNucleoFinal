package unbosque.edu.co.aulasync.dto;

import unbosque.edu.co.aulasync.motor.TipoAula;

/**
 * DTO de salida con los datos de un curso.
 *
 * <p>
 * Evita exponer la entidad JPA {@code Curso} directamente al front, de modo que
 * cambios en la base de datos no rompan el contrato de la API.
 * </p>
 */
public class CursoResponseDto {

	private final Long id;
	private final String nombre;
	private final int sesionesSemana;
	private final int duracionHoras;
	private final TipoAula tipoAula;

	/**
	 * Crea el DTO de un curso.
	 *
	 * @param id             identificador del curso
	 * @param nombre         nombre del curso
	 * @param sesionesSemana número de sesiones por semana
	 * @param duracionHoras  duración de cada sesión en horas
	 * @param tipoAula       tipo de aula requerida
	 */
	public CursoResponseDto(Long id, String nombre, int sesionesSemana, int duracionHoras, TipoAula tipoAula) {
		this.id = id;
		this.nombre = nombre;
		this.sesionesSemana = sesionesSemana;
		this.duracionHoras = duracionHoras;
		this.tipoAula = tipoAula;
	}

	/** @return identificador del curso */
	public Long getId() {
		return id;
	}

	/** @return nombre del curso */
	public String getNombre() {
		return nombre;
	}

	/** @return número de sesiones por semana */
	public int getSesionesSemana() {
		return sesionesSemana;
	}

	/** @return duración de cada sesión en horas */
	public int getDuracionHoras() {
		return duracionHoras;
	}

	/** @return tipo de aula requerida */
	public TipoAula getTipoAula() {
		return tipoAula;
	}
}