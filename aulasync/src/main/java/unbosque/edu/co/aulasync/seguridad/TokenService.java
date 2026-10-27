package unbosque.edu.co.aulasync.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import unbosque.edu.co.aulasync.exception.NoAutenticadoException;

/**
 * Emite y valida tokens de sesión firmados con HMAC-SHA256 (solo clases del
 * JDK). Formato: {@code base64url(id;rol;expiración).base64url(firma)}. El
 * front lo envía en la cabecera {@code Authorization: Bearer <token>}.
 */
@Component
public class TokenService {

	private static final String MENSAJE = "Tu sesión no es válida o expiró. Inicia sesión de nuevo.";
	private static final int MIN_LONGITUD_SECRETO = 32;

	private final byte[] secreto;
	private final long segundosValidez;

	/**
	 * Crea el servicio con el secreto y la duración configurados.
	 *
	 * @param secreto    clave de firma (mínimo 32 caracteres)
	 * @param horasSesion horas de vigencia del token
	 */
	public TokenService(@Value("${aulasync.seguridad.secreto}") String secreto,
			@Value("${aulasync.seguridad.horas-sesion:8}") long horasSesion) {
		if (secreto == null || secreto.length() < MIN_LONGITUD_SECRETO) {
			throw new IllegalStateException("aulasync.seguridad.secreto debe tener al menos 32 caracteres.");
		}
		this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
		this.segundosValidez = horasSesion * 3600;
	}

	/**
	 * Emite un token para un usuario.
	 *
	 * @param id  identificador del usuario
	 * @param rol rol del usuario
	 * @return token firmado
	 */
	public String emitir(Long id, Rol rol) {
		long expira = Instant.now().getEpochSecond() + segundosValidez;
		String carga = id + ";" + rol.name() + ";" + expira;
		String cuerpo = Base64.getUrlEncoder().withoutPadding().encodeToString(carga.getBytes(StandardCharsets.UTF_8));
		return cuerpo + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(firmar(cuerpo));
	}

	/**
	 * Valida firma y vigencia de un token.
	 *
	 * @param token token recibido
	 * @return usuario y rol que declara el token
	 * @throws NoAutenticadoException si el token está mal formado, alterado o
	 *                                vencido
	 */
	public SesionUsuario validar(String token) throws NoAutenticadoException {
		if (token == null) {
			throw new NoAutenticadoException(MENSAJE);
		}
		int punto = token.indexOf('.');
		if (punto <= 0 || punto == token.length() - 1) {
			throw new NoAutenticadoException(MENSAJE);
		}
		String cuerpo = token.substring(0, punto);
		String carga;
		try {
			byte[] firma = Base64.getUrlDecoder().decode(token.substring(punto + 1));
			if (!MessageDigest.isEqual(firmar(cuerpo), firma)) {
				throw new NoAutenticadoException(MENSAJE);
			}
			carga = new String(Base64.getUrlDecoder().decode(cuerpo), StandardCharsets.UTF_8);
		} catch (IllegalArgumentException e) {
			throw new NoAutenticadoException(MENSAJE);
		}
		String[] partes = carga.split(";");
		if (partes.length != 3) {
			throw new NoAutenticadoException(MENSAJE);
		}
		try {
			Long id = Long.valueOf(partes[0]);
			Rol rol = Rol.desde(partes[1]);
			long expira = Long.parseLong(partes[2]);
			if (rol == null || expira < Instant.now().getEpochSecond()) {
				throw new NoAutenticadoException(MENSAJE);
			}
			return new SesionUsuario(id, rol);
		} catch (NumberFormatException e) {
			throw new NoAutenticadoException(MENSAJE);
		}
	}

	private byte[] firmar(String cuerpo) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secreto, "HmacSHA256"));
			return mac.doFinal(cuerpo.getBytes(StandardCharsets.UTF_8));
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("No se pudo firmar el token.", e);
		}
	}
}
