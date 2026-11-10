import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { SesionService } from '../services/sesion.service';

/** Evita que un usuario con sesión vuelva a ver el login. */
export const invitadoGuard: CanActivateFn = () => {
  if (inject(SesionService).estaAutenticado()) {
    return inject(Router).createUrlTree(['/']);
  }
  return true;
};
