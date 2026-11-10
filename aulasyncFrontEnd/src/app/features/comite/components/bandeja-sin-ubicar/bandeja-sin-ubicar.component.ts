import { Component, inject, input, output, signal } from '@angular/core';

import { SesionHorarioModel } from '../../../../models/sesion-horario.model';
import { ArrastreSesionService } from '../../../../shared/services/arrastre-sesion.service';

/**
 * Sesiones que no tienen lugar en el horario, con la causa que dio el motor.
 * Se arrastran al calendario para ubicarlas, y una sesión del calendario que
 * se suelte aquí se retira del horario.
 */
@Component({
  selector: 'app-bandeja-sin-ubicar',
  standalone: false,
  templateUrl: './bandeja-sin-ubicar.component.html',
  styleUrl: './bandeja-sin-ubicar.component.css',
})
export class BandejaSinUbicarComponent {
  private readonly arrastre = inject(ArrastreSesionService);

  readonly sesiones = input.required<SesionHorarioModel[]>();
  readonly retirar = output<SesionHorarioModel>();

  protected readonly encima = signal(false);

  alIniciarArrastre(evento: DragEvent, sesion: SesionHorarioModel): void {
    if (evento.dataTransfer) {
      evento.dataTransfer.setData('text/plain', String(sesion.id));
      evento.dataTransfer.effectAllowed = 'move';
    }
    this.arrastre.iniciar(sesion);
  }

  alTerminarArrastre(): void {
    this.arrastre.terminar();
  }

  alArrastrarSobre(evento: DragEvent): void {
    evento.preventDefault();
    this.encima.set(true);
  }

  alSoltar(evento: DragEvent): void {
    evento.preventDefault();
    this.encima.set(false);
    const sesion = this.arrastre.recibir(evento);
    if (sesion !== null && sesion.ubicada) {
      this.retirar.emit(sesion);
    }
  }
}
