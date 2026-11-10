import { Component, OnInit, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { CursoModel } from '../../../../models/curso.model';
import { DocenteModel } from '../../../../models/docente.model';
import { FiltroGrupoModel } from '../../../../models/filtro-grupo.model';
import { GrupoModel } from '../../../../models/grupo.model';
import { SemestreModel } from '../../../../models/semestre.model';
import { SesionHorarioModel } from '../../../../models/sesion-horario.model';
import { CursoService } from '../../../../services/curso.service';
import { DocenteService } from '../../../../services/docente.service';
import { GrupoService } from '../../../../services/grupo.service';
import { HorarioService } from '../../../../services/horario.service';
import { SemestreService } from '../../../../services/semestre.service';

@Component({
  selector: 'app-grupos',
  standalone: false,
  templateUrl: './grupos.component.html',
  styleUrl: './grupos.component.css',
})
export class GruposComponent implements OnInit {
  private readonly grupoService = inject(GrupoService);
  private readonly horarioService = inject(HorarioService);
  private readonly cursoService = inject(CursoService);
  private readonly docenteService = inject(DocenteService);
  private readonly semestreService = inject(SemestreService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly grupos = signal<GrupoModel[]>([]);
  protected readonly cursos = signal<CursoModel[]>([]);
  protected readonly docentes = signal<DocenteModel[]>([]);
  protected readonly semestres = signal<SemestreModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected readonly grupoInscripcion = signal<GrupoModel | null>(null);
  protected readonly detalle = signal<GrupoModel | null>(null);
  protected readonly sesionesDetalle = signal<SesionHorarioModel[] | null>(null);
  protected readonly cargandoDetalle = signal(false);
  protected filtro: FiltroGrupoModel = { cursoId: null, docenteId: null, semestreId: null };
  protected formulario: GrupoModel = this.crearVacio();

  ngOnInit(): void {
    this.cursoService.listar().subscribe({ next: (lista) => this.cursos.set(lista) });
    this.docenteService.listar().subscribe({ next: (lista) => this.docentes.set(lista) });
    this.semestreService.listar().subscribe({ next: (lista) => this.semestres.set(lista) });
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.grupoService.listar(this.filtro).subscribe({
      next: (lista) => {
        this.grupos.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  /** Ficha del grupo y sus sesiones en el horario guardado (GET /horarios?grupoId=). */
  verDetalle(grupo: GrupoModel): void {
    if (grupo.id === null) {
      return;
    }
    this.detalle.set(grupo);
    this.sesionesDetalle.set(null);
    this.cargandoDetalle.set(true);
    this.grupoService.buscarPorId(grupo.id).subscribe({
      next: (datos) => {
        this.detalle.set(datos);
        this.cargandoDetalle.set(false);
      },
      error: () => this.cargandoDetalle.set(false),
    });
    const filtro = { semestre: null, cursoId: null, grupoId: grupo.id, docenteId: null, aulaId: null, sedeId: null, soloUbicadas: null };
    this.horarioService.listar(filtro).subscribe({ next: (lista) => this.sesionesDetalle.set(lista) });
  }

  nuevo(): void {
    this.formulario = this.crearVacio();
    this.panelAbierto.set(true);
  }

  editar(grupo: GrupoModel): void {
    this.formulario = { ...grupo };
    this.panelAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null ? this.grupoService.crear(this.formulario) : this.grupoService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Grupo guardado.');
        this.guardando.set(false);
        this.panelAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(grupo: GrupoModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar grupo',
      `¿Eliminar el grupo ${grupo.numero} de ${grupo.cursoCodigo}? Sus sesiones saldrán del horario.`,
      'Eliminar',
      true,
    );
    if (!confirmado || grupo.id === null) {
      return;
    }
    this.grupoService.eliminar(grupo.id).subscribe({
      next: () => {
        this.notificacionService.exito('Grupo eliminado.');
        this.cargar();
      },
    });
  }

  private crearVacio(): GrupoModel {
    return { id: null, numero: null, cursoId: null, docenteId: null };
  }
}
