import { Injectable } from '@angular/core';

import { PosicionNodoModel } from '../../../models/posicion-nodo.model';
import { TrazaMotorModel } from '../../../models/traza-motor.model';

const ITERACIONES = 320;
const MARGEN = 70;

/**
 * Acomoda los nodos en el lienzo con un algoritmo de fuerzas
 * (Fruchterman-Reingold): los nodos se repelen y las aristas los atraen, así
 * que los conflictos quedan agrupados. Es solo presentación: no decide nada
 * del horario.
 */
@Injectable({ providedIn: 'root' })
export class DisposicionGrafoService {
  calcular(traza: TrazaMotorModel, ancho: number, alto: number): PosicionNodoModel[] {
    const total = traza.nodos.length;
    const posiciones = this.posicionesIniciales(traza, ancho, alto);
    if (total === 0) {
      return posiciones;
    }
    const k = Math.sqrt((ancho * alto) / total) * 0.62;
    let temperatura = ancho / 9;
    const dx: number[] = new Array(total).fill(0);
    const dy: number[] = new Array(total).fill(0);

    for (let iteracion = 0; iteracion < ITERACIONES; iteracion++) {
      for (let i = 0; i < total; i++) {
        dx[i] = 0;
        dy[i] = 0;
      }
      this.repeler(posiciones, dx, dy, k);
      this.atraer(traza, posiciones, dx, dy, k);
      this.centrar(posiciones, dx, dy, ancho, alto);
      this.mover(posiciones, dx, dy, temperatura, ancho, alto);
      temperatura = Math.max(1, temperatura * 0.975);
    }
    this.ajustarAlLienzo(posiciones, ancho, alto);
    return posiciones;
  }

  /** Punto de partida: cada semestre ocupa un sector de una elipse. */
  private posicionesIniciales(traza: TrazaMotorModel, ancho: number, alto: number): PosicionNodoModel[] {
    const posiciones: PosicionNodoModel[] = [];
    for (const nodo of traza.nodos) {
      const angulo = ((nodo.semestre - 1) / 9) * Math.PI * 2 + (nodo.indice % 9) * 0.07;
      const radio = 0.28 + (nodo.indice % 5) * 0.03;
      posiciones.push({
        x: ancho / 2 + Math.cos(angulo) * ancho * radio,
        y: alto / 2 + Math.sin(angulo) * alto * radio,
      });
    }
    return posiciones;
  }

  private repeler(posiciones: PosicionNodoModel[], dx: number[], dy: number[], k: number): void {
    for (let i = 0; i < posiciones.length; i++) {
      for (let j = i + 1; j < posiciones.length; j++) {
        let difX = posiciones[i].x - posiciones[j].x;
        let difY = posiciones[i].y - posiciones[j].y;
        let distancia = Math.sqrt(difX * difX + difY * difY);
        if (distancia < 0.01) {
          difX = 0.01 * ((i % 3) - 1 || 1);
          difY = 0.01;
          distancia = 0.015;
        }
        const fuerza = (k * k) / distancia;
        dx[i] += (difX / distancia) * fuerza;
        dy[i] += (difY / distancia) * fuerza;
        dx[j] -= (difX / distancia) * fuerza;
        dy[j] -= (difY / distancia) * fuerza;
      }
    }
  }

  private atraer(traza: TrazaMotorModel, posiciones: PosicionNodoModel[], dx: number[], dy: number[], k: number): void {
    for (const arista of traza.aristas) {
      const a = posiciones[arista.origen];
      const b = posiciones[arista.destino];
      const difX = a.x - b.x;
      const difY = a.y - b.y;
      const distancia = Math.max(0.01, Math.sqrt(difX * difX + difY * difY));
      const fuerza = ((distancia * distancia) / k) * 0.35;
      dx[arista.origen] -= (difX / distancia) * fuerza;
      dy[arista.origen] -= (difY / distancia) * fuerza;
      dx[arista.destino] += (difX / distancia) * fuerza;
      dy[arista.destino] += (difY / distancia) * fuerza;
    }
  }

  /** Gravedad suave hacia el centro para que los nodos sueltos no se alejen. */
  private centrar(posiciones: PosicionNodoModel[], dx: number[], dy: number[], ancho: number, alto: number): void {
    for (let i = 0; i < posiciones.length; i++) {
      dx[i] += (ancho / 2 - posiciones[i].x) * 0.25;
      dy[i] += (alto / 2 - posiciones[i].y) * 0.25;
    }
  }

  /** Escala el dibujo para que ocupe todo el lienzo, dejando un margen. */
  private ajustarAlLienzo(posiciones: PosicionNodoModel[], ancho: number, alto: number): void {
    let minX = Number.MAX_VALUE;
    let maxX = -Number.MAX_VALUE;
    let minY = Number.MAX_VALUE;
    let maxY = -Number.MAX_VALUE;
    for (const posicion of posiciones) {
      minX = Math.min(minX, posicion.x);
      maxX = Math.max(maxX, posicion.x);
      minY = Math.min(minY, posicion.y);
      maxY = Math.max(maxY, posicion.y);
    }
    const rangoX = Math.max(1, maxX - minX);
    const rangoY = Math.max(1, maxY - minY);
    for (let i = 0; i < posiciones.length; i++) {
      posiciones[i] = {
        x: MARGEN + ((posiciones[i].x - minX) / rangoX) * (ancho - MARGEN * 2),
        y: MARGEN + ((posiciones[i].y - minY) / rangoY) * (alto - MARGEN * 2),
      };
    }
  }

  private mover(
    posiciones: PosicionNodoModel[],
    dx: number[],
    dy: number[],
    temperatura: number,
    ancho: number,
    alto: number,
  ): void {
    for (let i = 0; i < posiciones.length; i++) {
      const magnitud = Math.max(0.01, Math.sqrt(dx[i] * dx[i] + dy[i] * dy[i]));
      const paso = Math.min(magnitud, temperatura);
      const x = posiciones[i].x + (dx[i] / magnitud) * paso;
      const y = posiciones[i].y + (dy[i] / magnitud) * paso;
      posiciones[i] = {
        x: Math.min(ancho - MARGEN, Math.max(MARGEN, x)),
        y: Math.min(alto - MARGEN, Math.max(MARGEN, y)),
      };
    }
  }
}
