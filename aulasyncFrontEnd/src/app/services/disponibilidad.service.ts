import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { DisponibilidadDocenteModel } from '../models/disponibilidad-docente.model';
import { FranjaDocenteModel } from '../models/franja-docente.model';

/** Consume los endpoints de /api/docentes/{docenteId}/disponibilidad. */
@Injectable({ providedIn: 'root' })
export class DisponibilidadService {
  private readonly http = inject(HttpClient);

  obtener(docenteId: number): Observable<DisponibilidadDocenteModel> {
    return this.http.get<DisponibilidadDocenteModel>(this.url(docenteId));
  }

  reemplazar(docenteId: number, franjas: FranjaDocenteModel[]): Observable<DisponibilidadDocenteModel> {
    return this.http.put<DisponibilidadDocenteModel>(this.url(docenteId), franjas);
  }

  private url(docenteId: number): string {
    return `${environment.apiUrl}/docentes/${docenteId}/disponibilidad`;
  }
}
