import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { EspecialidadModel } from '../models/especialidad.model';

/** Consume los endpoints de /api/especialidades. */
@Injectable({ providedIn: 'root' })
export class EspecialidadService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/especialidades`;

  listar(): Observable<EspecialidadModel[]> {
    return this.http.get<EspecialidadModel[]>(this.url);
  }

  buscarPorId(id: number): Observable<EspecialidadModel> {
    return this.http.get<EspecialidadModel>(`${this.url}/${id}`);
  }

  crear(entidad: EspecialidadModel): Observable<EspecialidadModel> {
    return this.http.post<EspecialidadModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: EspecialidadModel): Observable<EspecialidadModel> {
    return this.http.put<EspecialidadModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
