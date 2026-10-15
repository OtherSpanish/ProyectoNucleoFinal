package unbosque.edu.co.aulasync.motor;

import java.time.DayOfWeek;

public record Franja(DayOfWeek dia, int horaInicio) {

	public static boolean traslapan(Franja a, int durA, Franja b, int durB) {
		return a.dia() == b.dia() && a.horaInicio() < b.horaInicio() + durB && b.horaInicio() < a.horaInicio() + durA;
	}
}