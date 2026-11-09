import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { SemestreModel } from '../models/semestre.model';

/** Consume los endpoints de /api/semestres. */
@Injectable({ providedIn: 'root' })
export class SemestreService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/semestres`;

  listar(): Observable<SemestreModel[]> {
    return this.http.get<SemestreModel[]>(this.url);
  }

  buscarPorId(id: number): Observable<SemestreModel> {
    return this.http.get<SemestreModel>(`${this.url}/${id}`);
  }

  crear(entidad: SemestreModel): Observable<SemestreModel> {
    return this.http.post<SemestreModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: SemestreModel): Observable<SemestreModel> {
    return this.http.put<SemestreModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
