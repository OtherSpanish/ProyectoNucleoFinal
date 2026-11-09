import { Component, input } from '@angular/core';

@Component({
  selector: 'app-estado-vacio',
  standalone: false,
  template: `<div class="vacio"><app-icono [nombre]="icono()" [tamano]="28" /><p>{{ mensaje() }}</p></div>`,
  styles: [
    `.vacio { display: grid; place-items: center; gap: 8px; padding: 40px 16px; color: var(--texto-suave);
      text-align: center; border: 1.5px dashed var(--borde); border-radius: var(--radio); }
     p { margin: 0; max-width: 46ch; }`,
  ],
})
export class EstadoVacioComponent {
  readonly mensaje = input<string>('No hay registros.');
  readonly icono = input<string>('bandeja');
}
