import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL, ENDPOINTS, pendiente } from './api.config';
import { ConfiguracionDto } from './dto';

/** Cliente de ParametrizacionController (CU03, RNF3). NO conectado todavía. */
@Injectable({ providedIn: 'root' })
export class ParametrizacionApi {
  private http = inject(HttpClient);
  private base = API_BASE_URL;

  obtener(): Observable<ConfiguracionDto> {
    return pendiente('obtener', ENDPOINTS.parametrizacion);
  }

  guardar(config: ConfiguracionDto): Observable<ConfiguracionDto> {
    return pendiente('guardar', ENDPOINTS.parametrizacion);
  }
}
