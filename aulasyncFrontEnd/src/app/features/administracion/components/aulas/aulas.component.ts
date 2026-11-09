import { Component, OnInit, inject, signal } from '@angular/core';

import { CATALOGO_TIPOS_AULA } from '../../../../core/constants/catalogos.constants';
import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { AulaModel } from '../../../../models/aula.model';
import { FiltroAulaModel } from '../../../../models/filtro-aula.model';
import { SedeModel } from '../../../../models/sede.model';
import { AulaService } from '../../../../services/aula.service';
import { SedeService } from '../../../../services/sede.service';

@Component({
  selector: 'app-aulas',
  standalone: false,
  templateUrl: './aulas.component.html',
  styleUrl: './aulas.component.css',
})
export class AulasComponent implements OnInit {
  private readonly aulaService = inject(AulaService);
  private readonly sedeService = inject(SedeService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly tipos = CATALOGO_TIPOS_AULA;
  protected readonly aulas = signal<AulaModel[]>([]);
  protected readonly sedes = signal<SedeModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected filtro: FiltroAulaModel = { sedeId: null, tipo: null, capacidadMinima: null, soloDisponibles: null };
  protected formulario: AulaModel = this.crearVacia();

  ngOnInit(): void {
    this.sedeService.listar().subscribe({ next: (lista) => this.sedes.set(lista) });
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.aulaService.listar(this.filtro).subscribe({
      next: (lista) => {
        this.aulas.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  nueva(): void {
    this.formulario = this.crearVacia();
    this.panelAbierto.set(true);
  }

  editar(aula: AulaModel): void {
    this.formulario = { ...aula };
    this.panelAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null ? this.aulaService.crear(this.formulario) : this.aulaService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Aula guardada.');
        this.guardando.set(false);
        this.panelAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(aula: AulaModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar aula',
      `¿Eliminar el aula ${aula.nombre}?`,
      'Eliminar',
      true,
    );
    if (!confirmado || aula.id === null) {
      return;
    }
    this.aulaService.eliminar(aula.id).subscribe({
      next: () => {
        this.notificacionService.exito('Aula eliminada.');
        this.cargar();
      },
    });
  }

  private crearVacia(): AulaModel {
    return { id: null, nombre: '', capacidad: null, tipo: null, disponible: true, sedeId: null };
  }
}
