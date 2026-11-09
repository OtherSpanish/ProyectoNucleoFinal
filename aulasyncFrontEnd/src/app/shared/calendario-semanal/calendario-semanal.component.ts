import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { DIAS, Dia, HORA_APERTURA, HORA_CIERRE, SesionHorario, formatoHora, horaCierre } from '../../core/models';
import { DragService } from '../drag.service';

export interface CeldaEstado {
  dia: Dia;
  hora: number;
  duracion: number;
  /** true = movimiento válido (verde), false = inválido (rojo), null = validando. */
  valido: boolean | null;
}

interface ItemSesion {
  s: SesionHorario;
  top: number;
  alto: number;
  izq: number;
  ancho: number;
}

export const ALTO_HORA = 44;

/**
 * Calendario semanal reutilizable (lunes–sábado).
 * - Solo lectura: muestra las sesiones ubicadas; las que coinciden en hora se reparten en carriles.
 * - Con [editable]: las sesiones se arrastran (HTML5 nativo) y las celdas avisan por (sobre) y (soltar);
 *   quien lo usa decide si el movimiento es válido y pinta la celda con [celdaEstado].
 */
@Component({
  selector: 'app-calendario-semanal',
  standalone: true,
  imports: [NgFor, NgIf, NgClass],
  templateUrl: './calendario-semanal.component.html',
})
export class CalendarioSemanalComponent implements OnChanges {
  drag = inject(DragService);

  @Input() sesiones: SesionHorario[] = [];
  @Input() editable = false;
  @Input() seleccionadaId: number | null = null;
  @Input() celdaEstado: CeldaEstado | null = null;
  @Input() horaMin = HORA_APERTURA;
  @Input() horaMax = HORA_CIERRE;

  @Output() soltar = new EventEmitter<{ sesionId: number; dia: Dia; hora: number }>();
  @Output() sobre = new EventEmitter<{ sesionId: number; dia: Dia; hora: number }>();
  @Output() seleccionar = new EventEmitter<number>();

  dias = DIAS;
  alto = ALTO_HORA;
  horas: number[] = [];
  columnas: { dia: Dia; items: ItemSesion[] }[] = [];
  private ultimaCelda = '';

  ngOnChanges(cambios: SimpleChanges): void {
    // Solo se recalcula la distribución cuando cambian las sesiones o el rango de horas; si no, cada cambio de
    // color de celda recrearía todo el DOM en pleno arrastre y el navegador perdería el destino del «drop».
    if (cambios['sesiones'] || cambios['horaMin'] || cambios['horaMax'] || !this.columnas.length) {
      this.horas = Array.from({ length: Math.max(this.horaMax - this.horaMin, 0) }, (_, i) => this.horaMin + i);
      this.columnas = DIAS.map((d) => ({ dia: d.valor, items: this.distribuir(d.valor) }));
    }
  }

  porDia(_: number, col: { dia: Dia }): Dia { return col.dia; }

  porId(_: number, it: { s: SesionHorario }): number { return it.s.id; }

  etiqueta(h: number): string { return formatoHora(h); }
  habil(dia: Dia, hora: number): boolean { return hora < horaCierre(dia); }

  claseCelda(dia: Dia, hora: number): string {
    if (!this.habil(dia, hora)) return 'off';
    const e = this.celdaEstado;
    if (e && e.dia === dia && hora >= e.hora && hora < e.hora + e.duracion) {
      return e.valido === null ? 'validando' : e.valido ? 'ok' : 'mal';
    }
    return '';
  }

  titulo(s: SesionHorario): string {
    return `${s.cursoCodigo} · ${s.cursoNombre}\nGrupo ${s.numeroGrupo} · Semestre ${s.semestre}\n` +
      `${formatoHora(s.horaInicio)}–${formatoHora(s.horaFin)}\n${s.docenteNombre}\n${s.aulaNombre ?? ''} ${s.sedeNombre ? '(' + s.sedeNombre + ')' : ''}`;
  }

  // ----- Drag & drop -----
  alIniciarArrastre(ev: DragEvent, s: SesionHorario): void {
    if (!this.editable) return;
    ev.dataTransfer?.setData('text/plain', String(s.id));
    if (ev.dataTransfer) ev.dataTransfer.effectAllowed = 'move';
    // Se difiere para que el navegador no cancele el arrastre al desactivar los eventos de las tarjetas.
    setTimeout(() => this.drag.sesionId.set(s.id));
    this.seleccionar.emit(s.id);
  }

  alTerminarArrastre(): void {
    this.drag.sesionId.set(null);
    this.ultimaCelda = '';
  }

  alArrastrarSobre(ev: DragEvent, dia: Dia, hora: number): void {
    const id = this.drag.sesionId();
    if (!this.editable || id === null || !this.habil(dia, hora)) return;
    ev.preventDefault();
    const k = `${id}|${dia}|${hora}`;
    if (k !== this.ultimaCelda) {
      this.ultimaCelda = k;
      this.sobre.emit({ sesionId: id, dia, hora });
    }
  }

  alSoltar(ev: DragEvent, dia: Dia, hora: number): void {
    ev.preventDefault();
    const txt = ev.dataTransfer?.getData('text/plain');
    const id = txt ? Number(txt) : this.drag.sesionId();
    this.drag.sesionId.set(null);
    this.ultimaCelda = '';
    if (id !== null && id !== undefined && !Number.isNaN(id) && this.editable && this.habil(dia, hora)) {
      this.soltar.emit({ sesionId: id, dia, hora });
    }
  }

  // ----- Distribución en carriles de las sesiones que se cruzan -----
  private distribuir(dia: Dia): ItemSesion[] {
    const delDia = this.sesiones
      .filter((s) => s.ubicada && s.dia === dia && s.horaInicio !== null)
      .map((s) => ({ s, ini: s.horaInicio as number, fin: (s.horaInicio as number) + s.duracionHoras }))
      .sort((a, b) => a.ini - b.ini || b.fin - a.fin || a.s.id - b.s.id);

    const resultado: ItemSesion[] = [];
    let grupo: { s: SesionHorario; ini: number; fin: number; carril: number }[] = [];
    let finGrupo = -1;

    const cerrar = () => {
      const carriles = grupo.reduce((m, x) => Math.max(m, x.carril + 1), 1);
      for (const x of grupo) {
        resultado.push({
          s: x.s,
          top: (x.ini - this.horaMin) * ALTO_HORA,
          alto: (x.fin - x.ini) * ALTO_HORA,
          izq: (x.carril / carriles) * 100,
          ancho: 100 / carriles,
        });
      }
      grupo = [];
    };

    for (const it of delDia) {
      if (grupo.length && it.ini >= finGrupo) cerrar();
      if (!grupo.length) finGrupo = -1;
      // primer carril libre en este grupo
      const ocupados = new Set(grupo.filter((g) => g.fin > it.ini).map((g) => g.carril));
      let carril = 0;
      while (ocupados.has(carril)) carril++;
      grupo.push({ ...it, carril });
      finGrupo = Math.max(finGrupo, it.fin);
    }
    if (grupo.length) cerrar();
    return resultado;
  }
}
