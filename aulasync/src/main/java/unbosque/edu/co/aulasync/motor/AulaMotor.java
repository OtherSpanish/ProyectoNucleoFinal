package unbosque.edu.co.aulasync.motor;

public class AulaMotor {
	private final int id;
	private final int capacidad;
	private final TipoAula tipo;

	public AulaMotor(int id, int capacidad, TipoAula tipo) {
		this.id = id;
		this.capacidad = capacidad;
		this.tipo = tipo;
	}

	public int getId() {
		return id;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public TipoAula getTipo() {
		return tipo;
	}
}