package unbosque.edu.co.aulasync.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Reenvía los errores de autenticación (401) y autorización (403) que ocurren
 * en los filtros de seguridad hacia {@code LanzadorException}.
 *
 * <p>
 * Los filtros se ejecutan antes de los controladores, así que sus excepciones
 * no llegan solas al {@code @RestControllerAdvice}. Esta clase las delega al
 * {@link HandlerExceptionResolver} de Spring MVC para que la respuesta JSON
 * salga del mismo lugar que el resto de errores.
 * </p>
 *
 * @version 1.0
 */
@Component
public class ManejadorErroresSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final HandlerExceptionResolver resolver;

	/**
	 * Crea el manejador.
	 *
	 * @param resolver resolutor de excepciones de Spring MVC.
	 */
	public ManejadorErroresSeguridad(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * Se invoca cuando una ruta protegida recibe una petición sin autenticación
	 * válida.
	 *
	 * @param request  petición HTTP.
	 * @param response respuesta HTTP.
	 * @param ex       excepción de autenticación.
	 * @throws IOException nunca en la práctica; lo exige la interfaz.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException {
		resolver.resolveException(request, response, null, ex);
	}

	/**
	 * Se invoca cuando un usuario autenticado intenta algo que su rol no
	 * permite.
	 *
	 * @param request  petición HTTP.
	 * @param response respuesta HTTP.
	 * @param ex       excepción de acceso denegado.
	 * @throws IOException nunca en la práctica; lo exige la interfaz.
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
			throws IOException {
		resolver.resolveException(request, response, null, ex);
	}
}
