import { Component, Input } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';

/** Mensajes de error / éxito reutilizables. `lista` agrega viñetas (por ejemplo, los motivos de un conflicto). */
@Component({
  selector: 'app-alerta',
  standalone: true,
  imports: [NgIf, NgFor],
  template: `
    <div class="alerta error" *ngIf="error" role="alert">
      {{ error }}
      <ul *ngIf="lista?.length"><li *ngFor="let l of lista">{{ l }}</li></ul>
    </div>
    <div class="alerta ok" *ngIf="ok" role="status">{{ ok }}</div>
  `,
})
export class AlertaComponent {
  @Input() error = '';
  @Input() ok = '';
  @Input() lista: string[] | null = null;
}
