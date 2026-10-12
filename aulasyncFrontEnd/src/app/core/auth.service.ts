import { Injectable, computed, inject, signal } from '@angular/core';
import { DataService } from './data.service';
import { RolTecnico, Usuario } from './models';

/** Autenticación simulada (RF10). Sustituir por llamada al backend (SeguridadController). */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private data = inject(DataService);
  private readonly CLAVE = 'aulasync_usuario';

  private readonly _usuario = signal<Usuario | null>(this.restaurar());
  readonly usuario = this._usuario.asReadonly();
  readonly autenticado = computed(() => this._usuario() !== null);

  login(usuario: string, clave: string): boolean {
    const u = this.data.usuarios().find((x) => x.usuario === usuario && x.clave === clave && x.activo);
    if (!u) return false;
    this._usuario.set(u);
    try { sessionStorage.setItem(this.CLAVE, u.id); } catch { /* sin storage */ }
    return true;
  }

  logout(): void {
    this._usuario.set(null);
    try { sessionStorage.removeItem(this.CLAVE); } catch { /* sin storage */ }
  }

  tieneRol(roles: RolTecnico[]): boolean {
    const u = this._usuario();
    return !!u && roles.includes(u.rol);
  }

  private restaurar(): Usuario | null {
    try {
      const id = sessionStorage.getItem(this.CLAVE);
      return this.data.usuarios().find((u) => u.id === id && u.activo) ?? null;
    } catch {
      return null;
    }
  }
}
