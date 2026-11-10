import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { crearParametros } from '../core/utils/parametros-http.util';
import { FiltroAulaModel } from '../models/filtro-aula.model';
import { AulaModel } from '../models/aula.model';

/** Consume los endpoints de /api/aulas. */
@Injectable({ providedIn: 'root' })
export class AulaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/aulas`;

  listar(filtro: FiltroAulaModel | null = null): Observable<AulaModel[]> {
    return this.http.get<AulaModel[]>(this.url, { params: crearParametros(filtro ?? {}) });
  }

  buscarPorId(id: number): Observable<AulaModel> {
    return this.http.get<AulaModel>(`${this.url}/${id}`);
  }

  crear(entidad: AulaModel): Observable<AulaModel> {
    return this.http.post<AulaModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: AulaModel): Observable<AulaModel> {
    return this.http.put<AulaModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
