import { Component, OnInit, inject, signal } from '@angular/core';

import { AulaModel } from '../../../../models/aula.model';
import { CursoModel } from '../../../../models/curso.model';
import { DocenteModel } from '../../../../models/docente.model';
import { FiltroHorarioModel } from '../../../../models/filtro-horario.model';
import { GrupoModel } from '../../../../models/grupo.model';
import { SedeModel } from '../../../../models/sede.model';
import { SemestreModel } from '../../../../models/semestre.model';
import { SesionHorarioModel } from '../../../../models/sesion-horario.model';
import { AulaService } from '../../../../services/aula.service';
import { CursoService } from '../../../../services/curso.service';
import { DocenteService } from '../../../../services/docente.service';
import { GrupoService } from '../../../../services/grupo.service';
import { HorarioService } from '../../../../services/horario.service';
import { SedeService } from '../../../../services/sede.service';
import { SemestreService } from '../../../../services/semestre.service';

/** Consulta del horario guardado con los filtros de GET /api/horarios (docente, aula, sede…). */
@Component({
  selector: 'app-consulta-horario',
  standalone: false,
  templateUrl: './consulta-horario.component.html',
  styleUrl: './consulta-horario.component.css',
})
export class ConsultaHorarioComponent implements OnInit {
  private readonly horarioService = inject(HorarioService);
  private readonly semestreService = inject(SemestreService);
  private readonly cursoService = inject(CursoService);
  private readonly grupoService = inject(GrupoService);
  private readonly docenteService = inject(DocenteService);
  private readonly aulaService = inject(AulaService);
  private readonly sedeService = inject(SedeService);

  protected readonly sesiones = signal<SesionHorarioModel[]>([]);
  protected readonly semestres = signal<SemestreModel[]>([]);
  protected readonly cursos = signal<CursoModel[]>([]);
  protected readonly grupos = signal<GrupoModel[]>([]);
  protected readonly docentes = signal<DocenteModel[]>([]);
  protected readonly aulas = signal<AulaModel[]>([]);
  protected readonly sedes = signal<SedeModel[]>([]);
  protected readonly cargando = signal(true);
  protected filtro: FiltroHorarioModel = this.crearFiltroVacio();

  ngOnInit(): void {
    this.semestreService.listar().subscribe({ next: (lista) => this.semestres.set(lista) });
    this.cursoService.listar().subscribe({ next: (lista) => this.cursos.set(lista) });
    this.docenteService.listar().subscribe({ next: (lista) => this.docentes.set(lista) });
    this.aulaService.listar().subscribe({ next: (lista) => this.aulas.set(lista) });
    this.sedeService.listar().subscribe({ next: (lista) => this.sedes.set(lista) });
    this.cargarGrupos();
    this.consultar();
  }

  consultar(): void {
    this.cargando.set(true);
    this.horarioService.listar(this.filtro).subscribe({
      next: (lista) => {
        this.sesiones.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  /** Al cambiar el curso se piden al backend solo sus grupos. */
  cambiarCurso(): void {
    this.filtro.grupoId = null;
    this.cargarGrupos();
    this.consultar();
  }

  limpiar(): void {
    this.filtro = this.crearFiltroVacio();
    this.cargarGrupos();
    this.consultar();
  }

  private cargarGrupos(): void {
    this.grupoService.listar({ cursoId: this.filtro.cursoId, docenteId: null, semestreId: null }).subscribe({
      next: (lista) => this.grupos.set(lista),
    });
  }

  private crearFiltroVacio(): FiltroHorarioModel {
    return {
      semestre: null,
      cursoId: null,
      grupoId: null,
      docenteId: null,
      aulaId: null,
      sedeId: null,
      soloUbicadas: null,
    };
  }
}
