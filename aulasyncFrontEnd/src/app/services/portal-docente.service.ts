import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { CalendarioSemanalModel } from '../models/calendario-semanal.model';
import { DisponibilidadDocenteModel } from '../models/disponibilidad-docente.model';
import { GrupoModel } from '../models/grupo.model';
import { InscritosGrupoModel } from '../models/inscritos-grupo.model';

/** Consume /api/mi: datos del docente autenticado. */
@Injectable({ providedIn: 'root' })
export class PortalDocenteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/mi`;

  misCursos(): Observable<GrupoModel[]> {
    return this.http.get<GrupoModel[]>(`${this.url}/cursos`);
  }

  estudiantesDeGrupo(grupoId: number): Observable<InscritosGrupoModel> {
    return this.http.get<InscritosGrupoModel>(`${this.url}/grupos/${grupoId}/estudiantes`);
  }

  miHorario(): Observable<CalendarioSemanalModel> {
    return this.http.get<CalendarioSemanalModel>(`${this.url}/horario`);
  }

  miDisponibilidad(): Observable<DisponibilidadDocenteModel> {
    return this.http.get<DisponibilidadDocenteModel>(`${this.url}/disponibilidad`);
  }
}
