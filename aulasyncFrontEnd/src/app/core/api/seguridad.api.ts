import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models';
import { API_BASE_URL, ENDPOINTS, pendiente } from './api.config';
import { LoginRequest, LoginResponse, UsuarioNuevoRequest, UsuarioDto } from './dto';

/** Cliente de SeguridadController (CU12, RF10, RNF7). NO conectado todavía. */
@Injectable({ providedIn: 'root' })
export class SeguridadApi {
  private http = inject(HttpClient);
  private base = API_BASE_URL;

  login(req: LoginRequest): Observable<LoginResponse> {
    // return this.http.post<LoginResponse>(this.base + ENDPOINTS.seguridad.login, req);
    return pendiente('login', ENDPOINTS.seguridad.login);
  }

  logout(): Observable<void> {
    return pendiente('logout', ENDPOINTS.seguridad.logout);
  }

  listarUsuarios(): Observable<UsuarioDto[]> {
    return pendiente('listarUsuarios', ENDPOINTS.seguridad.usuarios);
  }

  crearUsuario(req: UsuarioNuevoRequest): Observable<UsuarioDto> {
    return pendiente('crearUsuario', ENDPOINTS.seguridad.usuarios);
  }

  actualizarUsuario(id: string, cambios: Partial<Pick<Usuario, 'rol' | 'activo'>>): Observable<UsuarioDto> {
    return pendiente('actualizarUsuario', `${ENDPOINTS.seguridad.usuarios}/${id}`);
  }

  eliminarUsuario(id: string): Observable<void> {
    return pendiente('eliminarUsuario', `${ENDPOINTS.seguridad.usuarios}/${id}`);
  }
}
