import { Component, computed, input, output } from '@angular/core';

import { TrazaMotorModel } from '../../../../models/traza-motor.model';

/** Resumen compacto de la decisión de DSatur en el paso actual. */
@Component({
  selector: 'app-paso-dsatur',
  standalone: false,
  templateUrl: './paso-dsatur.component.html',
  styleUrl: './paso-dsatur.component.css',
})
export class PasoDsaturComponent {
  readonly traza = input.required<TrazaMotorModel>();
  readonly pasoActual = input.required<number>();
  readonly verNodo = output<number>();

  protected readonly paso = computed(() => this.traza().pasos[this.pasoActual() - 1] ?? null);
}
