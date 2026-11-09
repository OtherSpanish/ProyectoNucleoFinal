import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { crearParametros } from '../core/utils/parametros-http.util';
import { EstudianteModel } from '../models/estudiante.model';

/** Consume los endpoints de /api/estudiantes. */
@Injectable({ providedIn: 'root' })
export class EstudianteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/estudiantes`;

  listar(): Observable<EstudianteModel[]> {
    return this.http.get<EstudianteModel[]>(this.url);
  }

  buscarPorPrefijo(prefijo: string): Observable<EstudianteModel[]> {
    return this.http.get<EstudianteModel[]>(`${this.url}/buscar`, { params: crearParametros({ prefijo }) });
  }

  buscarPorId(id: number): Observable<EstudianteModel> {
    return this.http.get<EstudianteModel>(`${this.url}/${id}`);
  }

  crear(entidad: EstudianteModel): Observable<EstudianteModel> {
    return this.http.post<EstudianteModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: EstudianteModel): Observable<EstudianteModel> {
    return this.http.put<EstudianteModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
