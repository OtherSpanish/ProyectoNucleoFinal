import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Curso, DisponibilidadDocente, Docente, FranjaDocente, Grupo, InscritosGrupo } from '../models';
import { API_BASE_URL } from './api.config';

/** /api/docentes — leer: ADMIN y COMITE; escribir: ADMIN. */
@Injectable({ providedIn: 'root' })
export class DocentesApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/docentes`;

  listar(especialidadId?: number | null): Observable<Docente[]> {
    return this.http.get<Docente[]>(this.url, { params: especialidadId ? { especialidadId } : {} });
  }
  crear(d: Docente): Observable<Docente> { return this.http.post<Docente>(this.url, d); }
  actualizar(id: number, d: Docente): Observable<Docente> { return this.http.put<Docente>(`${this.url}/${id}`, d); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }

  /** GET: ADMIN y COMITE. */
  disponibilidad(docenteId: number): Observable<DisponibilidadDocente> {
    return this.http.get<DisponibilidadDocente>(`${this.url}/${docenteId}/disponibilidad`);
  }
  /** PUT reemplaza TODAS las franjas del docente (solo ADMIN). */
  guardarDisponibilidad(docenteId: number, franjas: FranjaDocente[]): Observable<DisponibilidadDocente> {
    return this.http.put<DisponibilidadDocente>(`${this.url}/${docenteId}/disponibilidad`, franjas);
  }
}

/** /api/cursos — leer: cualquier autenticado; escribir: COMITE. */
@Injectable({ providedIn: 'root' })
export class CursosApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/cursos`;

  listar(f: { semestreId?: number | null; especialidadId?: number | null } = {}): Observable<Curso[]> {
    let params = new HttpParams();
    if (f.semestreId) params = params.set('semestreId', f.semestreId);
    if (f.especialidadId) params = params.set('especialidadId', f.especialidadId);
    return this.http.get<Curso[]>(this.url, { params });
  }
  crear(c: Curso): Observable<Curso> { return this.http.post<Curso>(this.url, c); }
  actualizar(id: number, c: Curso): Observable<Curso> { return this.http.put<Curso>(`${this.url}/${id}`, c); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}

/** /api/grupos — leer: ADMIN y COMITE; escribir: COMITE. Inscripciones: ADMIN y COMITE. */
@Injectable({ providedIn: 'root' })
export class GruposApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/grupos`;

  listar(f: { cursoId?: number | null; docenteId?: number | null; semestreId?: number | null } = {}): Observable<Grupo[]> {
    let params = new HttpParams();
    if (f.cursoId) params = params.set('cursoId', f.cursoId);
    if (f.docenteId) params = params.set('docenteId', f.docenteId);
    if (f.semestreId) params = params.set('semestreId', f.semestreId);
    return this.http.get<Grupo[]>(this.url, { params });
  }
  crear(g: Grupo): Observable<Grupo> { return this.http.post<Grupo>(this.url, g); }
  actualizar(id: number, g: Grupo): Observable<Grupo> { return this.http.put<Grupo>(`${this.url}/${id}`, g); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }

  inscritos(grupoId: number): Observable<InscritosGrupo> {
    return this.http.get<InscritosGrupo>(`${this.url}/${grupoId}/estudiantes`);
  }
  inscribir(grupoId: number, estudianteId: number): Observable<InscritosGrupo> {
    return this.http.post<InscritosGrupo>(`${this.url}/${grupoId}/estudiantes/${estudianteId}`, null);
  }
  retirar(grupoId: number, estudianteId: number): Observable<InscritosGrupo> {
    return this.http.delete<InscritosGrupo>(`${this.url}/${grupoId}/estudiantes/${estudianteId}`);
  }
}
