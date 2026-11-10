import { Component, HostListener, input, output } from '@angular/core';

/** Ventana emergente centrada para ver el detalle de un registro. Se cierra con Esc o clic afuera. */
@Component({
  selector: 'app-ventana-detalle',
  standalone: false,
  templateUrl: './ventana-detalle.component.html',
  styleUrl: './ventana-detalle.component.css',
})
export class VentanaDetalleComponent {
  readonly titulo = input.required<string>();
  readonly subtitulo = input<string>('');
  readonly abierta = input<boolean>(false);
  readonly cargando = input<boolean>(false);
  readonly cerrar = output<void>();

  @HostListener('document:keydown.escape')
  alPresionarEscape(): void {
    if (this.abierta()) {
      this.cerrar.emit();
    }
  }
}
