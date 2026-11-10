import { Component, OnInit, computed, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CursosApi, EspecialidadesApi, SemestresApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import {
  Curso, Especialidad, INTENSIDADES, Intensidad, Semestre, TIPOS_AULA, TipoAula, etiquetaIntensidad, etiquetaTipoAula,
} from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';

/** Cursos (/api/cursos): leer ADMIN y COMITE; crear, editar y eliminar solo COMITE. */
@Component({
  selector: 'app-cursos',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent],
  templateUrl: './cursos.component.html',
})
export class CursosComponent implements OnInit {
  private api = inject(CursosApi);
  private semestresApi = inject(SemestresApi);
  private especialidadesApi = inject(EspecialidadesApi);
  private auth = inject(AuthService);

  puedeEditar = computed(() => this.auth.rol() === 'COMITE');

  cursos: Curso[] = [];
  semestres: Semestre[] = [];
  especialidades: Especialidad[] = [];
  tipos = TIPOS_AULA;
  intensidades = INTENSIDADES;
  etiquetaTipo = etiquetaTipoAula;
  etiquetaIntensidad = etiquetaIntensidad;

  filtroSemestre: number | null = null;
  filtroEspecialidad: number | null = null;

  mostrarForm = false;
  editandoId: number | null = null;
  form = this.vacio();

  error = '';
  ok = '';

  ngOnInit(): void {
    this.semestresApi.listar().subscribe({ next: (s) => (this.semestres = s), error: (e) => (this.error = mensajeError(e)) });
    this.especialidadesApi.listar().subscribe({ next: (e) => (this.especialidades = e), error: (e) => (this.error = mensajeError(e)) });
    this.cargar();
  }

  private vacio() {
    return {
      codigo: '', nombre: '', semestreId: null as number | null, especialidadId: null as number | null,
      tipoAula: 'CONVENCIONAL' as TipoAula, intensidad: 'DOS_SESIONES_DOS_HORAS' as Intensidad, nucleoComun: false,
    };
  }

  cargar(): void {
    this.api.listar({ semestreId: this.filtroSemestre, especialidadId: this.filtroEspecialidad }).subscribe({
      next: (c) => (this.cursos = c),
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  nuevo(): void {
    this.limpiar();
    this.editandoId = null;
    this.form = { ...this.vacio(), semestreId: this.filtroSemestre, especialidadId: this.filtroEspecialidad };
    this.mostrarForm = true;
  }

  editar(c: Curso): void {
    this.limpiar();
    this.editandoId = c.id ?? null;
    this.form = {
      codigo: c.codigo, nombre: c.nombre, semestreId: c.semestreId, especialidadId: c.especialidadId,
      tipoAula: c.tipoAula, intensidad: c.intensidad, nucleoComun: c.nucleoComun,
    };
    this.mostrarForm = true;
  }

  guardar(): void {
    this.limpiar();
    if (this.form.semestreId === null || this.form.especialidadId === null) {
      this.error = 'Selecciona el semestre y la especialidad del curso.';
      return;
    }
    const cuerpo: Curso = {
      codigo: this.form.codigo.trim(),
      nombre: this.form.nombre.trim(),
      semestreId: this.form.semestreId,
      especialidadId: this.form.especialidadId,
      tipoAula: this.form.tipoAula,
      intensidad: this.form.intensidad,
      nucleoComun: !!this.form.nucleoComun,
    };
    const p = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    p.subscribe({
      next: (c) => {
        this.ok = `Curso ${c.codigo} guardado.${this.editandoId !== null ? ' Si ya hay un horario, usa «Sincronizar» en Generar horario para reflejar el cambio.' : ''}`;
        this.mostrarForm = false;
        this.cargar();
      },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  eliminar(c: Curso): void {
    if (!c.id || !confirm(`¿Eliminar el curso ${c.codigo} – ${c.nombre}?`)) return;
    this.limpiar();
    this.api.eliminar(c.id).subscribe({
      next: () => { this.ok = `Curso ${c.codigo} eliminado.`; this.cargar(); },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
