import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { crearParametros } from '../core/utils/parametros-http.util';
import { DocenteModel } from '../models/docente.model';

/** Consume los endpoints de /api/docentes. */
@Injectable({ providedIn: 'root' })
export class DocenteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/docentes`;

  listar(especialidadId: number | null = null): Observable<DocenteModel[]> {
    return this.http.get<DocenteModel[]>(this.url, { params: crearParametros({ especialidadId }) });
  }

  buscarPorPrefijo(prefijo: string): Observable<DocenteModel[]> {
    return this.http.get<DocenteModel[]>(`${this.url}/buscar`, { params: crearParametros({ prefijo }) });
  }

  buscarPorId(id: number): Observable<DocenteModel> {
    return this.http.get<DocenteModel>(`${this.url}/${id}`);
  }

  crear(entidad: DocenteModel): Observable<DocenteModel> {
    return this.http.post<DocenteModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: DocenteModel): Observable<DocenteModel> {
    return this.http.put<DocenteModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
