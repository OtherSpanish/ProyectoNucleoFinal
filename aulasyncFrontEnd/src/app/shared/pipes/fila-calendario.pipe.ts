import { Pipe, PipeTransform } from '@angular/core';

import { SesionHorarioModel } from '../../models/sesion-horario.model';

/** Ubicación CSS grid de una sesión: fila de inicio / cantidad de horas. */
@Pipe({ name: 'filaCalendario', standalone: false })
export class FilaCalendarioPipe implements PipeTransform {
  transform(sesion: SesionHorarioModel, horaMinima: number): string {
    const inicio = (sesion.horaInicio ?? horaMinima) - horaMinima + 1;
    return `${inicio} / span ${sesion.duracionHoras}`;
  }
}
