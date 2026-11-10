import { Component, input } from '@angular/core';

import { SesionHorarioModel } from '../../../models/sesion-horario.model';

/** Tarjeta de una sesión de clase, usada en el calendario y en la bandeja. */
@Component({
  selector: 'app-tarjeta-sesion',
  standalone: false,
  templateUrl: './tarjeta-sesion.component.html',
  styleUrl: './tarjeta-sesion.component.css',
  host: { '[attr.data-semestre]': 'sesion().semestre' },
})
export class TarjetaSesionComponent {
  readonly sesion = input.required<SesionHorarioModel>();
  readonly compacta = input<boolean>(false);
  readonly mostrarCausa = input<boolean>(false);
}
