package unbosque.edu.co.aulasync.model;

import java.util.List;

import unbosque.edu.co.aulasync.motor.MotivoRechazo;

/**
 * Objeto de valor con el resultado de validar un movimiento manual de una
 * sesión en el editor del horario.
 *
 * <p>
 * A diferencia del motor, que se detiene en el primer motivo, aquí se
 * reportan todos los motivos que fallan, para que el comité vea de una vez
 * qué tiene que resolver.
 * </p>
 *
 * @version 1.0
 */
public class ValidacionMovimiento {

	private final Long sesionId;
	private final boolean valido;
	private final List<MotivoRechazo> motivos;
	private final List<String> mensajes;

	/**
	 * Crea el resultado de la validación.
	 *
	 * @param sesionId id de la sesión que se quiere mover.
	 * @param motivos  motivos que fallan (vacío si es válido).
	 * @param mensajes explicación legible de cada motivo, en el mismo orden.
	 */
	public ValidacionMovimiento(Long sesionId, List<MotivoRechazo> motivos, List<String> mensajes) {
		this.sesionId = sesionId;
		this.motivos = motivos;
		this.mensajes = mensajes;
		this.valido = motivos.isEmpty();
	}

	/**
	 * @return id de la sesión.
	 */
	public Long getSesionId() {
		return sesionId;
	}

	/**
	 * @return {@code true} si el movimiento cumple todas las reglas.
	 */
	public boolean isValido() {
		return valido;
	}

	/**
	 * @return motivos que fallan.
	 */
	public List<MotivoRechazo> getMotivos() {
		return motivos;
	}

	/**
	 * @return explicación legible de cada motivo.
	 */
	public List<String> getMensajes() {
		return mensajes;
	}
}
