import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { SesionService } from '../services/sesion.service';

/** Deja pasar solo si hay sesión iniciada; si no, lleva al login. */
export const autenticacionGuard: CanActivateFn = () => {
  const sesionService = inject(SesionService);
  if (sesionService.estaAutenticado()) {
    return true;
  }
  return inject(Router).createUrlTree(['/login']);
};
