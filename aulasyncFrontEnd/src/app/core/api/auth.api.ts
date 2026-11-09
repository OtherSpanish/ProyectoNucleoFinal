import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RespuestaLogin, Rol, Usuario } from '../models';
import { API_BASE_URL } from './api.config';

/** AuthController (/api/auth) y UsuarioController (/api/usuarios, solo ADMIN). */
@Injectable({ providedIn: 'root' })
export class AuthApi {
  private http = inject(HttpClient);

  login(correo: string, password: string): Observable<RespuestaLogin> {
    return this.http.post<RespuestaLogin>(`${API_BASE_URL}/auth/login`, { correo, password });
  }

  yo(): Observable<Usuario> {
    return this.http.get<Usuario>(`${API_BASE_URL}/auth/yo`);
  }
}

@Injectable({ providedIn: 'root' })
export class UsuariosApi {
  private http = inject(HttpClient);
  private url = `${API_BASE_URL}/usuarios`;

  listar(rol?: Rol | null): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.url, { params: rol ? { rol } : {} });
  }
  crear(u: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.url, u);
  }
  actualizar(id: number, u: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.url}/${id}`, u);
  }
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
