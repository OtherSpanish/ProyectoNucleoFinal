import { Component, OnInit, inject, signal } from '@angular/core';

import { CATALOGO_DEDICACIONES } from '../../../../core/constants/catalogos.constants';
import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { DocenteModel } from '../../../../models/docente.model';
import { EspecialidadModel } from '../../../../models/especialidad.model';
import { UsuarioModel } from '../../../../models/usuario.model';
import { DocenteService } from '../../../../services/docente.service';
import { EspecialidadService } from '../../../../services/especialidad.service';
import { UsuarioService } from '../../../../services/usuario.service';

@Component({
  selector: 'app-docentes',
  standalone: false,
  templateUrl: './docentes.component.html',
  styleUrl: './docentes.component.css',
})
export class DocentesComponent implements OnInit {
  private readonly docenteService = inject(DocenteService);
  private readonly especialidadService = inject(EspecialidadService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly dedicaciones = CATALOGO_DEDICACIONES;
  protected readonly docentes = signal<DocenteModel[]>([]);
  protected readonly especialidades = signal<EspecialidadModel[]>([]);
  protected readonly usuariosDocentes = signal<UsuarioModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected readonly docenteDisponibilidad = signal<DocenteModel | null>(null);
  protected filtroEspecialidadId: number | null = null;
  protected prefijo = '';
  protected formulario: DocenteModel = this.crearVacio();

  ngOnInit(): void {
    this.especialidadService.listar().subscribe({ next: (lista) => this.especialidades.set(lista) });
    this.usuarioService.listar('DOCENTE').subscribe({ next: (lista) => this.usuariosDocentes.set(lista) });
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    const peticion =
      this.prefijo.trim() === ''
        ? this.docenteService.listar(this.filtroEspecialidadId)
        : this.docenteService.buscarPorPrefijo(this.prefijo);
    peticion.subscribe({
      next: (lista) => {
        this.docentes.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  nuevo(): void {
    this.formulario = this.crearVacio();
    this.panelAbierto.set(true);
  }

  editar(docente: DocenteModel): void {
    this.formulario = { ...docente, usuario: { ...docente.usuario }, especialidades: [...docente.especialidades] };
    this.panelAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null ? this.docenteService.crear(this.formulario) : this.docenteService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Docente guardado.');
        this.guardando.set(false);
        this.panelAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(docente: DocenteModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar docente',
      `¿Eliminar el perfil docente de ${docente.usuario.nombre}? Su usuario se conserva.`,
      'Eliminar',
      true,
    );
    if (!confirmado || docente.id === null) {
      return;
    }
    this.docenteService.eliminar(docente.id).subscribe({
      next: () => {
        this.notificacionService.exito('Docente eliminado.');
        this.cargar();
      },
    });
  }

  compararPorId(a: { id: number | null } | null, b: { id: number | null } | null): boolean {
    return a !== null && b !== null && a.id === b.id;
  }

  private crearVacio(): DocenteModel {
    return {
      id: null,
      usuario: { id: null, nombre: '', correo: '', rol: 'DOCENTE' },
      dedicacion: null,
      especialidades: [],
    };
  }
}
