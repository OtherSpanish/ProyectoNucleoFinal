import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { CursoModel } from '../../../../models/curso.model';
import { EspecialidadModel } from '../../../../models/especialidad.model';
import { GrupoModel } from '../../../../models/grupo.model';
import { SemestreModel } from '../../../../models/semestre.model';
import { CatalogoService } from '../../../../services/catalogo.service';
import { CursoService } from '../../../../services/curso.service';
import { EspecialidadService } from '../../../../services/especialidad.service';
import { GrupoService } from '../../../../services/grupo.service';
import { SemestreService } from '../../../../services/semestre.service';

@Component({
  selector: 'app-cursos',
  standalone: false,
  templateUrl: './cursos.component.html',
  styleUrl: './cursos.component.css',
})
export class CursosComponent implements OnInit {
  private readonly cursoService = inject(CursoService);
  private readonly grupoService = inject(GrupoService);
  private readonly semestreService = inject(SemestreService);
  private readonly especialidadService = inject(EspecialidadService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  private readonly catalogoService = inject(CatalogoService);

  protected readonly tiposAula = computed(() => this.catalogoService.opciones('tiposAula'));
  protected readonly intensidades = computed(() => this.catalogoService.opciones('intensidades'));
  protected readonly cursos = signal<CursoModel[]>([]);
  protected readonly semestres = signal<SemestreModel[]>([]);
  protected readonly especialidades = signal<EspecialidadModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected readonly detalle = signal<CursoModel | null>(null);
  protected readonly gruposDetalle = signal<GrupoModel[] | null>(null);
  protected readonly cargandoDetalle = signal(false);
  protected filtroSemestreId: number | null = null;
  protected filtroEspecialidadId: number | null = null;
  protected prefijo = '';
  protected formulario: CursoModel = this.crearVacio();

  ngOnInit(): void {
    this.semestreService.listar().subscribe({ next: (lista) => this.semestres.set(lista) });
    this.especialidadService.listar().subscribe({ next: (lista) => this.especialidades.set(lista) });
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    const peticion =
      this.prefijo.trim() === ''
        ? this.cursoService.listar(this.filtroSemestreId, this.filtroEspecialidadId)
        : this.cursoService.buscarPorPrefijo(this.prefijo);
    peticion.subscribe({
      next: (lista) => {
        this.cursos.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  verDetalle(curso: CursoModel): void {
    if (curso.id === null) {
      return;
    }
    this.detalle.set(curso);
    this.gruposDetalle.set(null);
    this.cargandoDetalle.set(true);
    this.cursoService.buscarPorId(curso.id).subscribe({
      next: (datos) => {
        this.detalle.set(datos);
        this.cargandoDetalle.set(false);
      },
      error: () => this.cargandoDetalle.set(false),
    });
    this.grupoService.listar({ cursoId: curso.id, docenteId: null, semestreId: null }).subscribe({
      next: (lista) => this.gruposDetalle.set(lista),
    });
  }

  nuevo(): void {
    this.formulario = this.crearVacio();
    this.panelAbierto.set(true);
  }

  editar(curso: CursoModel): void {
    this.formulario = { ...curso };
    this.panelAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null ? this.cursoService.crear(this.formulario) : this.cursoService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Curso guardado.');
        this.guardando.set(false);
        this.panelAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(curso: CursoModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar curso',
      `¿Eliminar ${curso.codigo} · ${curso.nombre}?`,
      'Eliminar',
      true,
    );
    if (!confirmado || curso.id === null) {
      return;
    }
    this.cursoService.eliminar(curso.id).subscribe({
      next: () => {
        this.notificacionService.exito('Curso eliminado.');
        this.cargar();
      },
    });
  }

  private crearVacio(): CursoModel {
    return {
      id: null,
      codigo: '',
      nombre: '',
      semestreId: null,
      especialidadId: null,
      tipoAula: null,
      intensidad: null,
      nucleoComun: false,
    };
  }
}
