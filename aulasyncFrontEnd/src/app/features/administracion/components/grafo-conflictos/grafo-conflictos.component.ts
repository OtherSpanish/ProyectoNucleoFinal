import { Component, computed, input, output, signal } from '@angular/core';

import { AristaTrazaModel } from '../../../../models/arista-traza.model';
import { FiltroAristasModel, ModoColorModel } from '../../../../models/modo-color.model';
import { NodoTrazaModel } from '../../../../models/nodo-traza.model';
import { PosicionNodoModel } from '../../../../models/posicion-nodo.model';
import { TrazaMotorModel } from '../../../../models/traza-motor.model';
import {
  COLOR_ARISTA_BLOQUE,
  COLOR_ARISTA_DOCENTE,
  COLOR_ARISTA_GRUPO,
} from '../../constants/motor-explicado.constants';

export const ANCHO_LIENZO = 1000;
export const ALTO_LIENZO = 820;

interface CajaVista {
  x: number;
  y: number;
  ancho: number;
  alto: number;
}

/**
 * Dibuja el grafo de conflictos que envía el backend. Solo pinta: qué aristas
 * existen, a qué componente pertenece cada nodo y en qué paso se ubicó vienen
 * en la traza.
 */
@Component({
  selector: 'app-grafo-conflictos',
  standalone: false,
  templateUrl: './grafo-conflictos.component.html',
  styleUrl: './grafo-conflictos.component.css',
})
export class GrafoConflictosComponent {
  readonly traza = input.required<TrazaMotorModel>();
  readonly posiciones = input.required<PosicionNodoModel[]>();
  readonly modoColor = input.required<ModoColorModel>();
  readonly filtroAristas = input.required<FiltroAristasModel>();
  readonly pasoActual = input<number>(0);
  readonly seleccionado = input<number | null>(null);
  readonly mostrarEtiquetas = input<boolean>(false);
  readonly marcarRaices = input<boolean>(false);
  readonly seleccionar = output<number | null>();

  protected readonly caja = signal<CajaVista>({ x: 0, y: 0, ancho: ANCHO_LIENZO, alto: ALTO_LIENZO });
  protected readonly vistaCaja = computed(() => {
    const c = this.caja();
    return `${c.x} ${c.y} ${c.ancho} ${c.alto}`;
  });

  /** Nodo que DSatur está procesando en el paso actual (-1 si ninguno). */
  protected readonly nodoActual = computed(() => {
    const paso = this.traza().pasos[this.pasoActual() - 1];
    return this.filtroAristas() === 'actual' && paso ? paso.nodo : -1;
  });

  /** Nodos vecinos del seleccionado y del actual, para resaltarlos. */
  protected readonly resaltados = computed(() => {
    const conjunto = new Set<number>();
    const nodos = this.traza().nodos;
    const claves = [this.seleccionado(), this.nodoActual()];
    for (const clave of claves) {
      if (clave !== null && clave >= 0) {
        conjunto.add(clave);
        this.agregar(conjunto, nodos[clave].vecinosDocente);
        this.agregar(conjunto, nodos[clave].vecinosGrupo);
        this.agregar(conjunto, nodos[clave].vecinosBloque);
      }
    }
    return conjunto;
  });

  protected readonly aristasVisibles = computed(() => {
    const visibles: AristaTrazaModel[] = [];
    const filtro = this.filtroAristas();
    const elegido = this.seleccionado();
    const actual = this.nodoActual();
    for (const arista of this.traza().aristas) {
      const tocaElegido = elegido !== null && (arista.origen === elegido || arista.destino === elegido);
      const tocaActual = arista.origen === actual || arista.destino === actual;
      if (tocaElegido || tocaActual || this.pasaFiltro(arista, filtro)) {
        visibles.push(arista);
      }
    }
    return visibles;
  });

  private arrastre: { x: number; y: number; movido: boolean } | null = null;
  private ultimoArrastreMovido = false;

  radio(nodo: NodoTrazaModel): number {
    return 11 + Math.min(nodo.grado, 24) * 0.5;
  }

  colorArista(arista: AristaTrazaModel): string {
    const filtro = this.filtroAristas();
    if (filtro === 'docente') {
      return COLOR_ARISTA_DOCENTE;
    }
    if (filtro === 'grupo') {
      return COLOR_ARISTA_GRUPO;
    }
    if (filtro === 'bloque') {
      return COLOR_ARISTA_BLOQUE;
    }
    if (arista.mismoGrupo) {
      return COLOR_ARISTA_GRUPO;
    }
    if (arista.mismoDocente) {
      return COLOR_ARISTA_DOCENTE;
    }
    return COLOR_ARISTA_BLOQUE;
  }

  tocaSeleccion(arista: AristaTrazaModel): boolean {
    const elegido = this.seleccionado();
    const actual = this.nodoActual();
    return (
      arista.origen === elegido || arista.destino === elegido || arista.origen === actual || arista.destino === actual
    );
  }

  etiquetaVisible(nodo: NodoTrazaModel): boolean {
    return this.mostrarEtiquetas() || nodo.indice === this.seleccionado() || nodo.indice === this.nodoActual();
  }

  alClicNodo(evento: MouseEvent, nodo: NodoTrazaModel): void {
    evento.stopPropagation();
    if (this.ultimoArrastreMovido) {
      return;
    }
    this.seleccionar.emit(nodo.indice === this.seleccionado() ? null : nodo.indice);
  }

  alClicFondo(): void {
    if (!this.ultimoArrastreMovido) {
      this.seleccionar.emit(null);
    }
  }

  acercar(factor: number): void {
    const c = this.caja();
    const ancho = Math.min(ANCHO_LIENZO * 1.6, Math.max(ANCHO_LIENZO / 6, c.ancho / factor));
    const alto = (ancho * ALTO_LIENZO) / ANCHO_LIENZO;
    this.caja.set({ x: c.x + (c.ancho - ancho) / 2, y: c.y + (c.alto - alto) / 2, ancho, alto });
  }

  restablecerVista(): void {
    this.caja.set({ x: 0, y: 0, ancho: ANCHO_LIENZO, alto: ALTO_LIENZO });
  }

  alGirarRueda(evento: WheelEvent): void {
    evento.preventDefault();
    this.acercar(evento.deltaY < 0 ? 1.15 : 1 / 1.15);
  }

  alPresionar(evento: PointerEvent): void {
    this.arrastre = { x: evento.clientX, y: evento.clientY, movido: false };
    this.ultimoArrastreMovido = false;
  }

  alMover(evento: PointerEvent, lienzo: HTMLElement): void {
    if (this.arrastre === null) {
      return;
    }
    const difX = evento.clientX - this.arrastre.x;
    const difY = evento.clientY - this.arrastre.y;
    if (!this.arrastre.movido && Math.abs(difX) + Math.abs(difY) < 4) {
      return;
    }
    this.arrastre.movido = true;
    const c = this.caja();
    const escala = c.ancho / lienzo.clientWidth;
    this.caja.set({ ...c, x: c.x - difX * escala, y: c.y - difY * escala });
    this.arrastre.x = evento.clientX;
    this.arrastre.y = evento.clientY;
  }

  alSoltar(): void {
    this.ultimoArrastreMovido = this.arrastre !== null && this.arrastre.movido;
    this.arrastre = null;
  }

  private pasaFiltro(arista: AristaTrazaModel, filtro: FiltroAristasModel): boolean {
    switch (filtro) {
      case 'docente':
        return arista.mismoDocente;
      case 'grupo':
        return arista.mismoGrupo;
      case 'bloque':
        return arista.mismoBloque;
      case 'todas':
        return true;
      default:
        return false;
    }
  }

  private agregar(conjunto: Set<number>, indices: number[]): void {
    for (const indice of indices) {
      conjunto.add(indice);
    }
  }
}
