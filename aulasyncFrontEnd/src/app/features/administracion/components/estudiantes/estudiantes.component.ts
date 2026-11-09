import { Component, OnInit, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { EstudianteModel } from '../../../../models/estudiante.model';
import { SemestreModel } from '../../../../models/semestre.model';
import { EstudianteService } from '../../../../services/estudiante.service';
import { SemestreService } from '../../../../services/semestre.service';

@Component({
  selector: 'app-estudiantes',
  standalone: false,
  templateUrl: './estudiantes.component.html',
  styleUrl: './estudiantes.component.css',
})
export class EstudiantesComponent implements OnInit {
  private readonly estudianteService = inject(EstudianteService);
  private readonly semestreService = inject(SemestreService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly estudiantes = signal<EstudianteModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected readonly semestres = signal<SemestreModel[]>([]);
  protected readonly detalle = signal<EstudianteModel | null>(null);
  protected readonly cargandoDetalle = signal(false);
  protected prefijo = '';
  protected formulario: EstudianteModel = this.crearVacio();

  ngOnInit(): void {
    this.semestreService.listar().subscribe({ next: (lista) => this.semestres.set(lista) });
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    const peticion =
      this.prefijo.trim() === ''
        ? this.estudianteService.listar()
        : this.estudianteService.buscarPorPrefijo(this.prefijo);
    peticion.subscribe({
      next: (lista) => {
        this.estudiantes.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  limpiarBusqueda(): void {
    this.prefijo = '';
    this.cargar();
  }

  verDetalle(estudiante: EstudianteModel): void {
    if (estudiante.id === null) {
      return;
    }
    this.detalle.set(estudiante);
    this.cargandoDetalle.set(true);
    this.estudianteService.buscarPorId(estudiante.id).subscribe({
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

  editar(estudiante: EstudianteModel): void {
    this.formulario = { ...estudiante };
    this.panelAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null
        ? this.estudianteService.crear(this.formulario)
        : this.estudianteService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Estudiante guardado.');
        this.guardando.set(false);
        this.panelAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(estudiante: EstudianteModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar estudiante',
      `¿Eliminar a ${estudiante.nombre} (${estudiante.codigo})?`,
      'Eliminar',
      true,
    );
    if (!confirmado || estudiante.id === null) {
      return;
    }
    this.estudianteService.eliminar(estudiante.id).subscribe({
      next: () => {
        this.notificacionService.exito('Estudiante eliminado.');
        this.cargar();
      },
    });
  }

  private crearVacio(): EstudianteModel {
    return { id: null, codigo: '', nombre: '', correo: '', semestreId: null };
  }
}
