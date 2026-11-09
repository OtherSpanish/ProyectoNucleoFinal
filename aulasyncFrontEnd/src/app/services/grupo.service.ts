import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { crearParametros } from '../core/utils/parametros-http.util';
import { FiltroGrupoModel } from '../models/filtro-grupo.model';
import { GrupoModel } from '../models/grupo.model';

/** Consume los endpoints de /api/grupos. */
@Injectable({ providedIn: 'root' })
export class GrupoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/grupos`;

  listar(filtro: FiltroGrupoModel | null = null): Observable<GrupoModel[]> {
    return this.http.get<GrupoModel[]>(this.url, { params: crearParametros(filtro ?? {}) });
  }

  buscarPorId(id: number): Observable<GrupoModel> {
    return this.http.get<GrupoModel>(`${this.url}/${id}`);
  }

  crear(entidad: GrupoModel): Observable<GrupoModel> {
    return this.http.post<GrupoModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: GrupoModel): Observable<GrupoModel> {
    return this.http.put<GrupoModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
