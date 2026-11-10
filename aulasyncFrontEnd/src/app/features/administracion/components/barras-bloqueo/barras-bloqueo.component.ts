import { Component, input } from '@angular/core';

import { PasoTrazaModel } from '../../../../models/paso-traza.model';

/**
 * Cuántas franjas descartó cada regla en un paso. Las reglas, su orden y sus
 * nombres vienen del backend (PasoTraza.bloqueos y GET /api/catalogos).
 */
@Component({
  selector: 'app-barras-bloqueo',
  standalone: false,
  templateUrl: './barras-bloqueo.component.html',
  styleUrl: './barras-bloqueo.component.css',
})
export class BarrasBloqueoComponent {
  readonly paso = input.required<PasoTrazaModel>();
}
