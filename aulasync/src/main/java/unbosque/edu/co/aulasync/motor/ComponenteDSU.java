package unbosque.edu.co.aulasync.motor;

public class ComponenteDSU {
	private final int[] padre, rango;

	public ComponenteDSU(int n) {
		padre = new int[n];
		rango = new int[n];
		for (int i = 0; i < n; i++)
			padre[i] = i;
	}

	public int encontrar(int x) {
		while (padre[x] != x) {
			padre[x] = padre[padre[x]];
			x = padre[x];
		}
		return x;
	}

	public void unir(int a, int b) {
		a = encontrar(a);
		b = encontrar(b);
		if (a == b)
			return;
		if (rango[a] < rango[b]) {
			int t = a;
			a = b;
			b = t;
		}
		padre[b] = a;
		if (rango[a] == rango[b])
			rango[a]++;
	}
}