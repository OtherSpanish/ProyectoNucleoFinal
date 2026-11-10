import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { RUTA_INICIO_POR_ROL } from '../constants/navegacion.constants';
import { SesionService } from '../services/sesion.service';

/** Redirige la raíz a la pantalla principal del rol del usuario. */
export const inicioGuard: CanActivateFn = () => {
  const rol = inject(SesionService).rol();
  const router = inject(Router);
  if (rol === null) {
    return router.createUrlTree(['/login']);
  }
  return router.parseUrl(RUTA_INICIO_POR_ROL[rol]);
};
