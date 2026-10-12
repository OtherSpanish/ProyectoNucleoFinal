import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/data.service';
import { ScheduleService } from '../../core/schedule.service';
import { DIAS, Sesion } from '../../core/models';

/** CU08 y CU09 – Consultar el horario por curso o por docente y exportarlo (RF9, CA8). */
@Component({
  selector: 'app-consulta-horario',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './consulta-horario.component.html',
})
export class ConsultaHorarioComponent {
  schedule = inject(ScheduleService);
  data = inject(DataService);

  modo: 'curso' | 'docente' = 'curso';
  filtroId = '';
  dias = DIAS;

  cambiarModo(m: 'curso' | 'docente'): void {
    this.modo = m;
    this.filtroId = '';
  }

  get filas(): Sesion[] {
    return this.schedule
      .ubicadas()
      .filter((s) => !this.filtroId || (this.modo === 'curso' ? s.cursoId === this.filtroId : s.docenteId === this.filtroId))
      .sort((a, b) => (a.dia! - b.dia!) || (a.hora! - b.hora!));
  }

  exportarCsv(): void {
    const enc = ['Curso', 'Sesión', 'Docente', 'Grupo', 'Día', 'Inicio', 'Fin', 'Aula'];
    const filas = this.filas.map((s) => [
      this.data.nombreCurso(s.cursoId), s.numero, this.data.nombreDocente(s.docenteId), this.data.nombreGrupo(s.grupoId),
      DIAS[s.dia! - 1], `${s.hora}:00`, `${s.hora! + s.duracion}:00`, this.data.nombreAula(s.aulaId),
    ]);
    const csv = [enc, ...filas].map((f) => f.map((c) => `"${String(c).replace(/"/g, '""')}"`).join(',')).join('\n');
    const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `horario_por_${this.modo}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }
}
