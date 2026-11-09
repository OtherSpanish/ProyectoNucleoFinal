import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { RolModel } from '../../../../models/rol.model';
import { UsuarioModel } from '../../../../models/usuario.model';
import { CatalogoService } from '../../../../services/catalogo.service';
import { UsuarioService } from '../../../../services/usuario.service';

@Component({
  selector: 'app-usuarios',
  standalone: false,
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.css',
})
export class UsuariosComponent implements OnInit {
  private readonly usuarioService = inject(UsuarioService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  private readonly catalogoService = inject(CatalogoService);

  protected readonly roles = computed(() => this.catalogoService.opciones('roles'));
  protected readonly usuarios = signal<UsuarioModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected readonly detalle = signal<UsuarioModel | null>(null);
  protected readonly cargandoDetalle = signal(false);
  protected filtroRol: RolModel | null = null;
  protected formulario: UsuarioModel = this.crearVacio();

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.usuarioService.listar(this.filtroRol).subscribe({
      next: (lista) => {
        this.usuarios.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  verDetalle(usuario: UsuarioModel): void {
    if (usuario.id === null) {
      return;
    }
    this.detalle.set(usuario);
    this.cargandoDetalle.set(true);
    this.usuarioService.buscarPorId(usuario.id).subscribe({
      next: (datos) => {
        this.detalle.set(datos);
        this.cargandoDetalle.set(false);
      },
      error: () => this.cargandoDetalle.set(false),
    });
  }

  nuevo(): void {
    this.formulario = this.crearVacio();
    this.panelAbierto.set(true);
  }

  editar(usuario: UsuarioModel): void {
    this.formulario = { ...usuario, password: '' };
    this.panelAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null ? this.usuarioService.crear(this.formulario) : this.usuarioService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Usuario guardado.');
        this.guardando.set(false);
        this.panelAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(usuario: UsuarioModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar usuario',
      `¿Eliminar a ${usuario.nombre} (${usuario.correo})?`,
      'Eliminar',
      true,
    );
    if (!confirmado || usuario.id === null) {
      return;
    }
    this.usuarioService.eliminar(usuario.id).subscribe({
      next: () => {
        this.notificacionService.exito('Usuario eliminado.');
        this.cargar();
      },
    });
  }

  private crearVacio(): UsuarioModel {
    return { id: null, nombre: '', correo: '', password: '', rol: null };
  }
}
