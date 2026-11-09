import { Pipe, PipeTransform } from '@angular/core';

import { CATALOGOS } from '../../core/constants/catalogos.constants';

/** Traduce el valor de un enum del backend a su etiqueta: 'SALA_COMPUTO' | etiqueta:'tipoAula'. */
@Pipe({ name: 'etiqueta', standalone: false })
export class EtiquetaPipe implements PipeTransform {
  transform(valor: string | null | undefined, catalogo: string): string {
    if (!valor) {
      return '—';
    }
    for (const opcion of CATALOGOS[catalogo] ?? []) {
      if (opcion.valor === valor) {
        return opcion.etiqueta;
      }
    }
    return valor;
  }
}
