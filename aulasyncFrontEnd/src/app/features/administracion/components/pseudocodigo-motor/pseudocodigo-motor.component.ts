import { Component, input } from '@angular/core';

import { PSEUDOCODIGO_MOTOR } from '../../constants/motor-explicado.constants';

/** Pseudocódigo del motor con las líneas que se están ejecutando resaltadas (como en VisuAlgo). */
@Component({
  selector: 'app-pseudocodigo-motor',
  standalone: false,
  templateUrl: './pseudocodigo-motor.component.html',
  styleUrl: './pseudocodigo-motor.component.css',
})
export class PseudocodigoMotorComponent {
  readonly lineasActivas = input.required<number[]>();
  readonly lineaPrincipal = input.required<number>();
  protected readonly lineas = PSEUDOCODIGO_MOTOR;
}
