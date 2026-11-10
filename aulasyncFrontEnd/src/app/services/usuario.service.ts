import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { crearParametros } from '../core/utils/parametros-http.util';
import { UsuarioModel } from '../models/usuario.model';

/** Consume los endpoints de /api/usuarios. */
@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/usuarios`;

  listar(rol: string | null = null): Observable<UsuarioModel[]> {
    return this.http.get<UsuarioModel[]>(this.url, { params: crearParametros({ rol }) });
  }

  buscarPorId(id: number): Observable<UsuarioModel> {
    return this.http.get<UsuarioModel>(`${this.url}/${id}`);
  }

  crear(entidad: UsuarioModel): Observable<UsuarioModel> {
    return this.http.post<UsuarioModel>(this.url, entidad);
  }

  actualizar(id: number, entidad: UsuarioModel): Observable<UsuarioModel> {
    return this.http.put<UsuarioModel>(`${this.url}/${id}`, entidad);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
