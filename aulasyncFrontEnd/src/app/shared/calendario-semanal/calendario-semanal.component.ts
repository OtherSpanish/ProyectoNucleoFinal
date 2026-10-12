import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { NgClass, NgFor } from '@angular/common';
import { DataService } from '../../core/data.service';
import { ScheduleService } from '../../core/schedule.service';
import { DIAS, NUM_DIAS, Sesion } from '../../core/models';

/**
 * Calendario semanal reutilizable (CU05). Con [editable]="true" permite arrastrar y soltar
 * sesiones (CU06) y pinta en verde/rojo la celda sobre la que se arrastra (validación en vivo).
 * Usa el drag-and-drop nativo de HTML5, no requiere librerías.
 */
@Component({
  selector: 'app-calendario-semanal',
  standalone: true,
  imports: [NgFor, NgClass],
  templateUrl: './calendario-semanal.component.html',
})
export class CalendarioSemanalComponent {
  data = inject(DataService);
  schedule = inject(ScheduleService);

  @Input() sesiones: Sesion[] = [];
  @Input() editable = false;
  @Input() seleccionadaId: string | null = null;
  @Output() soltar = new EventEmitter<{ sesionId: string; dia: number; hora: number }>();
  @Output() seleccionar = new EventEmitter<string>();

  dias = DIAS;
  numDias = NUM_DIAS;
  hover = '';

  get inicio(): number {
    const c = this.data.config();
    return Math.min(c.horaInicioSemana, c.horaInicioSabado);
  }
  get horas(): number[] {
    const c = this.data.config();
    const fin = Math.max(c.horaFinSemana, c.horaFinSabado);
    return Array.from({ length: Math.max(fin - this.inicio, 0) }, (_, i) => this.inicio + i);
  }
  get ubicadas(): Sesion[] {
    return this.sesiones.filter((s) => s.estado === 'UBICADA' && s.dia !== undefined && s.hora !== undefined);
  }

  claseCelda(dia: number, hora: number): string {
    if (!this.schedule.horaHabilitada(dia, hora)) return 'off';
    const id = this.schedule.arrastrando();
    if (this.editable && id && this.hover === `${dia}-${hora}`) {
      return this.schedule.evaluar(id, dia, hora).resultado.valido ? 'ok' : 'mal';
    }
    return '';
  }

  // ----- Drag & drop -----
  alIniciarArrastre(ev: DragEvent, s: Sesion): void {
    if (!this.editable) return;
    ev.dataTransfer?.setData('text/plain', s.id);
    if (ev.dataTransfer) ev.dataTransfer.effectAllowed = 'move';
    // Se difiere para que el navegador no cancele el arrastre al desactivar los eventos de la tarjeta.
    setTimeout(() => this.schedule.arrastrando.set(s.id));
  }

  alTerminarArrastre(): void {
    this.schedule.arrastrando.set(null);
    this.hover = '';
  }

  alArrastrarSobre(ev: DragEvent, dia: number, hora: number): void {
    if (!this.editable || !this.schedule.horaHabilitada(dia, hora)) return;
    ev.preventDefault();
    this.hover = `${dia}-${hora}`;
  }

  alSoltar(ev: DragEvent, dia: number, hora: number): void {
    ev.preventDefault();
    const id = ev.dataTransfer?.getData('text/plain') || this.schedule.arrastrando();
    this.hover = '';
    this.schedule.arrastrando.set(null);
    if (id && this.editable && this.schedule.horaHabilitada(dia, hora)) {
      this.soltar.emit({ sesionId: id, dia, hora });
    }
  }
}
