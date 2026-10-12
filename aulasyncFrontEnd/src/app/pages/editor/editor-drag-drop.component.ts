import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/data.service';
import { ScheduleService } from '../../core/schedule.service';
import { DIAS, ResultadoValidacion, Sesion } from '../../core/models';
import { CalendarioSemanalComponent } from '../../shared/calendario-semanal/calendario-semanal.component';
import { PanelSinUbicarComponent } from '../../shared/panel-sin-ubicar/panel-sin-ubicar.component';

interface Alerta {
  tipo: 'ok' | 'error';
  titulo: string;
  resultado: ResultadoValidacion;
}

/** CU06 y CU07 – Mover sesiones (franja o aula) y recibir alerta inmediata de conflictos (RF4, RF5). */
@Component({
  selector: 'app-editor-drag-drop',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, CalendarioSemanalComponent, PanelSinUbicarComponent],
  templateUrl: './editor-drag-drop.component.html',
})
export class EditorDragDropComponent {
  schedule = inject(ScheduleService);
  data = inject(DataService);

  seleccionId: string | null = null;
  aulaNueva = '';
  alerta: Alerta | null = null;

  get seleccionada(): Sesion | undefined {
    return this.schedule.buscar(this.seleccionId);
  }

  seleccionar(id: string): void {
    this.seleccionId = id;
    this.aulaNueva = this.schedule.buscar(id)?.aulaId ?? '';
  }

  alSoltar(e: { sesionId: string; dia: number; hora: number }): void {
    const s = this.schedule.buscar(e.sesionId);
    if (!s) return;
    const r = this.schedule.mover(e.sesionId, e.dia, e.hora);
    this.seleccionar(e.sesionId);
    this.alerta = r.valido
      ? { tipo: 'ok', titulo: `${this.schedule.etiqueta(s)} movida a ${DIAS[e.dia - 1]} ${e.hora}:00. Cambio registrado en la auditoría.`, resultado: r }
      : { tipo: 'error', titulo: `No se pudo mover ${this.schedule.etiqueta(s)}:`, resultado: r };
  }

  cambiarAula(): void {
    const s = this.seleccionada;
    if (!s || !this.aulaNueva) return;
    const r = this.schedule.cambiarAula(s.id, this.aulaNueva);
    this.alerta = r.valido
      ? { tipo: 'ok', titulo: `Aula de ${this.schedule.etiqueta(s)} cambiada a ${this.data.nombreAula(this.aulaNueva)}.`, resultado: r }
      : { tipo: 'error', titulo: 'No se pudo cambiar el aula:', resultado: r };
  }

  retirar(): void {
    const s = this.seleccionada;
    if (!s) return;
    this.schedule.retirar(s.id);
    this.alerta = null;
  }
}
