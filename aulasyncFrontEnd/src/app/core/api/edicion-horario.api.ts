import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sesion } from '../models';
import { API_BASE_URL, ENDPOINTS, pendiente } from './api.config';
import { CambiarAulaRequest, MoverSesionRequest, MoverSesionResponse, ValidacionDto, ValidarMovimientoRequest } from './dto';

/**
 * Cliente de EdicionHorarioController (CU06, CU07, RF4, RF5). NO conectado todavía.
 * Reemplaza `validar`, `mover`, `cambiarAula` y `retirar` de `ScheduleService`.
 * Meta de rendimiento: la validación debe responder en hasta 1 s (RNF6).
 */
@Injectable({ providedIn: 'root' })
export class EdicionHorarioApi {
  private http = inject(HttpClient);
  private base = API_BASE_URL;
  private e = ENDPOINTS.edicion;

  validarMovimiento(req: ValidarMovimientoRequest): Observable<ValidacionDto> {
    return pendiente('validarMovimiento', this.e.validar);
  }

  moverSesion(req: MoverSesionRequest): Observable<MoverSesionResponse> {
    return pendiente('moverSesion', this.e.mover);
  }

  cambiarAula(req: CambiarAulaRequest): Observable<MoverSesionResponse> {
    return pendiente('cambiarAula', this.e.cambiarAula);
  }

  retirarSesion(sesionId: string): Observable<Sesion> {
    return pendiente('retirarSesion', this.e.retirar);
  }
}
