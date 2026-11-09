import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { DestinoSesionModel } from '../models/destino-sesion.model';
import { SesionHorarioModel } from '../models/sesion-horario.model';
import { ValidacionMovimientoModel } from '../models/validacion-movimiento.model';

/** Consume /api/horarios/sesiones: validar, mover y desubicar (drag & drop). */
@Injectable({ providedIn: 'root' })
export class EdicionHorarioService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/horarios/sesiones`;

  validar(sesionId: number, destino: DestinoSesionModel): Observable<ValidacionMovimientoModel> {
    return this.http.post<ValidacionMovimientoModel>(`${this.url}/${sesionId}/validar`, destino);
  }

  mover(sesionId: number, destino: DestinoSesionModel): Observable<SesionHorarioModel> {
    return this.http.patch<SesionHorarioModel>(`${this.url}/${sesionId}`, destino);
  }

  desubicar(sesionId: number): Observable<SesionHorarioModel> {
    return this.http.delete<SesionHorarioModel>(`${this.url}/${sesionId}/ubicacion`);
  }
}
