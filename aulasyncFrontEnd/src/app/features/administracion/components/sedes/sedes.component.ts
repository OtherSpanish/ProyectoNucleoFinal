import { Component, OnInit, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { AulaModel } from '../../../../models/aula.model';
import { FranjaSedeModel } from '../../../../models/franja-sede.model';
import { SedeModel } from '../../../../models/sede.model';
import { AulaService } from '../../../../services/aula.service';
import { SedeService } from '../../../../services/sede.service';

@Component({
  selector: 'app-sedes',
  standalone: false,
  templateUrl: './sedes.component.html',
  styleUrl: './sedes.component.css',
})
export class SedesComponent implements OnInit {
  private readonly sedeService = inject(SedeService);
  private readonly aulaService = inject(AulaService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly sedes = signal<SedeModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelSedeAbierto = signal(false);
  protected readonly sedeFranjas = signal<SedeModel | null>(null);
  protected readonly franjas = signal<FranjaSedeModel[]>([]);
  protected readonly detalle = signal<SedeModel | null>(null);
  protected readonly aulasDetalle = signal<AulaModel[] | null>(null);
  protected readonly cargandoDetalle = signal(false);
  protected formulario: SedeModel = this.crearVacia();

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.sedeService.listar().subscribe({
      next: (lista) => {
        this.sedes.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  verDetalle(sede: SedeModel): void {
    if (sede.id === null) {
      return;
    }
    this.detalle.set(sede);
    this.aulasDetalle.set(null);
    this.cargandoDetalle.set(true);
    this.sedeService.buscarPorId(sede.id).subscribe({
      next: (datos) => {
        this.detalle.set(datos);
        this.cargandoDetalle.set(false);
      },
      error: () => this.cargandoDetalle.set(false),
    });
    this.aulaService.listar({ sedeId: sede.id, tipo: null, capacidadMinima: null, soloDisponibles: null }).subscribe({
      next: (lista) => this.aulasDetalle.set(lista),
    });
  }

  nueva(): void {
    this.formulario = this.crearVacia();
    this.panelSedeAbierto.set(true);
  }

  editar(sede: SedeModel): void {
    this.formulario = { ...sede };
    this.panelSedeAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null ? this.sedeService.crear(this.formulario) : this.sedeService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Sede guardada.');
        this.guardando.set(false);
        this.panelSedeAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  abrirFranjas(sede: SedeModel): void {
    const copias: FranjaSedeModel[] = [];
    for (const franja of sede.franjas) {
      copias.push({ ...franja });
    }
    this.franjas.set(copias);
    this.sedeFranjas.set(sede);
  }

  guardarFranjas(): void {
    const sede = this.sedeFranjas();
    if (sede === null || sede.id === null) {
      return;
    }
    this.guardando.set(true);
    this.sedeService.reemplazarFranjas(sede.id, this.franjas()).subscribe({
      next: () => {
        this.notificacionService.exito(`Franjas de ${sede.nombre} actualizadas.`);
        this.guardando.set(false);
        this.sedeFranjas.set(null);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(sede: SedeModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar sede',
      `¿Eliminar la sede ${sede.nombre}?`,
      'Eliminar',
      true,
    );
    if (!confirmado || sede.id === null) {
      return;
    }
    this.sedeService.eliminar(sede.id).subscribe({
      next: () => {
        this.notificacionService.exito('Sede eliminada.');
        this.cargar();
      },
    });
  }

  private crearVacia(): SedeModel {
    return { id: null, nombre: '', exclusivaPrimerSemestre: false, diasPrimerSemestre: null, franjas: [] };
  }
}
