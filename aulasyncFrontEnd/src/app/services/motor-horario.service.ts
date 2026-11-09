import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { ResultadoGeneracionModel } from '../models/resultado-generacion.model';

/** Consume /api/motor: propuesta de horario sin guardar (vista previa). */
@Injectable({ providedIn: 'root' })
export class MotorHorarioService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/motor`;

  generarVistaPrevia(): Observable<ResultadoGeneracionModel> {
    return this.http.post<ResultadoGeneracionModel>(`${this.url}/generar`, null);
  }
}
