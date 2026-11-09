package unbosque.edu.co.aulasync.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import unbosque.edu.co.aulasync.model.Rol;

/**
 * Genera y valida los tokens JWT de sesión.
 *
 * <p>
 * El token lleva el correo del usuario como {@code subject} y su rol como
 * dato adicional. Se firma con HMAC-SHA usando
 * {@code aulasync.seguridad.secreto} (mínimo 32 caracteres) y vence a las
 * {@code aulasync.seguridad.horas-sesion} horas.
 * </p>
 *
 * @version 1.0
 */
@Component
public class JwtService {

	private static final long MILIS_POR_HORA = 3600000L;

	private final SecretKey clave;
	private final long duracionMilis;

	/**
	 * Crea el servicio con la clave y la duración configuradas.
	 *
	 * @param secreto      secreto de firma (mínimo 32 caracteres).
	 * @param horasSesion  horas de validez de cada token.
	 */
	public JwtService(@Value("${aulasync.seguridad.secreto}") String secreto,
			@Value("${aulasync.seguridad.horas-sesion:8}") long horasSesion) {
		this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
		this.duracionMilis = horasSesion * MILIS_POR_HORA;
	}

	/**
	 * Genera un token firmado para el usuario.
	 *
	 * @param correo correo del usuario (subject del token).
	 * @param rol    rol del usuario.
	 * @return token JWT compacto.
	 */
	public String generarToken(String correo, Rol rol) {
		Date ahora = new Date();
		Date vence = new Date(ahora.getTime() + duracionMilis);
		return Jwts.builder()
				.subject(correo)
				.claim("rol", rol.name())
				.issuedAt(ahora)
				.expiration(vence)
				.signWith(clave)
				.compact();
	}

	/**
	 * Valida el token y extrae el correo del usuario.
	 *
	 * @param token token JWT recibido en la cabecera.
	 * @return el correo si el token es válido y no ha vencido; {@code null} en
	 *         cualquier otro caso.
	 */
	public String extraerCorreo(String token) {
		try {
			Claims datos = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
			return datos.getSubject();
		} catch (JwtException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * @return duración de cada token, en milisegundos.
	 */
	public long getDuracionMilis() {
		return duracionMilis;
	}
}
