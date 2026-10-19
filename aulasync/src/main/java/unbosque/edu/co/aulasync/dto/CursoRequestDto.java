package unbosque.edu.co.aulasync.dto;

import unbosque.edu.co.aulasync.motor.TipoAula;

/**
 * DTO de entrada para crear un curso ({@code POST /api/cursos}).
 *
 * <p>
 * Los campos numéricos son {@link Integer} (y no {@code int}) para poder
 * distinguir un valor ausente de un cero. Las reglas se validan en el servicio
 * mediante {@code LanzadorException}.
 * </p>
 */
public class CursoRequestDto {

	private String nombre;
	private Integer sesionesSemana;
	private Integer duracionHoras;
	private TipoAula tipoAula;

	/** Constructor vacío requerido para deserializar el JSON. */
	public CursoRequestDto() {
	}

	/** @return nombre del curso */
	public String getNombre() {
		return nombre;
	}

	/** @param nombre nombre del curso */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/** @return número de sesiones por semana (1 a 4) */
	public Integer getSesionesSemana() {
		return sesionesSemana;
	}

	/** @param sesionesSemana número de sesiones por semana */
	public void setSesionesSemana(Integer sesionesSemana) {
		this.sesionesSemana = sesionesSemana;
	}

	/** @return duración de cada sesión en horas (2 o 4) */
	public Integer getDuracionHoras() {
		return duracionHoras;
	}

	/** @param duracionHoras duración de cada sesión en horas */
	public void setDuracionHoras(Integer duracionHoras) {
		this.duracionHoras = duracionHoras;
	}

	/** @return tipo de aula que requiere el curso */
	public TipoAula getTipoAula() {
		return tipoAula;
	}

	/** @param tipoAula tipo de aula que requiere el curso */
	public void setTipoAula(TipoAula tipoAula) {
		this.tipoAula = tipoAula;
	}
}