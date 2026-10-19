package unbosque.edu.co.aulasync.dto;

import java.util.List;

/**
 * DTO de respuesta del endpoint que genera el horario con los datos persistidos
 * ({@code POST /api/motor/generar}).
 *
 * <p>
 * Separa las sesiones que el motor ubicó de las que no pudo ubicar, de modo que
 * el front pueda pintar el calendario con las primeras y listar las segundas
 * con su causa para que el Comité Táctico las resuelva manualmente.
 * </p>
 */
public class RespuestaGeneracionDto {

	private final List<SesionGeneradaDto> ubicadas;
	private final List<SesionGeneradaDto> sinUbicar;

	/**
	 * Crea la respuesta de una generación de horario.
	 *
	 * @param ubicadas  sesiones con franja y aula asignadas
	 * @param sinUbicar sesiones que no se pudieron ubicar, con su causa y desglose
	 */
	public RespuestaGeneracionDto(List<SesionGeneradaDto> ubicadas, List<SesionGeneradaDto> sinUbicar) {
		this.ubicadas = ubicadas;
		this.sinUbicar = sinUbicar;
	}

	/** @return sesiones ubicadas por el motor */
	public List<SesionGeneradaDto> getUbicadas() {
		return ubicadas;
	}

	/** @return sesiones que el motor no pudo ubicar */
	public List<SesionGeneradaDto> getSinUbicar() {
		return sinUbicar;
	}
}