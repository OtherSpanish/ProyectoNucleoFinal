import { Component, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ScheduleService } from '../../core/schedule.service';
import { PanelSinUbicarComponent } from '../../shared/panel-sin-ubicar/panel-sin-ubicar.component';

/** CU04 – Generar la propuesta inicial y ver las sesiones sin ubicar (RF2, CA1). */
@Component({
  selector: 'app-generar-propuesta',
  standalone: true,
  imports: [NgIf, RouterLink, PanelSinUbicarComponent],
  templateUrl: './generar-propuesta.component.html',
})
export class GenerarPropuestaComponent {
  schedule = inject(ScheduleService);
  generada = false;
  duracionMs = 0;

  generar(): void {
    if (this.schedule.sesiones().length > 0 && !confirm('Se reemplazará el horario actual, incluidos los ajustes manuales. ¿Continuar?')) {
      return;
    }
    const t0 = performance.now();
    this.schedule.generarPropuesta();
    this.duracionMs = Math.round(performance.now() - t0);
    this.generada = true;
  }
}
