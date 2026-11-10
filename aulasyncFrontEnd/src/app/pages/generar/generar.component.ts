import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HorarioApi } from '../../core/api';
import { ResultadoGeneracion, ResultadoSincronizacion, SesionHorario, describirMotivo } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';
import { CalendarioSemanalComponent } from '../../shared/calendario-semanal/calendario-semanal.component';
import { generadasASesiones, semestresDe } from '../../shared/horario-util';

/**
 * Motor de horarios.
 * - «Previsualizar» → POST /api/motor/generar: propone un horario sin guardarlo.
 * - «Generar y guardar» → POST /api/horarios/generar: genera y reemplaza el horario guardado.
 * - «Sincronizar» → POST /api/horarios/sincronizar: ajusta el horario guardado a los cambios de datos maestros.
 */
@Component({
  selector: 'app-generar',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, RouterLink, AlertaComponent, CalendarioSemanalComponent],
  templateUrl: './generar.component.html',
})
export class GenerarComponent {
  private api = inject(HorarioApi);

  resultado: ResultadoGeneracion | null = null;
  guardado = false;
  sync: ResultadoSincronizacion | null = null;

  ubicadasCal: SesionHorario[] = [];
  sinUbicarCal: SesionHorario[] = [];
  semestres: number[] = [];
  semestreVista: number | null = null;

  trabajando = false;
  error = '';
  ok = '';
  causa = describirMotivo;

  previsualizar(): void { this.generar(false); }

  generarYGuardar(): void {
    if (!confirm('Se generará un horario nuevo y REEMPLAZARÁ el horario guardado (incluidos los ajustes manuales). ¿Continuar?')) return;
    this.generar(true);
  }

  private generar(guardar: boolean): void {
    this.limpiar();
    this.trabajando = true;
    const p = guardar ? this.api.generarYGuardar() : this.api.previsualizar();
    p.subscribe({
      next: (r) => {
        this.trabajando = false;
        this.resultado = r;
        this.guardado = guardar;
        this.ubicadasCal = generadasASesiones(r.ubicadas, true);
        this.sinUbicarCal = generadasASesiones(r.sinUbicar, false);
        this.semestres = semestresDe(this.ubicadasCal);
        this.semestreVista = this.semestres[0] ?? null;
        this.refiltrar();
        this.ok = guardar
          ? 'Horario generado y guardado. Ya puedes revisarlo en el calendario o ajustarlo en el editor.'
          : 'Propuesta generada. Todavía NO está guardada: usa «Generar y guardar» para aplicarla.';
      },
      error: (e) => { this.trabajando = false; this.error = mensajeError(e); },
    });
  }

  sincronizar(): void {
    this.limpiar();
    this.trabajando = true;
    this.api.sincronizar().subscribe({
      next: (r) => {
        this.trabajando = false;
        this.sync = r;
        this.ok = r.hayHorario
          ? `Sincronización lista: ${r.sesionesCreadas} creadas, ${r.sesionesEliminadas} eliminadas, ${r.sesionesDesubicadas} desubicadas.`
          : 'No hay un horario guardado que sincronizar. Genera uno primero.';
      },
      error: (e) => { this.trabajando = false; this.error = mensajeError(e); },
    });
  }

  visibles: SesionHorario[] = [];

  refiltrar(): void {
    this.visibles = this.ubicadasCal.filter((s) => this.semestreVista === null || s.semestre === this.semestreVista);
  }

  private limpiar(): void { this.error = ''; this.ok = ''; this.sync = null; }
}
