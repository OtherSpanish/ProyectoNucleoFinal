import { Component, OnInit, computed, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CursosApi, DocentesApi, EstudiantesApi, GruposApi, SemestresApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { Curso, Docente, Estudiante, Grupo, InscritosGrupo, Semestre } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';

/**
 * Grupos (/api/grupos) e inscritos (/api/grupos/{id}/estudiantes).
 * COMITE crea/edita/elimina grupos; ADMIN y COMITE gestionan inscripciones.
 * Un grupo está «abierto» desde 10 inscritos; el máximo es 30 (reglas del back).
 */
@Component({
  selector: 'app-grupos',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent],
  templateUrl: './grupos.component.html',
})
export class GruposComponent implements OnInit {
  private api = inject(GruposApi);
  private cursosApi = inject(CursosApi);
  private docentesApi = inject(DocentesApi);
  private estudiantesApi = inject(EstudiantesApi);
  private semestresApi = inject(SemestresApi);
  private auth = inject(AuthService);

  puedeEditar = computed(() => this.auth.rol() === 'COMITE');

  grupos: Grupo[] = [];
  cursos: Curso[] = [];
  docentes: Docente[] = [];
  semestres: Semestre[] = [];
  estudiantes: Estudiante[] = [];

  filtroSemestre: number | null = null;
  filtroCurso: number | null = null;
  filtroDocente: number | null = null;

  // formulario de grupo
  mostrarForm = false;
  editandoId: number | null = null;
  cursoId: number | null = null;
  docenteId: number | null = null;
  numero = 1;

  // inscritos
  panel: InscritosGrupo | null = null;
  estudianteElegido: number | null = null;
  buscarEst = '';

  error = '';
  ok = '';

  ngOnInit(): void {
    this.semestresApi.listar().subscribe({ next: (s) => (this.semestres = s), error: (e) => (this.error = mensajeError(e)) });
    this.cursosApi.listar().subscribe({ next: (c) => (this.cursos = c), error: (e) => (this.error = mensajeError(e)) });
    this.docentesApi.listar().subscribe({ next: (d) => (this.docentes = d), error: (e) => (this.error = mensajeError(e)) });
    this.estudiantesApi.listar().subscribe({ next: (s) => (this.estudiantes = s), error: (e) => (this.error = mensajeError(e)) });
    this.cargar();
  }

  cargar(): void {
    this.api.listar({ semestreId: this.filtroSemestre, cursoId: this.filtroCurso, docenteId: this.filtroDocente }).subscribe({
      next: (g) => (this.grupos = g),
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  /** Solo los docentes con la especialidad del curso elegido (el back lo exige al guardar). */
  docentesAptos(): Docente[] {
    const curso = this.cursos.find((c) => c.id === this.cursoId);
    if (!curso) return this.docentes;
    return this.docentes.filter((d) => d.especialidades.some((e) => e.id === curso.especialidadId));
  }

  // ---------------------------------------------------------------- Grupo
  nuevo(): void {
    this.limpiar();
    this.editandoId = null;
    this.cursoId = this.filtroCurso;
    this.docenteId = null;
    this.numero = 1;
    this.mostrarForm = true;
  }

  editar(g: Grupo): void {
    this.limpiar();
    this.editandoId = g.id ?? null;
    this.cursoId = g.cursoId;
    this.docenteId = g.docenteId;
    this.numero = g.numero;
    this.mostrarForm = true;
  }

  guardar(): void {
    this.limpiar();
    if (this.cursoId === null || this.docenteId === null) { this.error = 'Selecciona el curso y el docente.'; return; }
    const cuerpo: Grupo = { numero: Number(this.numero), cursoId: this.cursoId, docenteId: this.docenteId };
    const p = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    p.subscribe({
      next: (g) => {
        this.ok = `Grupo ${g.cursoCodigo} G${g.numero} guardado.`;
        this.mostrarForm = false;
        this.cargar();
        if (this.panel?.grupo.id === g.id) this.abrirInscritos(g);
      },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  eliminar(g: Grupo): void {
    if (!g.id || !confirm(`¿Eliminar el grupo ${g.cursoCodigo} G${g.numero}? Se borran sus inscripciones y sesiones de horario.`)) return;
    this.limpiar();
    this.api.eliminar(g.id).subscribe({
      next: () => {
        this.ok = `Grupo ${g.cursoCodigo} G${g.numero} eliminado.`;
        if (this.panel?.grupo.id === g.id) this.panel = null;
        this.cargar();
      },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  // ---------------------------------------------------------------- Inscritos
  abrirInscritos(g: Grupo): void {
    if (!g.id) return;
    this.api.inscritos(g.id).subscribe({
      next: (p) => { this.panel = p; this.estudianteElegido = null; this.buscarEst = ''; },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  cerrarInscritos(): void { this.panel = null; }

  /** Estudiantes aún no inscritos en este grupo, filtrados por el texto de búsqueda. */
  candidatos(): Estudiante[] {
    if (!this.panel) return [];
    const ya = new Set(this.panel.estudiantes.map((e) => e.id));
    const t = this.buscarEst.trim().toLowerCase();
    return this.estudiantes
      .filter((e) => !ya.has(e.id))
      .filter((e) => !t || `${e.codigo} ${e.nombre}`.toLowerCase().includes(t));
  }

  inscribir(): void {
    if (!this.panel?.grupo.id || this.estudianteElegido === null) return;
    this.limpiar();
    this.api.inscribir(this.panel.grupo.id, this.estudianteElegido).subscribe({
      next: (p) => { this.panel = p; this.estudianteElegido = null; this.cargar(); },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  retirar(est: Estudiante): void {
    if (!this.panel?.grupo.id || !est.id) return;
    this.limpiar();
    this.api.retirar(this.panel.grupo.id, est.id).subscribe({
      next: (p) => { this.panel = p; this.cargar(); },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
