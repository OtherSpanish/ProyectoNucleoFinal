package unbosque.edu.co.aulasync.model;

import java.util.List;

import unbosque.edu.co.aulasync.dto.EstudianteDTO;
import unbosque.edu.co.aulasync.dto.GrupoDTO;

/**
 * Objeto de valor con la lista de inscritos de un grupo, lista para
 * mostrarse.
 *
 * <p>
 * No es una tabla. Trae ya calculados el mínimo para abrir el grupo, cuántos
 * faltan para alcanzarlo, el máximo permitido y cuántos cupos quedan, para que
 * el front no tenga que calcular nada.
 * </p>
 *
 * @version 1.1
 */
public class InscritosGrupo {

	private final GrupoDTO grupo;
	private final List<EstudianteDTO> estudiantes;
	private final int minimoInscritos;
	private final int faltantesParaAbrir;
	private final int maximoInscritos;
	private final int cuposDisponibles;

	/**
	 * Crea la lista de inscritos.
	 *
	 * @param grupo              datos del grupo (incluye cantidad y si está
	 *                           abierto).
	 * @param estudiantes        inscritos en orden alfabético.
	 * @param minimoInscritos    mínimo de inscritos para abrir el grupo.
	 * @param faltantesParaAbrir cuántos faltan para el mínimo (0 si ya está
	 *                           abierto).
	 * @param maximoInscritos    máximo de inscritos permitido.
	 * @param cuposDisponibles   cuántos cupos quedan hasta el máximo.
	 */
	public InscritosGrupo(GrupoDTO grupo, List<EstudianteDTO> estudiantes, int minimoInscritos,
			int faltantesParaAbrir, int maximoInscritos, int cuposDisponibles) {
		this.grupo = grupo;
		this.estudiantes = estudiantes;
		this.minimoInscritos = minimoInscritos;
		this.faltantesParaAbrir = faltantesParaAbrir;
		this.maximoInscritos = maximoInscritos;
		this.cuposDisponibles = cuposDisponibles;
	}

	/**
	 * @return datos del grupo.
	 */
	public GrupoDTO getGrupo() {
		return grupo;
	}

	/**
	 * @return inscritos en orden alfabético.
	 */
	public List<EstudianteDTO> getEstudiantes() {
		return estudiantes;
	}

	/**
	 * @return mínimo de inscritos para abrir el grupo.
	 */
	public int getMinimoInscritos() {
		return minimoInscritos;
	}

	/**
	 * @return cuántos faltan para el mínimo (0 si ya está abierto).
	 */
	public int getFaltantesParaAbrir() {
		return faltantesParaAbrir;
	}

	/**
	 * @return máximo de inscritos permitido.
	 */
	public int getMaximoInscritos() {
		return maximoInscritos;
	}

	/**
	 * @return cuántos cupos quedan hasta el máximo.
	 */
	public int getCuposDisponibles() {
		return cuposDisponibles;
	}
}
