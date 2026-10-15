package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;

public class DocenteMotor {
	private final int id;
	private final int topeHorasSemanales; // según dedicación (RD5)
	private final Map<DayOfWeek, Set<Integer>> horasDisponibles; // null = siempre disponible

	public DocenteMotor(int id, int topeHorasSemanales, Map<DayOfWeek, Set<Integer>> horasDisponibles) {
		this.id = id;
		this.topeHorasSemanales = topeHorasSemanales;
		this.horasDisponibles = horasDisponibles;
	}

	public boolean estaDisponibleEn(Franja f, int duracion) { // RD4
		if (horasDisponibles == null)
			return true;
		Set<Integer> horas = horasDisponibles.getOrDefault(f.dia(), Set.of());
		for (int h = f.horaInicio(); h < f.horaInicio() + duracion; h++)
			if (!horas.contains(h))
				return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public int getTopeHorasSemanales() {
		return topeHorasSemanales;
	}
}