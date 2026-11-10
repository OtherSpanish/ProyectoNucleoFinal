import { Component, input, output } from '@angular/core';

/** Panel que se desliza desde la derecha con un formulario o detalle. */
@Component({
  selector: 'app-panel-lateral',
  standalone: false,
  templateUrl: './panel-lateral.component.html',
  styleUrl: './panel-lateral.component.css',
})
export class PanelLateralComponent {
  readonly titulo = input.required<string>();
  readonly abierto = input<boolean>(false);
  readonly ancho = input<string>('480px');
  readonly cerrar = output<void>();
}
