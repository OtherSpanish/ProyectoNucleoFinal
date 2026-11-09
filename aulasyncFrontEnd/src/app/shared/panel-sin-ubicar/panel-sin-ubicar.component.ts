import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { SesionHorario, describirMotivo } from '../../core/models';
import { DragService } from '../drag.service';

/** Lista las sesiones sin ubicar con la causa por la que el motor (o el comité) las dejó fuera del horario. */
@Component({
  selector: 'app-panel-sin-ubicar',
  standalone: true,
  imports: [NgFor, NgIf],
  template: `
    <div class="card">
      <h3>Sesiones sin ubicar ({{ sesiones.length }})</h3>
      <p *ngIf="sesiones.length === 0" class="vacio">No hay sesiones sin ubicar.</p>
      <p *ngIf="arrastrable && sesiones.length > 0" class="ayuda">Arrastra una sesión hacia el calendario para ubicarla.</p>

      <ul class="lista-sesiones">
        <li
          *ngFor="let s of sesiones"
          [attr.draggable]="arrastrable"
          [class.arrastrable]="arrastrable"
          [class.seleccionada]="s.id === seleccionadaId"
          (dragstart)="alIniciarArrastre($event, s)"
          (dragend)="drag.sesionId.set(null)"
          (click)="seleccionar.emit(s.id)"
        >
          <strong>{{ s.cursoCodigo }} · G{{ s.numeroGrupo }} · sesión {{ s.numeroSesion }}</strong>
          <small>{{ s.cursoNombre }} · Sem. {{ s.semestre }} · {{ s.duracionHoras }} h</small>
          <small>{{ s.docenteNombre }}</small>
          <small class="causa" *ngIf="s.causa">Causa: {{ causa(s.causa) }}</small>
          <small class="causa" *ngIf="s.detalleCausa">{{ s.detalleCausa }}</small>
        </li>
      </ul>
    </div>
  `,
})
export class PanelSinUbicarComponent {
  drag = inject(DragService);

  @Input() sesiones: SesionHorario[] = [];
  @Input() arrastrable = false;
  @Input() seleccionadaId: number | null = null;
  @Output() seleccionar = new EventEmitter<number>();

  causa = describirMotivo;

  alIniciarArrastre(ev: DragEvent, s: SesionHorario): void {
    if (!this.arrastrable) return;
    ev.dataTransfer?.setData('text/plain', String(s.id));
    if (ev.dataTransfer) ev.dataTransfer.effectAllowed = 'move';
    this.drag.sesionId.set(s.id);
    this.seleccionar.emit(s.id);
  }
}
