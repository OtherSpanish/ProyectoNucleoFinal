import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Aula, Especialidad, Estudiante, FranjaSede, Sede, Semestre, TipoAula } from '../models';
import { API_BASE_URL } from './api.config';

/** /api/especialidades — leer: ADMIN y COMITE; escribir: ADMIN. */
@Injectable({ providedIn: 'root' })
export class EspecialidadesApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/especialidades`;

  listar(): Observable<Especialidad[]> { return this.http.get<Especialidad[]>(this.url); }
  crear(e: Especialidad): Observable<Especialidad> { return this.http.post<Especialidad>(this.url, e); }
  actualizar(id: number, e: Especialidad): Observable<Especialidad> { return this.http.put<Especialidad>(`${this.url}/${id}`, e); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}

/** /api/semestres — leer: cualquier usuario autenticado; escribir: ADMIN. */
@Injectable({ providedIn: 'root' })
export class SemestresApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/semestres`;

  listar(): Observable<Semestre[]> { return this.http.get<Semestre[]>(this.url); }
  crear(s: Semestre): Observable<Semestre> { return this.http.post<Semestre>(this.url, s); }
  actualizar(id: number, s: Semestre): Observable<Semestre> { return this.http.put<Semestre>(`${this.url}/${id}`, s); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}

/** /api/sedes — leer: ADMIN y COMITE; escribir: ADMIN. Las franjas se reemplazan completas con PUT /{id}/franjas. */
@Injectable({ providedIn: 'root' })
export class SedesApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/sedes`;

  listar(): Observable<Sede[]> { return this.http.get<Sede[]>(this.url); }
  crear(s: Partial<Sede>): Observable<Sede> { return this.http.post<Sede>(this.url, s); }
  actualizar(id: number, s: Partial<Sede>): Observable<Sede> { return this.http.put<Sede>(`${this.url}/${id}`, s); }
  reemplazarFranjas(id: number, franjas: FranjaSede[]): Observable<Sede> {
    return this.http.put<Sede>(`${this.url}/${id}/franjas`, franjas);
  }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}

/** /api/aulas — leer: ADMIN y COMITE; escribir: ADMIN. */
@Injectable({ providedIn: 'root' })
export class AulasApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/aulas`;

  listar(f: { sedeId?: number | null; tipo?: TipoAula | null } = {}): Observable<Aula[]> {
    let params = new HttpParams();
    if (f.sedeId) params = params.set('sedeId', f.sedeId);
    if (f.tipo) params = params.set('tipo', f.tipo);
    return this.http.get<Aula[]>(this.url, { params });
  }
  crear(a: Aula): Observable<Aula> { return this.http.post<Aula>(this.url, a); }
  actualizar(id: number, a: Aula): Observable<Aula> { return this.http.put<Aula>(`${this.url}/${id}`, a); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}

/** /api/estudiantes — leer: ADMIN y COMITE; escribir: ADMIN. */
@Injectable({ providedIn: 'root' })
export class EstudiantesApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/estudiantes`;

  listar(): Observable<Estudiante[]> { return this.http.get<Estudiante[]>(this.url); }
  crear(e: Estudiante): Observable<Estudiante> { return this.http.post<Estudiante>(this.url, e); }
  actualizar(id: number, e: Estudiante): Observable<Estudiante> { return this.http.put<Estudiante>(`${this.url}/${id}`, e); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}
