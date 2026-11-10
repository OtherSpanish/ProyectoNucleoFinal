import { Component, EventEmitter, HostListener, Input, OnChanges, Output } from '@angular/core';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { DIAS, Dia, Franja, HORA_APERTURA, HORA_CIERRE, formatoHora, horaCierre } from '../../core/models';

/**
 * Cuadrícula semanal (lunes–sábado, 7:00–22:00) para ver o editar franjas horarias.
 * Sirve para la disponibilidad de un docente y para las franjas de una sede: ambas se guardan en el back
 * como una lista de {dia, horaInicio, horaFin} que no se puede traslapar. Aquí cada celda es una hora;
 * al emitir, las horas contiguas del mismo día se fusionan en una sola franja.
 * Con `editable`, se hace clic o se arrastra el mouse sobre las celdas para marcarlas / desmarcarlas.
 */
@Component({
  selector: 'app-editor-franjas',
  standalone: true,
  imports: [NgFor, NgIf, NgClass],
  template: `
    <div class="franjas" [class.solo-lectura]="!editable" (mouseleave)="pintando = false">
      <div class="fr-h"></div>
      <div class="fr-h" *ngFor="let d of dias">{{ d.corto }}</div>
      <ng-container *ngFor="let h of horas">
        <div class="fr-hora">{{ etiqueta(h) }}</div>
        <div
          *ngFor="let d of dias"
          class="fr-celda"
          [ngClass]="{ on: activa(d.valor, h), off: !habil(d.valor, h) }"
          [attr.aria-label]="d.etiqueta + ' ' + etiqueta(h)"
          (mousedown)="empezar($event, d.valor, h)"
          (mouseenter)="arrastrar(d.valor, h)"
        ></div>
      </ng-container>
    </div>
    <p class="ayuda" *ngIf="editable">
      Clic o arrastre para marcar/desmarcar. Total marcado: <strong>{{ total }} h</strong>.
    </p>
  `,
})
export class EditorFranjasComponent implements OnChanges {
  @Input() franjas: Franja[] = [];
  @Input() editable = true;
  @Output() cambio = new EventEmitter<Franja[]>();

  dias = DIAS;
  horas = Array.from({ length: HORA_CIERRE - HORA_APERTURA }, (_, i) => HORA_APERTURA + i);
  total = 0;
  pintando = false;

  private marcadas = new Set<string>();
  private modo = true;

  ngOnChanges(): void {
    this.marcadas = new Set();
    for (const f of this.franjas ?? []) {
      for (let h = f.horaInicio; h < f.horaFin; h++) this.marcadas.add(this.clave(f.dia, h));
    }
    this.total = this.marcadas.size;
  }

  etiqueta(h: number): string { return formatoHora(h); }
  habil(dia: Dia, hora: number): boolean { return hora < horaCierre(dia); }
  activa(dia: Dia, hora: number): boolean { return this.marcadas.has(this.clave(dia, hora)); }

  empezar(ev: MouseEvent, dia: Dia, hora: number): void {
    if (!this.editable || !this.habil(dia, hora)) return;
    ev.preventDefault();
    this.modo = !this.activa(dia, hora);
    this.pintando = true;
    this.aplicar(dia, hora);
  }

  arrastrar(dia: Dia, hora: number): void {
    if (this.pintando && this.habil(dia, hora)) this.aplicar(dia, hora);
  }

  @HostListener('window:mouseup')
  terminar(): void { this.pintando = false; }

  private aplicar(dia: Dia, hora: number): void {
    const k = this.clave(dia, hora);
    const antes = this.marcadas.has(k);
    if (this.modo === antes) return;
    if (this.modo) this.marcadas.add(k); else this.marcadas.delete(k);
    this.total = this.marcadas.size;
    this.cambio.emit(this.aFranjas());
  }

  private clave(dia: Dia, hora: number): string { return `${dia}|${hora}`; }

  /** Fusiona horas contiguas del mismo día en franjas. */
  private aFranjas(): Franja[] {
    const resultado: Franja[] = [];
    for (const d of DIAS) {
      let inicio: number | null = null;
      for (let h = HORA_APERTURA; h <= HORA_CIERRE; h++) {
        const on = h < HORA_CIERRE && this.marcadas.has(this.clave(d.valor, h));
        if (on && inicio === null) inicio = h;
        if (!on && inicio !== null) {
          resultado.push({ dia: d.valor, horaInicio: inicio, horaFin: h });
          inicio = null;
        }
      }
    }
    return resultado;
  }
}
