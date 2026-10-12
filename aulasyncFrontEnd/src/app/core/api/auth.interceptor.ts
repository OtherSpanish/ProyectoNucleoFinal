import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor de autenticación. NO está registrado todavía (ver app.config.ts).
 * Al conectar: adjuntar el token JWT y, ante 401, cerrar sesión y volver a /login.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // TODO: const token = inject(AuthService).token();
  // TODO: if (token) req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  // TODO: manejar 401/403 con catchError.
  return next(req);
};
