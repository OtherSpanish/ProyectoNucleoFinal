import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HorarioApi } from '../../core/api';
import { SesionHorario } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';
import { CalendarioSemanalComponent } from '../../shared/calendario-semanal/calendario-semanal.component';
import { PanelSinUbicarComponent } from '../../shared/panel-sin-ubicar/panel-sin-ubicar.component';
import { semestresDe } from '../../shared/horario-util';

/** Calendario semanal del horario guardado (GET /api/horarios), con filtros en el navegador. */
@Component({
  selector: 'app-calendario',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent, CalendarioSemanalComponent, PanelSinUbicarComponent],
  templateUrl: './calendario.component.html',
})
export class CalendarioComponent implements OnInit {
  private api = inject(HorarioApi);

  sesiones: SesionHorario[] = [];
  semestres: number[] = [];
  docentes: { id: number; nombre: string }[] = [];
  sedes: { id: number; nombre: string }[] = [];
  numerosGrupo: number[] = [];

  filtroSemestre: number | null = null;
  filtroGrupo: number | null = null;
  filtroDocente: number | null = null;
  filtroSede: number | null = null;

  cargando = true;
  error = '';

  ngOnInit(): void {
    this.api.listar().subscribe({
      next: (s) => {
        this.sesiones = s;
        this.semestres = semestresDe(s);
        this.filtroSemestre = this.semestres[0] ?? null;
        this.numerosGrupo = [...new Set(s.map((x) => x.numeroGrupo))].sort((a, b) => a - b);
        this.docentes = this.unicos(s.map((x) => ({ id: x.docenteId, nombre: x.docenteNombre })));
        this.sedes = this.unicos(s.filter((x) => x.sedeId).map((x) => ({ id: x.sedeId as number, nombre: x.sedeNombre as string })));
        this.refiltrar();
        this.cargando = false;
      },
      error: (e) => { this.error = mensajeError(e); this.cargando = false; },
    });
  }

  private unicos(l: { id: number; nombre: string }[]): { id: number; nombre: string }[] {
    const m = new Map<number, string>();
    l.forEach((x) => m.set(x.id, x.nombre));
    return [...m].map(([id, nombre]) => ({ id, nombre })).sort((a, b) => a.nombre.localeCompare(b.nombre));
  }

  private pasa(s: SesionHorario): boolean {
    return (this.filtroSemestre === null || s.semestre === this.filtroSemestre)
      && (this.filtroGrupo === null || s.numeroGrupo === this.filtroGrupo)
      && (this.filtroDocente === null || s.docenteId === this.filtroDocente)
      && (this.filtroSede === null || s.sedeId === this.filtroSede);
  }

  ubicadas: SesionHorario[] = [];
  sinUbicar: SesionHorario[] = [];

  refiltrar(): void {
    this.ubicadas = this.sesiones.filter((s) => s.ubicada && this.pasa(s));
    this.sinUbicar = this.sesiones.filter((s) => !s.ubicada && this.pasa(s));
  }
}
