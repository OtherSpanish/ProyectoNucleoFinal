import { Pipe, PipeTransform, inject } from '@angular/core';

import { CatalogosModel } from '../../models/catalogos.model';
import { CatalogoService } from '../../services/catalogo.service';

/**
 * Nombre para mostrar de un valor de enum, tomado de GET /api/catalogos:
 * 'SALA_COMPUTO' | etiqueta: 'tiposAula' → «Sala de cómputo».
 * Es impuro para refrescarse cuando llegan los catálogos.
 */
@Pipe({ name: 'etiqueta', standalone: false, pure: false })
export class EtiquetaPipe implements PipeTransform {
  private readonly catalogoService = inject(CatalogoService);

  transform(valor: string | null | undefined, catalogo: keyof Omit<CatalogosModel, 'horasJornada'>): string {
    if (!valor) {
      return '—';
    }
    for (const opcion of this.catalogoService.opciones(catalogo)) {
      if (opcion.valor === valor) {
        return opcion.etiqueta;
      }
    }
    return valor;
  }
}
