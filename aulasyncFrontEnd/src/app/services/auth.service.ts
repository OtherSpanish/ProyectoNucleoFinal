import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { CredencialesModel } from '../models/credenciales.model';
import { RespuestaLoginModel } from '../models/respuesta-login.model';
import { UsuarioModel } from '../models/usuario.model';

/** Consume los endpoints de /api/auth. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/auth`;

  iniciarSesion(credenciales: CredencialesModel): Observable<RespuestaLoginModel> {
    return this.http.post<RespuestaLoginModel>(`${this.url}/login`, credenciales);
  }

  obtenerUsuarioActual(): Observable<UsuarioModel> {
    return this.http.get<UsuarioModel>(`${this.url}/yo`);
  }
}
