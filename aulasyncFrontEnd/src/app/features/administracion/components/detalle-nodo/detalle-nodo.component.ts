import { Component, computed, input, output } from '@angular/core';

import { TrazaMotorModel } from '../../../../models/traza-motor.model';

/**
 * Explica un nodo: qué restricciones lo unen a otros (docente, grupo, bloque),
 * a qué componente del DSU pertenece y cómo lo evaluó DSatur.
 */
@Component({
  selector: 'app-detalle-nodo',
  standalone: false,
  templateUrl: './detalle-nodo.component.html',
  styleUrl: './detalle-nodo.component.css',
})
export class DetalleNodoComponent {
  readonly traza = input.required<TrazaMotorModel>();
  readonly indice = input.required<number>();
  readonly seleccionar = output<number>();
  readonly verPaso = output<number>();
  readonly cerrar = output<void>();

  protected readonly nodo = computed(() => this.traza().nodos[this.indice()]);
  protected readonly paso = computed(() => this.traza().pasos[this.nodo().orden - 1] ?? null);
}
