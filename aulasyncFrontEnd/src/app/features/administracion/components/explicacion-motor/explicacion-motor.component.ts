import { Component, HostListener, OnDestroy, computed, inject, signal } from '@angular/core';

import { ModoColorModel } from '../../../../models/modo-color.model';
import { PosicionNodoModel } from '../../../../models/posicion-nodo.model';
import { TrazaMotorModel } from '../../../../models/traza-motor.model';
import { TrazaMotorService } from '../../../../services/traza-motor.service';
import { CATALOGO_MODOS_COLOR, ETAPAS_MOTOR } from '../../constants/motor-explicado.constants';
import { DisposicionGrafoService } from '../../services/disposicion-grafo.service';
import { ALTO_LIENZO, ANCHO_LIENZO } from '../grafo-conflictos/grafo-conflictos.component';

const INDICE_DSATUR = 6;
const INDICE_RESULTADO = 7;

/**
 * Explica visualmente el motor de horarios con la traza real del backend:
 * nodos, aristas por docente / grupo / bloque, DSU y DSatur paso a paso.
 */
@Component({
  selector: 'app-explicacion-motor',
  standalone: false,
  templateUrl: './explicacion-motor.component.html',
  styleUrl: './explicacion-motor.component.css',
})
export class ExplicacionMotorComponent implements OnDestroy {
  private readonly trazaMotorService = inject(TrazaMotorService);
  private readonly disposicionGrafoService = inject(DisposicionGrafoService);

  protected readonly etapas = ETAPAS_MOTOR;
  protected readonly modos = CATALOGO_MODOS_COLOR;
  protected readonly velocidades = [
    { valor: 1400, etiqueta: 'Lenta' },
    { valor: 700, etiqueta: 'Normal' },
    { valor: 250, etiqueta: 'Rápida' },
  ];

  protected readonly traza = signal<TrazaMotorModel | null>(null);
  protected readonly posiciones = signal<PosicionNodoModel[]>([]);
  protected readonly cargando = signal(false);
  protected readonly indiceEtapa = signal(0);
  protected readonly pasoActual = signal(0);
  protected readonly seleccionado = signal<number | null>(null);
  protected readonly modoManual = signal<ModoColorModel | null>(null);
  protected readonly mostrarEtiquetas = signal(false);
  protected readonly reproduciendo = signal(false);
  protected readonly velocidad = signal(700);

  protected readonly etapa = computed(() => this.etapas[this.indiceEtapa()]);
  protected readonly modoColor = computed(() => this.modoManual() ?? this.etapa().colorPor);
  protected readonly enDsatur = computed(() => this.indiceEtapa() === INDICE_DSATUR);
  /** En las etapas anteriores a DSatur nada está ubicado; en el resultado, todo. */
  protected readonly pasoVisible = computed(() => {
    if (this.indiceEtapa() < INDICE_DSATUR) {
      return 0;
    }
    return this.pasoActual();
  });

  private temporizador: ReturnType<typeof setInterval> | null = null;

  ejecutar(): void {
    this.detener();
    this.cargando.set(true);
    this.trazaMotorService.obtener().subscribe({
      next: (datos) => {
        this.posiciones.set(this.disposicionGrafoService.calcular(datos, ANCHO_LIENZO, ALTO_LIENZO));
        this.traza.set(datos);
        this.seleccionado.set(null);
        this.irAEtapa(0);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  irAEtapa(indice: number): void {
    this.detener();
    this.indiceEtapa.set(indice);
    this.modoManual.set(null);
    const total = this.traza()?.pasos.length ?? 0;
    if (indice === INDICE_RESULTADO) {
      this.pasoActual.set(total);
    } else if (indice === INDICE_DSATUR) {
      this.pasoActual.set(0);
    }
  }

  irAPaso(numero: number): void {
    const total = this.traza()?.pasos.length ?? 0;
    this.pasoActual.set(Math.max(0, Math.min(total, numero)));
  }

  verPasoDeNodo(numero: number): void {
    this.irAEtapa(INDICE_DSATUR);
    this.irAPaso(numero);
  }

  verNodoDelPaso(indice: number): void {
    this.seleccionado.set(indice);
  }

  alternarReproduccion(): void {
    if (this.reproduciendo()) {
      this.detener();
      return;
    }
    const total = this.traza()?.pasos.length ?? 0;
    if (this.pasoActual() >= total) {
      this.pasoActual.set(0);
    }
    this.reproduciendo.set(true);
    this.temporizador = setInterval(() => this.avanzarAutomatico(), this.velocidad());
  }

  cambiarVelocidad(valor: number): void {
    this.velocidad.set(valor);
    if (this.reproduciendo()) {
      this.detener();
      this.alternarReproduccion();
    }
  }

  @HostListener('document:keydown', ['$event'])
  alPresionarTecla(evento: KeyboardEvent): void {
    const objetivo = evento.target as HTMLElement;
    if (this.traza() === null || objetivo.tagName === 'INPUT' || objetivo.tagName === 'SELECT') {
      return;
    }
    if (evento.key === 'ArrowRight') {
      this.siguiente();
    } else if (evento.key === 'ArrowLeft') {
      this.anterior();
    }
  }

  siguiente(): void {
    if (this.enDsatur() && this.pasoActual() < (this.traza()?.pasos.length ?? 0)) {
      this.irAPaso(this.pasoActual() + 1);
    } else if (this.indiceEtapa() < this.etapas.length - 1) {
      this.irAEtapa(this.indiceEtapa() + 1);
    }
  }

  anterior(): void {
    if (this.enDsatur() && this.pasoActual() > 0) {
      this.irAPaso(this.pasoActual() - 1);
    } else if (this.indiceEtapa() > 0) {
      this.irAEtapa(this.indiceEtapa() - 1);
    }
  }

  ngOnDestroy(): void {
    this.detener();
  }

  private avanzarAutomatico(): void {
    const total = this.traza()?.pasos.length ?? 0;
    if (this.pasoActual() >= total) {
      this.detener();
      return;
    }
    this.pasoActual.set(this.pasoActual() + 1);
  }

  private detener(): void {
    if (this.temporizador !== null) {
      clearInterval(this.temporizador);
      this.temporizador = null;
    }
    this.reproduciendo.set(false);
  }
}
