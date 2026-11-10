import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { FranjaSedeModel } from '../models/franja-sede.model';
import { SedeModel } from '../models/sede.model';

/** Consume los endpoints de /api/sedes. */
@Injectable({ providedIn: 'root' })
export class SedeService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/sedes`;

  listar(): Observable<SedeModel[]> {
    return this.http.get<SedeModel[]>(this.url);
  }

  reemplazarFranjas(id: number, franjas: FranjaSedeModel[]): Observable<SedeModel> {
    return this.http.put<SedeModel>(`${this.url}/${id}/franjas`, franjas);
  }

  buscarPorId(id: number): Observable<SedeModel> {
    return this.http.get<SedeModel>(`${this.url}/${id}`);
  }

  crear(entidad: SedeModel): Observable<SedeModel> {
    return this.http.post<SedeModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: SedeModel): Observable<SedeModel> {
    return this.http.put<SedeModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
