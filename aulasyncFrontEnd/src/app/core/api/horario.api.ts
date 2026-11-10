import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CalendarioSemanal, DisponibilidadDocente, FiltroHorario, Grupo, InscritosGrupo, ResultadoGeneracion,
  ResultadoSincronizacion, SesionHorario, ValidacionMovimiento, Dia,
} from '../models';
import { API_BASE_URL } from './api.config';

/** HorarioController, MotorHorarioController y EdicionHorarioController. */
@Injectable({ providedIn: 'root' })
export class HorarioApi {
  private http = inject(HttpClient);

  /** POST /api/motor/generar (COMITE): propone un horario SIN guardarlo. */
  previsualizar(): Observable<ResultadoGeneracion> {
    return this.http.post<ResultadoGeneracion>(`${API_BASE_URL}/motor/generar`, null);
  }

  /** POST /api/horarios/generar (COMITE): genera y REEMPLAZA el horario guardado. */
  generarYGuardar(): Observable<ResultadoGeneracion> {
    return this.http.post<ResultadoGeneracion>(`${API_BASE_URL}/horarios/generar`, null);
  }

  /** POST /api/horarios/sincronizar (COMITE): ajusta el horario guardado a los datos maestros actuales. */
  sincronizar(): Observable<ResultadoSincronizacion> {
    return this.http.post<ResultadoSincronizacion>(`${API_BASE_URL}/horarios/sincronizar`, null);
  }

  /** GET /api/horarios (cualquier autenticado). */
  listar(f: FiltroHorario = {}): Observable<SesionHorario[]> {
    let params = new HttpParams();
    (Object.keys(f) as (keyof FiltroHorario)[]).forEach((k) => {
      const v = f[k];
      if (v !== null && v !== undefined && v !== false) params = params.set(k, String(v));
    });
    return this.http.get<SesionHorario[]>(`${API_BASE_URL}/horarios`, { params });
  }

  /** POST /api/horarios/sesiones/{id}/validar (COMITE): valida sin aplicar. */
  validar(sesionId: number, destino: { dia: Dia; horaInicio: number; aulaId: number }): Observable<ValidacionMovimiento> {
    return this.http.post<ValidacionMovimiento>(`${API_BASE_URL}/horarios/sesiones/${sesionId}/validar`, destino);
  }

  /** PATCH /api/horarios/sesiones/{id} (COMITE): mueve la sesión; 409 si incumple alguna regla. */
  mover(sesionId: number, destino: { dia: Dia; horaInicio: number; aulaId: number }): Observable<SesionHorario> {
    return this.http.patch<SesionHorario>(`${API_BASE_URL}/horarios/sesiones/${sesionId}`, destino);
  }

  /** DELETE /api/horarios/sesiones/{id}/ubicacion (COMITE): devuelve la sesión a «sin ubicar». */
  desubicar(sesionId: number): Observable<SesionHorario> {
    return this.http.delete<SesionHorario>(`${API_BASE_URL}/horarios/sesiones/${sesionId}/ubicacion`);
  }
}

/** PortalDocenteController (/api/mi/**, solo DOCENTE). */
@Injectable({ providedIn: 'root' })
export class PortalDocenteApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/mi`;

  cursos(): Observable<Grupo[]> { return this.http.get<Grupo[]>(`${this.url}/cursos`); }
  estudiantes(grupoId: number): Observable<InscritosGrupo> {
    return this.http.get<InscritosGrupo>(`${this.url}/grupos/${grupoId}/estudiantes`);
  }
  horario(): Observable<CalendarioSemanal> { return this.http.get<CalendarioSemanal>(`${this.url}/horario`); }
  disponibilidad(): Observable<DisponibilidadDocente> {
    return this.http.get<DisponibilidadDocente>(`${this.url}/disponibilidad`);
  }
}
