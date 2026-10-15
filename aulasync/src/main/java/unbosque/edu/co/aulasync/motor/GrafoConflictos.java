package unbosque.edu.co.aulasync.motor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrafoConflictos {
	private final List<Sesion> nodos;
	private final List<Set<Integer>> adj = new ArrayList<>();

	public GrafoConflictos(List<Sesion> sesiones) {
		this.nodos = sesiones;
		for (int i = 0; i < sesiones.size(); i++)
			adj.add(new HashSet<>());
		for (int i = 0; i < sesiones.size(); i++)
			for (int j = i + 1; j < sesiones.size(); j++)
				if (compartenRecurso(sesiones.get(i), sesiones.get(j))) {
					adj.get(i).add(j);
					adj.get(j).add(i);
				}
	}

	private boolean compartenRecurso(Sesion a, Sesion b) {
		return a.getDocenteId() == b.getDocenteId() // RD1
				|| a.getGrupoId() == b.getGrupoId(); // RD2
	}

	public Set<Integer> vecinosDe(int i) {
		return adj.get(i);
	}

	public int grado(int i) {
		return adj.get(i).size();
	}
}