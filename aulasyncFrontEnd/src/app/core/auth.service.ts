import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthApi } from './api/auth.api';
import { RespuestaLogin, Rol, Usuario } from './models';

interface Sesion {
  token: string;
  usuario: Usuario;
  /** Instante (ms epoch) en que vence el token. */
  expira: number;
}

/** Autenticación real contra el back: JWT en `Authorization: Bearer` (ver auth.interceptor.ts). */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = inject(AuthApi);
  private router = inject(Router);
  private readonly CLAVE = 'aulasync_sesion';

  private readonly _sesion = signal<Sesion | null>(this.restaurar());
  readonly usuario = computed(() => this._sesion()?.usuario ?? null);
  readonly rol = computed<Rol | null>(() => this._sesion()?.usuario.rol ?? null);
  readonly autenticado = computed(() => this._sesion() !== null);

  /** Mensaje que se muestra en /login (por ejemplo, «tu sesión expiró»). */
  readonly aviso = signal('');

  get token(): string | null {
    const s = this._sesion();
    if (!s) return null;
    if (Date.now() >= s.expira) {
      this.cerrar();
      return null;
    }
    return s.token;
  }

  login(correo: string, password: string): Observable<RespuestaLogin> {
    return this.api.login(correo.trim(), password).pipe(
      tap((r) => {
        const s: Sesion = { token: r.token, usuario: r.usuario, expira: Date.now() + r.expiraEnSegundos * 1000 };
        this._sesion.set(s);
        this.aviso.set('');
        this.guardar(s);
      }),
    );
  }

  /** Vuelve a leer los datos del usuario (por si un administrador le cambió el rol o el nombre). */
  refrescar(): void {
    if (!this.autenticado()) return;
    this.api.yo().subscribe({
      next: (u) => {
        const s = this._sesion();
        if (!s) return;
        const nueva = { ...s, usuario: u };
        this._sesion.set(nueva);
        this.guardar(nueva);
      },
      error: () => { /* el interceptor ya maneja el 401 */ },
    });
  }

  logout(aviso = ''): void {
    this.cerrar();
    this.aviso.set(aviso);
    this.router.navigate(['/login']);
  }

  tieneRol(roles: Rol[]): boolean {
    const r = this.rol();
    return !!r && roles.includes(r);
  }

  private cerrar(): void {
    this._sesion.set(null);
    try { sessionStorage.removeItem(this.CLAVE); } catch { /* sin storage */ }
  }

  private guardar(s: Sesion): void {
    try { sessionStorage.setItem(this.CLAVE, JSON.stringify(s)); } catch { /* sin storage */ }
  }

  private restaurar(): Sesion | null {
    try {
      const txt = sessionStorage.getItem(this.CLAVE);
      if (!txt) return null;
      const s = JSON.parse(txt) as Sesion;
      if (!s?.token || !s.usuario || Date.now() >= s.expira) {
        sessionStorage.removeItem(this.CLAVE);
        return null;
      }
      return s;
    } catch {
      return null;
    }
  }
}
