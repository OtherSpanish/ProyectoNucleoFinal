import { Component, computed, input } from '@angular/core';

import { CATALOGO_DIAS } from '../../../../core/constants/catalogos.constants';
import { ItemLeyendaModel } from '../../../../models/item-leyenda.model';
import { FiltroAristasModel, ModoColorModel } from '../../../../models/modo-color.model';
import { TrazaMotorModel } from '../../../../models/traza-motor.model';
import {
  COLOR_ACTUAL,
  COLOR_ARISTA_BLOQUE,
  COLOR_ARISTA_DOCENTE,
  COLOR_ARISTA_GRUPO,
  COLOR_PENDIENTE,
  COLOR_SIN_UBICAR,
  COLORES_BLOQUE,
  COLORES_DIA,
  COLORES_SEMESTRE,
  ESCALA_GRADO,
} from '../../constants/motor-explicado.constants';

/** Explica qué significa cada color y cada tipo de línea en la etapa actual. */
@Component({
  selector: 'app-leyenda-grafo',
  standalone: false,
  templateUrl: './leyenda-grafo.component.html',
  styleUrl: './leyenda-grafo.component.css',
})
export class LeyendaGrafoComponent {
  readonly modo = input.required<ModoColorModel>();
  readonly aristas = input.required<FiltroAristasModel>();
  readonly traza = input.required<TrazaMotorModel>();
  readonly marcarRaices = input<boolean>(false);

  protected readonly puntos = computed<ItemLeyendaModel[]>(() => {
    switch (this.modo()) {
      case 'semestre':
        return this.lista(COLORES_SEMESTRE, 'Semestre ');
      case 'bloque':
        return this.lista(COLORES_BLOQUE, 'G').concat([{ color: 'transparent', etiqueta: '(el color se repite cada 6)' }]);
      case 'grado':
        return this.escalaGrado();
      case 'dsatur':
        return [
          { color: COLOR_PENDIENTE, etiqueta: 'Pendiente' },
          { color: COLOR_ACTUAL, etiqueta: 'Elegida en este paso' },
          { color: COLORES_DIA['MONDAY'], etiqueta: 'Ubicada (color del día)' },
          { color: COLOR_SIN_UBICAR, etiqueta: 'Sin ubicar' },
        ];
      case 'dia':
        return this.dias();
      default:
        return [];
    }
  });

  protected readonly lineas = computed<ItemLeyendaModel[]>(() => {
    const filtro = this.aristas();
    const items: ItemLeyendaModel[] = [];
    if (filtro === 'docente' || filtro === 'todas' || filtro === 'actual') {
      items.push({ color: COLOR_ARISTA_DOCENTE, etiqueta: 'Mismo docente', forma: 'linea' });
    }
    if (filtro === 'grupo' || filtro === 'todas' || filtro === 'actual') {
      items.push({ color: COLOR_ARISTA_GRUPO, etiqueta: 'Mismo grupo', forma: 'linea' });
    }
    if (filtro === 'bloque' || filtro === 'todas' || filtro === 'actual') {
      items.push({ color: COLOR_ARISTA_BLOQUE, etiqueta: 'Mismo bloque', forma: 'linea' });
    }
    return items;
  });

  private lista(colores: string[], prefijo: string): ItemLeyendaModel[] {
    const items: ItemLeyendaModel[] = [];
    for (let i = 0; i < colores.length; i++) {
      items.push({ color: colores[i], etiqueta: prefijo + (i + 1) });
    }
    return items;
  }

  private escalaGrado(): ItemLeyendaModel[] {
    const items: ItemLeyendaModel[] = [];
    for (let i = 0; i < ESCALA_GRADO.length; i++) {
      const siguiente = ESCALA_GRADO[i + 1];
      const etiqueta = siguiente ? `${ESCALA_GRADO[i][0]}–${siguiente[0] - 1} vecinos` : `${ESCALA_GRADO[i][0]}+ vecinos`;
      items.push({ color: ESCALA_GRADO[i][1], etiqueta });
    }
    return items;
  }

  private dias(): ItemLeyendaModel[] {
    const items: ItemLeyendaModel[] = [];
    for (const dia of CATALOGO_DIAS) {
      items.push({ color: COLORES_DIA[dia.valor], etiqueta: dia.etiqueta });
    }
    items.push({ color: COLOR_SIN_UBICAR, etiqueta: 'Sin ubicar' });
    return items;
  }
}
