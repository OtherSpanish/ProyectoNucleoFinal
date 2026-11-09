import { Injectable, computed, signal } from '@angular/core';

import { RespuestaLoginModel } from '../../models/respuesta-login.model';
import { UsuarioModel } from '../../models/usuario.model';
import { CLAVE_TOKEN, CLAVE_USUARIO } from '../constants/almacenamiento.constants';

/** Guarda el token JWT y el usuario autenticado en el navegador. */
@Injectable({ providedIn: 'root' })
export class SesionService {
  private readonly usuarioActual = signal<UsuarioModel | null>(this.leerUsuario());
  private tokenActual: string | null = this.leerToken();

  readonly usuario = this.usuarioActual.asReadonly();
  readonly rol = computed(() => this.usuarioActual()?.rol ?? null);

  iniciar(respuesta: RespuestaLoginModel): void {
    this.tokenActual = respuesta.token;
    this.escribir(CLAVE_TOKEN, respuesta.token);
    this.actualizarUsuario(respuesta.usuario);
  }

  actualizarUsuario(usuario: UsuarioModel): void {
    this.escribir(CLAVE_USUARIO, JSON.stringify(usuario));
    this.usuarioActual.set(usuario);
  }

  cerrar(): void {
    this.tokenActual = null;
    this.borrar(CLAVE_TOKEN);
    this.borrar(CLAVE_USUARIO);
    this.usuarioActual.set(null);
  }

  token(): string | null {
    return this.tokenActual;
  }

  estaAutenticado(): boolean {
    return this.tokenActual !== null && this.usuarioActual() !== null;
  }

  private leerToken(): string | null {
    try {
      return localStorage.getItem(CLAVE_TOKEN);
    } catch {
      return null;
    }
  }

  private leerUsuario(): UsuarioModel | null {
    try {
      const texto = localStorage.getItem(CLAVE_USUARIO);
      return texto ? (JSON.parse(texto) as UsuarioModel) : null;
    } catch {
      return null;
    }
  }

  private escribir(clave: string, valor: string): void {
    try {
      localStorage.setItem(clave, valor);
    } catch {
      // El navegador no permite almacenamiento: la sesión vive solo en memoria.
    }
  }

  private borrar(clave: string): void {
    try {
      localStorage.removeItem(clave);
    } catch {
      // Nada que borrar.
    }
  }
}
