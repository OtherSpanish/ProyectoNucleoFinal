import { Pipe, PipeTransform } from '@angular/core';

/** Filas del calendario: 7 | rangoHoras:22 → [7, 8, ..., 21]. */
@Pipe({ name: 'rangoHoras', standalone: false })
export class RangoHorasPipe implements PipeTransform {
  transform(desde: number, hasta: number): number[] {
    const horas: number[] = [];
    for (let hora = desde; hora < hasta; hora++) {
      horas.push(hora);
    }
    return horas;
  }
}
