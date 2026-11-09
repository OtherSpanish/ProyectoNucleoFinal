import { Component, OnInit, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { PortalDocenteApi } from '../../core/api';
import { CalendarioSemanal, SesionHorario } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';
import { CalendarioSemanalComponent } from '../../shared/calendario-semanal/calendario-semanal.component';
import { PanelSinUbicarComponent } from '../../shared/panel-sin-ubicar/panel-sin-ubicar.component';

/** Horario del docente autenticado: GET /api/mi/horario. */
@Component({
  selector: 'app-mi-horario',
  standalone: true,
  imports: [NgIf, AlertaComponent, CalendarioSemanalComponent, PanelSinUbicarComponent],
  template: `
    <h2>Mi horario</h2>
    <app-alerta [error]="error"></app-alerta>
    <p class="vacio" *ngIf="cargado && cal && cal.totalUbicadas === 0 && cal.totalSinUbicar === 0">
      Todavía no tienes clases en el horario.
    </p>
    <ng-container *ngIf="cal">
      <p class="resumen">Sesiones ubicadas: {{ cal.totalUbicadas }} · Sin ubicar: {{ cal.totalSinUbicar }}</p>
      <div class="card">
        <app-calendario-semanal [sesiones]="sesiones" [horaMin]="cal.horaMinima" [horaMax]="cal.horaMaxima"></app-calendario-semanal>
      </div>
      <app-panel-sin-ubicar *ngIf="cal.sinUbicar.length > 0" [sesiones]="cal.sinUbicar"></app-panel-sin-ubicar>
    </ng-container>
  `,
})
export class MiHorarioComponent implements OnInit {
  private api = inject(PortalDocenteApi);

  cal: CalendarioSemanal | null = null;
  sesiones: SesionHorario[] = [];
  cargado = false;
  error = '';

  ngOnInit(): void {
    this.api.horario().subscribe({
      next: (c) => {
        this.cal = c;
        this.sesiones = c.dias.flatMap((d) => d.sesiones);
        this.cargado = true;
      },
      error: (e) => { this.error = mensajeError(e); this.cargado = true; },
    });
  }
}
