import { Pipe, PipeTransform, inject } from '@angular/core';

import { ModoColorModel } from '../../../models/modo-color.model';
import { NodoTrazaModel } from '../../../models/nodo-traza.model';
import { CatalogoService } from '../../../services/catalogo.service';
import {
  COLOR_ACTUAL,
  COLOR_PENDIENTE,
  COLOR_SIN_UBICAR,
  COLORES_BLOQUE,
  COLORES_DIA,
  COLORES_SEMESTRE,
  ESCALA_GRADO,
} from '../constants/motor-explicado.constants';

/** Color con el que se pinta un nodo según el criterio elegido (solo presentación). */
@Pipe({ name: 'colorNodo', standalone: false })
export class ColorNodoPipe implements PipeTransform {
  private readonly catalogoService = inject(CatalogoService);

  transform(nodo: NodoTrazaModel, modo: ModoColorModel, pasoActual: number): string {
    switch (modo) {
      case 'semestre':
        return COLORES_SEMESTRE[(nodo.semestre - 1) % COLORES_SEMESTRE.length];
      case 'docente':
        return this.categoria(nodo.docenteId);
      case 'grupo':
        return this.categoria(nodo.raizDsu * 5 + 2);
      case 'bloque':
        return COLORES_BLOQUE[(nodo.numeroGrupo - 1) % COLORES_BLOQUE.length];
      case 'grado':
        return this.porGrado(nodo.grado);
      case 'dsatur':
        return this.porEstado(nodo, pasoActual);
      default:
        return this.porDia(nodo);
    }
  }

  /** Tono distinto para cada clave usando el ángulo áureo. */
  private categoria(clave: number): string {
    const tono = Math.round((clave * 137.508) % 360);
    return `hsl(${tono} 55% 45%)`;
  }

  private porGrado(grado: number): string {
    let color = ESCALA_GRADO[0][1];
    for (const escalon of ESCALA_GRADO) {
      if (grado >= escalon[0]) {
        color = escalon[1];
      }
    }
    return color;
  }

  private porEstado(nodo: NodoTrazaModel, pasoActual: number): string {
    if (nodo.orden === pasoActual) {
      return COLOR_ACTUAL;
    }
    if (nodo.orden > pasoActual) {
      return COLOR_PENDIENTE;
    }
    return this.porDia(nodo);
  }

  private porDia(nodo: NodoTrazaModel): string {
    if (!nodo.ubicada || nodo.dia === null) {
      return COLOR_SIN_UBICAR;
    }
    const dias = this.catalogoService.opciones('dias');
    for (let i = 0; i < dias.length; i++) {
      if (dias[i].valor === nodo.dia) {
        return COLORES_DIA[i % COLORES_DIA.length];
      }
    }
    return COLOR_SIN_UBICAR;
  }
}
