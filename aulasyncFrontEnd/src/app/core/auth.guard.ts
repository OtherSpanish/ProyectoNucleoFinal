import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { RolTecnico } from './models';

/** Solo usuarios autenticados (CA10). */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.autenticado() ? true : router.createUrlTree(['/login']);
};

/** Acceso por rol: lee `data: { roles: [...] }` de la ruta (CA10). */
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const roles = route.data['roles'] as RolTecnico[] | undefined;
  return !roles || auth.tieneRol(roles) ? true : router.createUrlTree(['/inicio']);
};
