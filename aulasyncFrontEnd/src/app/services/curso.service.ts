import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { crearParametros } from '../core/utils/parametros-http.util';
import { CursoModel } from '../models/curso.model';

/** Consume los endpoints de /api/cursos. */
@Injectable({ providedIn: 'root' })
export class CursoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/cursos`;

  listar(semestreId: number | null = null, especialidadId: number | null = null): Observable<CursoModel[]> {
    return this.http.get<CursoModel[]>(this.url, { params: crearParametros({ semestreId, especialidadId }) });
  }

  buscarPorPrefijo(prefijo: string): Observable<CursoModel[]> {
    return this.http.get<CursoModel[]>(`${this.url}/buscar`, { params: crearParametros({ prefijo }) });
  }

  buscarPorId(id: number): Observable<CursoModel> {
    return this.http.get<CursoModel>(`${this.url}/${id}`);
  }

  crear(entidad: CursoModel): Observable<CursoModel> {
    return this.http.post<CursoModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: CursoModel): Observable<CursoModel> {
    return this.http.put<CursoModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
