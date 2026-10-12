import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegistroAuditoria } from '../models';
import { API_BASE_URL, ENDPOINTS, pendiente } from './api.config';

/**
 * Cliente de AuditoriaController (CU10, RF8, RF11, RNF8). NO conectado todavía.
 * Solo lectura: el registro lo genera el backend al confirmar cada movimiento (CU11),
 * por eso no hay método para crear, editar ni borrar.
 */
@Injectable({ providedIn: 'root' })
export class AuditoriaApi {
  private http = inject(HttpClient);
  private base = API_BASE_URL;

  /** Sin `sesionId` devuelve el log general; con `sesionId`, el historial de esa sesión en orden cronológico. */
  historial(sesionId?: string): Observable<RegistroAuditoria[]> {
    return pendiente('historial', ENDPOINTS.auditoria);
  }
}
