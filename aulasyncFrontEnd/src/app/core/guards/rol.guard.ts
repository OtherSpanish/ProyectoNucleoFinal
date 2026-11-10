import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { SesionService } from '../services/sesion.service';

/** Deja pasar si el rol del usuario está en data.roles de la ruta. */
export const rolGuard: CanActivateFn = (ruta: ActivatedRouteSnapshot) => {
  const rol = inject(SesionService).rol();
  const permitidos = ruta.data['roles'] as string[];
  if (rol !== null && permitidos.includes(rol)) {
    return true;
  }
  return inject(Router).createUrlTree(['/']);
};
