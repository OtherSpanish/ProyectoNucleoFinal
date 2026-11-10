import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { EstudianteModel } from '../models/estudiante.model';
import { InscritosGrupoModel } from '../models/inscritos-grupo.model';

/** Consume los endpoints de /api/grupos/{grupoId}/estudiantes. */
@Injectable({ providedIn: 'root' })
export class InscripcionService {
  private readonly http = inject(HttpClient);

  obtener(grupoId: number): Observable<InscritosGrupoModel> {
    return this.http.get<InscritosGrupoModel>(this.url(grupoId));
  }

  reemplazar(grupoId: number, estudiantes: EstudianteModel[]): Observable<InscritosGrupoModel> {
    return this.http.put<InscritosGrupoModel>(this.url(grupoId), estudiantes);
  }

  agregar(grupoId: number, estudianteId: number): Observable<InscritosGrupoModel> {
    return this.http.post<InscritosGrupoModel>(`${this.url(grupoId)}/${estudianteId}`, null);
  }

  quitar(grupoId: number, estudianteId: number): Observable<InscritosGrupoModel> {
    return this.http.delete<InscritosGrupoModel>(`${this.url(grupoId)}/${estudianteId}`);
  }

  private url(grupoId: number): string {
    return `${environment.apiUrl}/grupos/${grupoId}/estudiantes`;
  }
}
