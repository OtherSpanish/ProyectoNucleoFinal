package unbosque.edu.co.aulasync.model;

import unbosque.edu.co.aulasync.motor.MotivoRechazo;

/**
 * Objeto de valor con una línea del desglose de causas de una sesión sin
 * ubicar: un motivo, su explicación y cuántas franjas descartó.
 *
 * @version 1.0
 */
public class CausaDescartada {

	private final MotivoRechazo motivo;
	private final String descripcion;
	private final int franjas;

	/**
	 * Crea la línea del desglose.
	 *
	 * @param motivo      motivo de rechazo.
	 * @param descripcion explicación legible del motivo.
	 * @param franjas     cantidad de franjas que descartó.
	 */
	public CausaDescartada(MotivoRechazo motivo, String descripcion, int franjas) {
		this.motivo = motivo;
		this.descripcion = descripcion;
		this.franjas = franjas;
	}

	/**
	 * @return motivo de rechazo.
	 */
	public MotivoRechazo getMotivo() {
		return motivo;
	}

	/**
	 * @return explicación legible del motivo.
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @return cantidad de franjas que descartó.
	 */
	public int getFranjas() {
		return franjas;
	}
}
