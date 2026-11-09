import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { API_BASE_URL } from './api/api.config';
import { AuthService } from './auth.service';

/**
 * - Adjunta `Authorization: Bearer <token>` a todas las peticiones hacia el back.
 * - Si el back responde 401 (token inválido o vencido) en una ruta distinta del login, cierra la sesión.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(API_BASE_URL)) return next(req);

  const auth = inject(AuthService);
  const esLogin = req.url === `${API_BASE_URL}/auth/login`;
  const token = esLogin ? null : auth.token;
  const peticion = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(peticion).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse && err.status === 401 && !esLogin && auth.autenticado()) {
        auth.logout('Tu sesión expiró. Vuelve a iniciar sesión.');
      }
      return throwError(() => err);
    }),
  );
};
