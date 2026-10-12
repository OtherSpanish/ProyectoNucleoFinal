import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { DataService } from '../../core/data.service';
import { ROLES, RolTecnico } from '../../core/models';

/** CU12 – Gestión de usuarios y roles (RF10, CA10). */
@Component({
  selector: 'app-gestion-usuarios',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './gestion-usuarios.component.html',
})
export class GestionUsuariosComponent {
  data = inject(DataService);
  auth = inject(AuthService);

  roles = ROLES;
  nuevo = { nombre: '', usuario: '', clave: '', rol: 'COMITE_TACTICO' as RolTecnico };
  error = '';

  agregar(): void {
    const n = this.nuevo;
    if (!n.nombre.trim() || !n.usuario.trim() || !n.clave) {
      this.error = 'Nombre, usuario y contraseña son obligatorios.';
      return;
    }
    if (this.data.usuarios().some((u) => u.usuario === n.usuario.trim())) {
      this.error = 'Ese nombre de usuario ya existe.';
      return;
    }
    this.error = '';
    this.data.usuarios.update((l) => [
      ...l,
      { id: this.data.nuevoId('U'), nombre: n.nombre.trim(), usuario: n.usuario.trim(), clave: n.clave, rol: n.rol, activo: true },
    ]);
    this.nuevo = { nombre: '', usuario: '', clave: '', rol: 'COMITE_TACTICO' };
  }

  cambiarRol(id: string, rol: RolTecnico): void {
    this.data.usuarios.update((l) => l.map((u) => (u.id === id ? { ...u, rol } : u)));
  }

  alternarActivo(id: string): void {
    this.data.usuarios.update((l) => l.map((u) => (u.id === id ? { ...u, activo: !u.activo } : u)));
  }

  eliminar(id: string): void {
    this.data.usuarios.update((l) => l.filter((u) => u.id !== id));
  }

  esYo(id: string): boolean {
    return this.auth.usuario()?.id === id;
  }
}
