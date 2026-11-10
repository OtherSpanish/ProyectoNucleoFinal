import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { crearParametros } from '../core/utils/parametros-http.util';
import { CalendarioSemanalModel } from '../models/calendario-semanal.model';
import { FiltroHorarioModel } from '../models/filtro-horario.model';
import { ResultadoGeneracionModel } from '../models/resultado-generacion.model';
import { ResultadoSincronizacionModel } from '../models/resultado-sincronizacion.model';
import { SesionHorarioModel } from '../models/sesion-horario.model';

/** Consume los endpoints de /api/horarios (generar, sincronizar y consultar). */
@Injectable({ providedIn: 'root' })
export class HorarioService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/horarios`;

  generar(): Observable<ResultadoGeneracionModel> {
    return this.http.post<ResultadoGeneracionModel>(`${this.url}/generar`, null);
  }

  sincronizar(): Observable<ResultadoSincronizacionModel> {
    return this.http.post<ResultadoSincronizacionModel>(`${this.url}/sincronizar`, null);
  }

  listar(filtros: FiltroHorarioModel): Observable<SesionHorarioModel[]> {
    return this.http.get<SesionHorarioModel[]>(this.url, { params: crearParametros(filtros) });
  }

  calendario(semestre: number | null, prefijo: string | null): Observable<CalendarioSemanalModel> {
    return this.http.get<CalendarioSemanalModel>(`${this.url}/calendario`, {
      params: crearParametros({ semestre, prefijo }),
    });
  }
}
