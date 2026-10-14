import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Aula, Curso, Docente, FranjaDisponible, GrupoEstudiantes, Seccion } from '../models';
import { API_BASE_URL, ENDPOINTS, pendiente } from './api.config';

/** Cliente de DatosMaestrosController (CU01, CU02, RF1, RF6). NO conectado todavía. */
@Injectable({ providedIn: 'root' })
export class DatosMaestrosApi {
  private http = inject(HttpClient);
  private base = API_BASE_URL;
  private e = ENDPOINTS.datosMaestros;

  // Cursos
  listarCursos(): Observable<Curso[]> { return pendiente('listarCursos', this.e.cursos); }
  crearCurso(c: Omit<Curso, 'id'>): Observable<Curso> { return pendiente('crearCurso', this.e.cursos); }
  eliminarCurso(id: string): Observable<void> { return pendiente('eliminarCurso', `${this.e.cursos}/${id}`); }

  // Secciones
  listarSecciones(): Observable<Seccion[]> { return pendiente('listarSecciones', this.e.secciones); }
  crearSeccion(s: Omit<Seccion, 'id' | 'numero'>): Observable<Seccion> { return pendiente('crearSeccion', this.e.secciones); }
  eliminarSeccion(id: string): Observable<void> { return pendiente('eliminarSeccion', `${this.e.secciones}/${id}`); }

  // Docentes y disponibilidad
  listarDocentes(): Observable<Docente[]> { return pendiente('listarDocentes', this.e.docentes); }
  crearDocente(d: Pick<Docente, 'nombre' | 'dedicacion'>): Observable<Docente> { return pendiente('crearDocente', this.e.docentes); }
  actualizarDocente(id: string, cambios: Partial<Pick<Docente, 'nombre' | 'dedicacion'>>): Observable<Docente> {
    return pendiente('actualizarDocente', `${this.e.docentes}/${id}`);
  }
  guardarDisponibilidad(docenteId: string, franjas: FranjaDisponible[]): Observable<Docente> {
    return pendiente('guardarDisponibilidad', `${this.e.docentes}/${docenteId}/disponibilidad`);
  }
  eliminarDocente(id: string): Observable<void> { return pendiente('eliminarDocente', `${this.e.docentes}/${id}`); }

  // Aulas
  listarAulas(): Observable<Aula[]> { return pendiente('listarAulas', this.e.aulas); }
  crearAula(a: Omit<Aula, 'id'>): Observable<Aula> { return pendiente('crearAula', this.e.aulas); }
  eliminarAula(id: string): Observable<void> { return pendiente('eliminarAula', `${this.e.aulas}/${id}`); }

  // Grupos
  listarGrupos(): Observable<GrupoEstudiantes[]> { return pendiente('listarGrupos', this.e.grupos); }
  crearGrupo(g: Omit<GrupoEstudiantes, 'id'>): Observable<GrupoEstudiantes> { return pendiente('crearGrupo', this.e.grupos); }
  eliminarGrupo(id: string): Observable<void> { return pendiente('eliminarGrupo', `${this.e.grupos}/${id}`); }
}
