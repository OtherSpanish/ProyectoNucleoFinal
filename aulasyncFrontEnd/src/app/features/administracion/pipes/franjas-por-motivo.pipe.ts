import { Pipe, PipeTransform } from '@angular/core';

import { CausaDescartadaModel } from '../../../models/causa-descartada.model';

/** Franjas que una regla descartó en un paso (0 si no descartó ninguna). */
@Pipe({ name: 'franjasPorMotivo', standalone: false })
export class FranjasPorMotivoPipe implements PipeTransform {
  transform(bloqueos: CausaDescartadaModel[], motivo: string): number {
    for (const bloqueo of bloqueos) {
      if (bloqueo.motivo === motivo) {
        return bloqueo.franjas;
      }
    }
    return 0;
  }
}
