package unbosque.edu.co.aulasync.dto;

import unbosque.edu.co.aulasync.model.IntensidadHoraria;
import unbosque.edu.co.aulasync.model.TipoAula;

/**
 * DTO de entrada y salida de {@code Curso}.
 *
 * <p>
 * Tiene los mismos atributos que la entidad, salvo que el semestre y la
 * especialidad se representan con su id (obligatorios al crear o actualizar).
 * {@code nucleoComun} es opcional al crear (por defecto {@code false}). En las
 * respuestas incluye además datos informativos: número de semestre, nombre de
 * la especialidad y el desglose de la intensidad.
 * </p>
 *
 * @version 1.1
 */
public class CursoDTO {

	private Long id;
	private String codigo;
	private String nombre;
	private Long semestreId;
	private Integer semestreNumero;
	private Long especialidadId;
	private String especialidadNombre;
	private TipoAula tipoAula;
	private IntensidadHoraria intensidad;
	private Boolean nucleoComun;
	private Integer sesionesSemana;
	private Integer duracionHoras;
	private Integer horasSemanales;

	/**
	 * Constructor vacío requerido para la deserialización JSON.
	 */
	public CursoDTO() {
		// Requerido por Jackson.
	}

	/**
	 * Crea el DTO de salida con todos sus datos. El desglose de la intensidad
	 * se calcula a partir de {@code intensidad}.
	 *
	 * @param id                 identificador del curso.
	 * @param codigo             código único.
	 * @param nombre             nombre de la asignatura.
	 * @param semestreId         id del semestre.
	 * @param semestreNumero     número del semestre.
	 * @param especialidadId     id de la especialidad.
	 * @param especialidadNombre nombre de la especialidad.
	 * @param tipoAula           tipo de aula que necesita.
	 * @param intensidad         patrón de sesiones semanales.
	 * @param nucleoComun        si es materia del núcleo común.
	 */
	public CursoDTO(Long id, String codigo, String nombre, Long semestreId, Integer semestreNumero,
			Long especialidadId, String especialidadNombre, TipoAula tipoAula, IntensidadHoraria intensidad,
			Boolean nucleoComun) {
		this.id = id;
		this.codigo = codigo;
		this.nombre = nombre;
		this.semestreId = semestreId;
		this.semestreNumero = semestreNumero;
		this.especialidadId = especialidadId;
		this.especialidadNombre = especialidadNombre;
		this.tipoAula = tipoAula;
		this.intensidad = intensidad;
		this.nucleoComun = nucleoComun;
		if (intensidad != null) {
			this.sesionesSemana = intensidad.getSesionesSemana();
			this.duracionHoras = intensidad.getDuracionHoras();
			this.horasSemanales = intensidad.getHorasSemanales();
		}
	}

	/**
	 * @return identificador del curso.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del curso.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return código único.
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo código único.
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return nombre de la asignatura.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre nombre de la asignatura.
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return id del semestre.
	 */
	public Long getSemestreId() {
		return semestreId;
	}

	/**
	 * @param semestreId id del semestre.
	 */
	public void setSemestreId(Long semestreId) {
		this.semestreId = semestreId;
	}

	/**
	 * @return número del semestre.
	 */
	public Integer getSemestreNumero() {
		return semestreNumero;
	}

	/**
	 * @param semestreNumero número del semestre.
	 */
	public void setSemestreNumero(Integer semestreNumero) {
		this.semestreNumero = semestreNumero;
	}

	/**
	 * @return id de la especialidad.
	 */
	public Long getEspecialidadId() {
		return especialidadId;
	}

	/**
	 * @param especialidadId id de la especialidad.
	 */
	public void setEspecialidadId(Long especialidadId) {
		this.especialidadId = especialidadId;
	}

	/**
	 * @return nombre de la especialidad.
	 */
	public String getEspecialidadNombre() {
		return especialidadNombre;
	}

	/**
	 * @param especialidadNombre nombre de la especialidad.
	 */
	public void setEspecialidadNombre(String especialidadNombre) {
		this.especialidadNombre = especialidadNombre;
	}

	/**
	 * @return tipo de aula que necesita.
	 */
	public TipoAula getTipoAula() {
		return tipoAula;
	}

	/**
	 * @param tipoAula tipo de aula que necesita.
	 */
	public void setTipoAula(TipoAula tipoAula) {
		this.tipoAula = tipoAula;
	}

	/**
	 * @return patrón de sesiones semanales.
	 */
	public IntensidadHoraria getIntensidad() {
		return intensidad;
	}

	/**
	 * @param intensidad patrón de sesiones semanales.
	 */
	public void setIntensidad(IntensidadHoraria intensidad) {
		this.intensidad = intensidad;
	}

	/**
	 * @return si es materia del núcleo común.
	 */
	public Boolean getNucleoComun() {
		return nucleoComun;
	}

	/**
	 * @param nucleoComun si es materia del núcleo común.
	 */
	public void setNucleoComun(Boolean nucleoComun) {
		this.nucleoComun = nucleoComun;
	}

	/**
	 * @return sesiones por semana (informativo).
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
	 * @return horas por sesión (informativo).
	 */
	public Integer getDuracionHoras() {
		return duracionHoras;
	}

	/**
	 * @param duracionHoras horas por sesión.
	 */
	public void setDuracionHoras(Integer duracionHoras) {
		this.duracionHoras = duracionHoras;
	}

	/**
	 * @return horas totales por semana (informativo).
	 */
	public Integer getHorasSemanales() {
		return horasSemanales;
	}

	/**
	 * @param horasSemanales horas totales por semana.
	 */
	public void setHorasSemanales(Integer horasSemanales) {
		this.horasSemanales = horasSemanales;
	}
}
