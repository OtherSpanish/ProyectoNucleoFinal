import { Component, model } from '@angular/core';

import { CATALOGO_DIAS, HORAS_JORNADA } from '../../../core/constants/catalogos.constants';
import { FranjaHorariaModel } from '../../../models/franja-horaria.model';

/**
 * Tabla editable de franjas (día, hora inicio, hora fin). No valida nada:
 * el backend responde si una franja es inválida o se traslapa.
 */
@Component({
  selector: 'app-editor-franjas',
  standalone: false,
  templateUrl: './editor-franjas.component.html',
  styleUrl: './editor-franjas.component.css',
})
export class EditorFranjasComponent {
  readonly franjas = model.required<FranjaHorariaModel[]>();

  protected readonly dias = CATALOGO_DIAS;
  protected readonly horas = HORAS_JORNADA;

  agregar(): void {
    const nueva: FranjaHorariaModel = { id: null, dia: 'MONDAY', horaInicio: 7, horaFin: 9 };
    this.franjas.set([...this.franjas(), nueva]);
  }

  quitar(indice: number): void {
    const restantes: FranjaHorariaModel[] = [];
    const actuales = this.franjas();
    for (let i = 0; i < actuales.length; i++) {
      if (i !== indice) {
        restantes.push(actuales[i]);
      }
    }
    this.franjas.set(restantes);
  }
}
