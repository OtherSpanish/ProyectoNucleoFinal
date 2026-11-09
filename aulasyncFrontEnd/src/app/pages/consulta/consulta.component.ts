import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AulasApi, CursosApi, DocentesApi, HorarioApi, SedesApi } from '../../core/api';
import { Aula, Curso, Docente, Sede, SesionHorario, etiquetaDia, formatoHora } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';
import { descargarCsv } from '../../shared/horario-util';

/** Consulta del horario guardado con filtros en el servidor (GET /api/horarios) y exportación a CSV. */
@Component({
  selector: 'app-consulta',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent],
  templateUrl: './consulta.component.html',
})
export class ConsultaComponent implements OnInit {
  private api = inject(HorarioApi);
  private cursosApi = inject(CursosApi);
  private docentesApi = inject(DocentesApi);
  private sedesApi = inject(SedesApi);
  private aulasApi = inject(AulasApi);

  cursos: Curso[] = [];
  docentes: Docente[] = [];
  sedes: Sede[] = [];
  aulas: Aula[] = [];
  semestres = [1, 2, 3, 4, 5, 6, 7, 8, 9];

  semestre: number | null = null;
  cursoId: number | null = null;
  docenteId: number | null = null;
  sedeId: number | null = null;
  aulaId: number | null = null;
  soloUbicadas = false;

  sesiones: SesionHorario[] = [];
  error = '';
  dia = etiquetaDia;
  hora = formatoHora;

  ngOnInit(): void {
    this.cursosApi.listar().subscribe({ next: (c) => (this.cursos = c), error: (e) => (this.error = mensajeError(e)) });
    this.docentesApi.listar().subscribe({ next: (d) => (this.docentes = d), error: (e) => (this.error = mensajeError(e)) });
    this.sedesApi.listar().subscribe({ next: (s) => (this.sedes = s), error: (e) => (this.error = mensajeError(e)) });
    this.aulasApi.listar().subscribe({ next: (a) => (this.aulas = a), error: (e) => (this.error = mensajeError(e)) });
    this.consultar();
  }

  consultar(): void {
    this.error = '';
    this.api.listar({
      semestre: this.semestre, cursoId: this.cursoId, docenteId: this.docenteId,
      sedeId: this.sedeId, aulaId: this.aulaId, soloUbicadas: this.soloUbicadas,
    }).subscribe({
      next: (s) => (this.sesiones = s),
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  limpiarFiltros(): void {
    this.semestre = this.cursoId = this.docenteId = this.sedeId = this.aulaId = null;
    this.soloUbicadas = false;
    this.consultar();
  }

  exportar(): void {
    const filas: (string | number | null | undefined)[][] = [
      ['Semestre', 'Código', 'Curso', 'Grupo', 'Sesión', 'Día', 'Inicio', 'Fin', 'Aula', 'Sede', 'Docente', 'Estado'],
      ...this.sesiones.map((s) => [
        s.semestre, s.cursoCodigo, s.cursoNombre, s.numeroGrupo, s.numeroSesion,
        s.ubicada ? etiquetaDia(s.dia) : '', s.ubicada ? formatoHora(s.horaInicio) : '', s.ubicada ? formatoHora(s.horaFin) : '',
        s.aulaNombre, s.sedeNombre, s.docenteNombre, s.ubicada ? 'Ubicada' : 'Sin ubicar',
      ]),
    ];
    descargarCsv('horario.csv', filas);
  }
}
