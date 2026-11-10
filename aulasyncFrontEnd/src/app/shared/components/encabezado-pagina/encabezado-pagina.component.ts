import { Component, input } from '@angular/core';

@Component({
  selector: 'app-encabezado-pagina',
  standalone: false,
  templateUrl: './encabezado-pagina.component.html',
  styleUrl: './encabezado-pagina.component.css',
})
export class EncabezadoPaginaComponent {
  readonly titulo = input.required<string>();
  readonly descripcion = input<string>('');
}
