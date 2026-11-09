import { Component, input } from '@angular/core';

import { PasoTrazaModel } from '../../../../models/paso-traza.model';
import { REGLAS_MOTOR } from '../../constants/motor-explicado.constants';

/** Cuántas franjas descartó cada regla en un paso, en el orden en que el motor las revisa. */
@Component({
  selector: 'app-barras-bloqueo',
  standalone: false,
  templateUrl: './barras-bloqueo.component.html',
  styleUrl: './barras-bloqueo.component.css',
})
export class BarrasBloqueoComponent {
  readonly paso = input.required<PasoTrazaModel>();
  protected readonly reglas = REGLAS_MOTOR;
}
