import { Component, input } from '@angular/core';

import { ResultadoGeneracionModel } from '../../../../models/resultado-generacion.model';

/** Muestra lo que devolvió el motor: totales, avisos y el desglose de causas de lo que no se ubicó. */
@Component({
  selector: 'app-resumen-generacion',
  standalone: false,
  templateUrl: './resumen-generacion.component.html',
  styleUrl: './resumen-generacion.component.css',
})
export class ResumenGeneracionComponent {
  readonly resultado = input.required<ResultadoGeneracionModel>();
  readonly mostrarUbicadas = input<boolean>(false);
}
