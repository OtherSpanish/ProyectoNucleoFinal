import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuariosApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { ROLES, Rol, Usuario, etiquetaRol } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';

/** Gestión de usuarios (solo ADMIN): /api/usuarios. La contraseña pide 8+ caracteres con letra y número. */
@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent],
  templateUrl: './usuarios.component.html',
})
export class UsuariosComponent implements OnInit {
  private api = inject(UsuariosApi);
  private auth = inject(AuthService);

  usuarios: Usuario[] = [];
  filtroRol: Rol | '' = '';
  roles = ROLES;
  etiqueta = etiquetaRol;

  editandoId: number | null = null;
  form: Usuario = this.vacio();
  mostrarForm = false;

  error = '';
  ok = '';
  cargando = false;

  ngOnInit(): void { this.cargar(); }

  private vacio(): Usuario { return { nombre: '', correo: '', password: '', rol: 'COMITE' }; }

  get esYo(): (u: Usuario) => boolean {
    return (u) => u.correo === this.auth.usuario()?.correo;
  }

  cargar(): void {
    this.cargando = true;
    this.api.listar(this.filtroRol || null).subscribe({
      next: (u) => { this.usuarios = u; this.cargando = false; },
      error: (e) => { this.error = mensajeError(e); this.cargando = false; },
    });
  }

  nuevo(): void {
    this.editandoId = null;
    this.form = this.vacio();
    this.mostrarForm = true;
    this.limpiar();
  }

  editar(u: Usuario): void {
    this.editandoId = u.id ?? null;
    this.form = { id: u.id, nombre: u.nombre, correo: u.correo, rol: u.rol, password: '' };
    this.mostrarForm = true;
    this.limpiar();
  }

  cancelar(): void { this.mostrarForm = false; this.editandoId = null; }

  guardar(): void {
    this.limpiar();
    const cuerpo: Usuario = { nombre: this.form.nombre.trim(), correo: this.form.correo.trim(), rol: this.form.rol };
    if (this.form.password) cuerpo.password = this.form.password;

    const peticion = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    peticion.subscribe({
      next: (u) => {
        this.ok = this.editandoId === null ? `Usuario ${u.correo} creado.` : `Usuario ${u.correo} actualizado.`;
        this.cancelar();
        this.cargar();
        // Si me edité a mí mismo, se refrescan nombre/rol del menú.
        if (u.correo === this.auth.usuario()?.correo) this.auth.refrescar();
      },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  eliminar(u: Usuario): void {
    if (!u.id || !confirm(`¿Eliminar al usuario ${u.correo}?`)) return;
    this.limpiar();
    this.api.eliminar(u.id).subscribe({
      next: () => { this.ok = `Usuario ${u.correo} eliminado.`; this.cargar(); },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
