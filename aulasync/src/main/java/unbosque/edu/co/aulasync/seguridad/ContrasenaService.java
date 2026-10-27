package unbosque.edu.co.aulasync.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

/**
 * Cifrado y verificación de contraseñas con PBKDF2-HMAC-SHA256 y sal aleatoria
 * (solo clases del JDK, sin dependencias adicionales). Formato guardado:
 * {@code pbkdf2$iteraciones$sal$hash}. Las contraseñas antiguas en texto plano
 * se siguen aceptando para poder migrarlas al primer inicio de sesión.
 */
@Component
public class ContrasenaService {

	private static final String PREFIJO = "pbkdf2$";
	private static final int ITERACIONES = 120_000;
	private static final int BYTES_SAL = 16;
	private static final int BITS_CLAVE = 256;

	private final SecureRandom azar = new SecureRandom();

	/**
	 * Cifra una contraseña con una sal nueva.
	 *
	 * @param plana contraseña en texto plano
	 * @return texto cifrado listo para guardar
	 */
	public String cifrar(String plana) {
		byte[] sal = new byte[BYTES_SAL];
		azar.nextBytes(sal);
		byte[] hash = derivar(plana, sal, ITERACIONES);
		Base64.Encoder b64 = Base64.getEncoder();
		return PREFIJO + ITERACIONES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
	}

	/**
	 * Indica si el valor guardado ya está cifrado.
	 *
	 * @param guardada valor de la columna password
	 * @return {@code true} si tiene el formato cifrado
	 */
	public boolean estaCifrada(String guardada) {
		return guardada != null && guardada.startsWith(PREFIJO);
	}

	/**
	 * Compara una contraseña en texto plano con la guardada, en tiempo constante.
	 *
	 * @param plana    contraseña recibida
	 * @param guardada valor guardado (cifrado o, si es antiguo, texto plano)
	 * @return {@code true} si coinciden
	 */
	public boolean coincide(String plana, String guardada) {
		if (plana == null || guardada == null) {
			return false;
		}
		if (!estaCifrada(guardada)) {
			return MessageDigest.isEqual(plana.getBytes(StandardCharsets.UTF_8),
					guardada.getBytes(StandardCharsets.UTF_8));
		}
		String[] partes = guardada.split("\\$");
		if (partes.length != 4) {
			return false;
		}
		try {
			int iteraciones = Integer.parseInt(partes[1]);
			byte[] sal = Base64.getDecoder().decode(partes[2]);
			byte[] esperado = Base64.getDecoder().decode(partes[3]);
			return MessageDigest.isEqual(esperado, derivar(plana, sal, iteraciones));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private byte[] derivar(String plana, byte[] sal, int iteraciones) {
		try {
			PBEKeySpec spec = new PBEKeySpec(plana.toCharArray(), sal, iteraciones, BITS_CLAVE);
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("No se pudo cifrar la contraseña.", e);
		}
	}
}
