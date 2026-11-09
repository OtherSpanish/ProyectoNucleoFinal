import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { AulaModel } from '../../../../models/aula.model';
import { FiltroAulaModel } from '../../../../models/filtro-aula.model';
import { FiltroHorarioModel } from '../../../../models/filtro-horario.model';
import { SedeModel } from '../../../../models/sede.model';
import { SesionHorarioModel } from '../../../../models/sesion-horario.model';
import { AulaService } from '../../../../services/aula.service';
import { CatalogoService } from '../../../../services/catalogo.service';
import { HorarioService } from '../../../../services/horario.service';
import { SedeService } from '../../../../services/sede.service';

@Component({
  selector: 'app-aulas',
  standalone: false,
  templateUrl: './aulas.component.html',
  styleUrl: './aulas.component.css',
})
export class AulasComponent implements OnInit {
  private readonly aulaService = inject(AulaService);
  private readonly horarioService = inject(HorarioService);
  private readonly sedeService = inject(SedeService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  private readonly catalogoService = inject(CatalogoService);

  protected readonly tipos = computed(() => this.catalogoService.opciones('tiposAula'));
  protected readonly aulas = signal<AulaModel[]>([]);
  protected readonly sedes = signal<SedeModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected readonly detalle = signal<AulaModel | null>(null);
  protected readonly sesionesDetalle = signal<SesionHorarioModel[] | null>(null);
  protected readonly cargandoDetalle = signal(false);
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

  /** Ficha del aula y su ocupación en el horario guardado (GET /horarios?aulaId=). */
  verDetalle(aula: AulaModel): void {
    if (aula.id === null) {
      return;
    }
    this.detalle.set(aula);
    this.sesionesDetalle.set(null);
    this.cargandoDetalle.set(true);
    this.aulaService.buscarPorId(aula.id).subscribe({
      next: (datos) => {
        this.detalle.set(datos);
        this.cargandoDetalle.set(false);
      },
      error: () => this.cargandoDetalle.set(false),
    });
    this.horarioService.listar(this.filtroHorario(aula.id)).subscribe({
      next: (lista) => this.sesionesDetalle.set(lista),
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

  private filtroHorario(aulaId: number): FiltroHorarioModel {
    return { semestre: null, cursoId: null, grupoId: null, docenteId: null, aulaId, sedeId: null, soloUbicadas: true };
  }

  private crearVacia(): AulaModel {
    return { id: null, nombre: '', capacidad: null, tipo: null, disponible: true, sedeId: null };
  }
}
