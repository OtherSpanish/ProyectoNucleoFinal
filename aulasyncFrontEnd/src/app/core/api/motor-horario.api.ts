import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sesion } from '../models';
import { API_BASE_URL, ENDPOINTS, pendiente } from './api.config';
import { GenerarPropuestaResponse } from './dto';

/**
 * Cliente de MotorHorarioController (CU04, RF2). NO conectado todavía.
 * Reemplaza la heurística simulada de `ScheduleService.generarPropuesta()` (grafo de conflictos + DSatur en el backend).
 */
@Injectable({ providedIn: 'root' })
export class MotorHorarioApi {
  private http = inject(HttpClient);
  private base = API_BASE_URL;

  generarPropuesta(): Observable<GenerarPropuestaResponse> {
    return pendiente('generarPropuesta', ENDPOINTS.motor.generar);
  }

  obtenerSinUbicar(): Observable<Sesion[]> {
    return pendiente('obtenerSinUbicar', ENDPOINTS.motor.sinUbicar);
  }
}
