import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { ScheduleService } from '../../core/schedule.service';
import { DataService } from '../../core/data.service';
import { Sesion } from '../../core/models';

/** Lista las sesiones sin ubicar con la causa del bloqueo (RF2, CU04). */
@Component({
  selector: 'app-panel-sin-ubicar',
  standalone: true,
  imports: [NgFor, NgIf],
  templateUrl: './panel-sin-ubicar.component.html',
})
export class PanelSinUbicarComponent {
  schedule = inject(ScheduleService);
  data = inject(DataService);

  /** Si es true, las sesiones se pueden arrastrar hacia el calendario del editor. */
  @Input() arrastrable = false;
  @Output() seleccionar = new EventEmitter<string>();

  alIniciarArrastre(ev: DragEvent, s: Sesion): void {
    if (!this.arrastrable) return;
    ev.dataTransfer?.setData('text/plain', s.id);
    if (ev.dataTransfer) ev.dataTransfer.effectAllowed = 'move';
    this.schedule.arrastrando.set(s.id);
  }

  alTerminarArrastre(): void {
    this.schedule.arrastrando.set(null);
  }
}
