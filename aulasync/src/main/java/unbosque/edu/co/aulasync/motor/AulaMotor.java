package unbosque.edu.co.aulasync.motor;

import unbosque.edu.co.aulasync.model.TipoAula;

/**
 * Aula tal como la ve el motor. Solo se le pasan aulas marcadas como
 * disponibles.
 *
 * @version 2.0
 */
public class AulaMotor {

	private final long id;
	private final String nombre;
	private final int capacidad;
	private final TipoAula tipo;
	private final long sedeId;

	/**
	 * Crea un aula.
	 *
	 * @param id        id del aula.
	 * @param nombre    nombre del aula.
	 * @param capacidad número máximo de estudiantes.
	 * @param tipo      tipo de espacio.
	 * @param sedeId    id de la sede donde está.
	 */
	public AulaMotor(long id, String nombre, int capacidad, TipoAula tipo, long sedeId) {
		this.id = id;
		this.nombre = nombre;
		this.capacidad = capacidad;
		this.tipo = tipo;
		this.sedeId = sedeId;
	}

	/**
	 * @return id del aula.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return nombre del aula.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @return capacidad.
	 */
	public int getCapacidad() {
		return capacidad;
	}

	/**
	 * @return tipo de espacio.
	 */
	public TipoAula getTipo() {
		return tipo;
	}

	/**
	 * @return id de la sede.
	 */
	public long getSedeId() {
		return sedeId;
	}
}
