import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Rol } from './models';
import { rutaInicial } from './menu';

/** Solo usuarios con sesión vigente. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.token ? true : router.createUrlTree(['/login']);
};

/** Acceso por rol: lee `data: { roles: [...] }` de la ruta (el back valida lo mismo con @PreAuthorize). */
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const roles = route.data['roles'] as Rol[] | undefined;
  return !roles || auth.tieneRol(roles) ? true : router.createUrlTree([rutaInicial(auth.rol())]);
};
