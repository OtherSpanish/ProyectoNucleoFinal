import { Component, OnInit, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { CursoModel } from '../../../../models/curso.model';
import { DocenteModel } from '../../../../models/docente.model';
import { EspecialidadModel } from '../../../../models/especialidad.model';
import { CursoService } from '../../../../services/curso.service';
import { DocenteService } from '../../../../services/docente.service';
import { EspecialidadService } from '../../../../services/especialidad.service';

@Component({
  selector: 'app-especialidades',
  standalone: false,
  templateUrl: './especialidades.component.html',
  styleUrl: './especialidades.component.css',
})
export class EspecialidadesComponent implements OnInit {
  private readonly especialidadService = inject(EspecialidadService);
  private readonly docenteService = inject(DocenteService);
  private readonly cursoService = inject(CursoService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly especialidades = signal<EspecialidadModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected readonly detalle = signal<EspecialidadModel | null>(null);
  protected readonly docentesDetalle = signal<DocenteModel[] | null>(null);
  protected readonly cursosDetalle = signal<CursoModel[] | null>(null);
  protected readonly cargandoDetalle = signal(false);
  protected formulario: EspecialidadModel = this.crearVacia();

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.especialidadService.listar().subscribe({
      next: (lista) => {
        this.especialidades.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  /** Ficha de la especialidad con sus docentes y los cursos que la exigen. */
  verDetalle(especialidad: EspecialidadModel): void {
    if (especialidad.id === null) {
      return;
    }
    this.detalle.set(especialidad);
    this.docentesDetalle.set(null);
    this.cursosDetalle.set(null);
    this.cargandoDetalle.set(true);
    this.especialidadService.buscarPorId(especialidad.id).subscribe({
      next: (datos) => {
        this.detalle.set(datos);
        this.cargandoDetalle.set(false);
      },
      error: () => this.cargandoDetalle.set(false),
    });
    this.docenteService.listar(especialidad.id).subscribe({ next: (lista) => this.docentesDetalle.set(lista) });
    this.cursoService.listar(null, especialidad.id).subscribe({ next: (lista) => this.cursosDetalle.set(lista) });
  }

  nueva(): void {
    this.formulario = this.crearVacia();
    this.panelAbierto.set(true);
  }

  editar(especialidad: EspecialidadModel): void {
    this.formulario = { ...especialidad };
    this.panelAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null
        ? this.especialidadService.crear(this.formulario)
        : this.especialidadService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Especialidad guardada.');
        this.guardando.set(false);
        this.panelAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(especialidad: EspecialidadModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar especialidad',
      `¿Eliminar "${especialidad.nombre}"?`,
      'Eliminar',
      true,
    );
    if (!confirmado || especialidad.id === null) {
      return;
    }
    this.especialidadService.eliminar(especialidad.id).subscribe({
      next: () => {
        this.notificacionService.exito('Especialidad eliminada.');
        this.cargar();
      },
    });
  }

  private crearVacia(): EspecialidadModel {
    return { id: null, nombre: '', descripcion: '' };
  }
}
