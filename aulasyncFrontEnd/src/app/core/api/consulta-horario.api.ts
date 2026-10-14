import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sesion } from '../models';
import { API_BASE_URL, ENDPOINTS, pendiente } from './api.config';
import { FormatoExportacion } from './dto';

/** Cliente de ConsultaHorarioController (CU05, CU08, CU09, RF3, RF9). NO conectado todavía. */
@Injectable({ providedIn: 'root' })
export class ConsultaHorarioApi {
  private http = inject(HttpClient);
  private base = API_BASE_URL;
  private e = ENDPOINTS.consulta;

  obtenerHorario(): Observable<Sesion[]> {
    return pendiente('obtenerHorario', this.e.horario);
  }

  porCurso(cursoId: string): Observable<Sesion[]> {
    return pendiente('porCurso', `${this.e.porCurso}/${cursoId}`);
  }

  porDocente(docenteId: string): Observable<Sesion[]> {
    return pendiente('porDocente', `${this.e.porDocente}/${docenteId}`);
  }

  /** Devuelve el archivo; usar `responseType: 'blob'` al implementarlo. */
  exportar(formato: FormatoExportacion, filtro?: { cursoId?: string; docenteId?: string }): Observable<Blob> {
    return pendiente('exportar', this.e.exportar);
  }
}
