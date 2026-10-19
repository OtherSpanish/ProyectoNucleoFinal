package unbosque.edu.co.aulasync.dto;

import java.util.List;

/**
 * DTO de respuesta del endpoint de demostración ({@code GET /api/motor/demo}).
 *
 * <p>
 * Agrupa el resultado del motor en dos listas: las sesiones que se lograron
 * ubicar y las que quedaron sin ubicar.
 * </p>
 */
public class RespuestaDemoDto {

	private final List<SesionDemoDto> ubicadas;
	private final List<SesionDemoDto> sinUbicar;

	/**
	 * Crea la respuesta de la demostración.
	 *
	 * @param ubicadas  sesiones a las que el motor asignó franja y aula
	 * @param sinUbicar sesiones que el motor no pudo ubicar
	 */
	public RespuestaDemoDto(List<SesionDemoDto> ubicadas, List<SesionDemoDto> sinUbicar) {
		this.ubicadas = ubicadas;
		this.sinUbicar = sinUbicar;
	}

	/** @return sesiones ubicadas por el motor */
	public List<SesionDemoDto> getUbicadas() {
		return ubicadas;
	}

	/** @return sesiones que el motor no pudo ubicar */
	public List<SesionDemoDto> getSinUbicar() {
		return sinUbicar;
	}
}