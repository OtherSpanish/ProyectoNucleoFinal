import { Component, OnInit, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { PortalDocenteApi } from '../../core/api';
import { DisponibilidadDocente, etiquetaDedicacion } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';
import { EditorFranjasComponent } from '../../shared/editor-franjas/editor-franjas.component';

/** Disponibilidad del docente (GET /api/mi/disponibilidad). Solo lectura: la define el administrador. */
@Component({
  selector: 'app-mi-disponibilidad',
  standalone: true,
  imports: [NgIf, AlertaComponent, EditorFranjasComponent],
  template: `
    <h2>Mi disponibilidad</h2>
    <app-alerta [error]="error"></app-alerta>
    <ng-container *ngIf="disp">
      <p class="resumen">
        {{ disp.nombreDocente }} · {{ dedicacion(disp.dedicacion) }} ·
        Horas disponibles: <strong>{{ disp.horasDisponibles }}</strong> ·
        Tope semanal: <strong>{{ disp.horasMaximas }}</strong>
      </p>
      <p class="ayuda">Si necesitas cambiar tu disponibilidad, solicítalo al administrador.</p>
      <div class="card"><app-editor-franjas [franjas]="disp.franjas" [editable]="false"></app-editor-franjas></div>
    </ng-container>
  `,
})
export class MiDisponibilidadComponent implements OnInit {
  private api = inject(PortalDocenteApi);

  disp: DisponibilidadDocente | null = null;
  error = '';
  dedicacion = etiquetaDedicacion;

  ngOnInit(): void {
    this.api.disponibilidad().subscribe({
      next: (d) => (this.disp = d),
      error: (e) => (this.error = mensajeError(e)),
    });
  }
}
