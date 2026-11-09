import { Component, OnInit, inject, signal } from '@angular/core';

import { CATALOGO_INTENSIDADES, CATALOGO_TIPOS_AULA } from '../../../../core/constants/catalogos.constants';
import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { CursoModel } from '../../../../models/curso.model';
import { EspecialidadModel } from '../../../../models/especialidad.model';
import { SemestreModel } from '../../../../models/semestre.model';
import { CursoService } from '../../../../services/curso.service';
import { EspecialidadService } from '../../../../services/especialidad.service';
import { SemestreService } from '../../../../services/semestre.service';

@Component({
  selector: 'app-cursos',
  standalone: false,
  templateUrl: './cursos.component.html',
  styleUrl: './cursos.component.css',
})
export class CursosComponent implements OnInit {
  private readonly cursoService = inject(CursoService);
  private readonly semestreService = inject(SemestreService);
  private readonly especialidadService = inject(EspecialidadService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly tiposAula = CATALOGO_TIPOS_AULA;
  protected readonly intensidades = CATALOGO_INTENSIDADES;
  protected readonly cursos = signal<CursoModel[]>([]);
  protected readonly semestres = signal<SemestreModel[]>([]);
  protected readonly especialidades = signal<EspecialidadModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
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
      tipoAula: 'CONVENCIONAL',
      intensidad: null,
      nucleoComun: false,
    };
  }
}
