import { Component, HostListener, OnDestroy, computed, inject, signal } from '@angular/core';

import { ModoColorModel } from '../../../../models/modo-color.model';
import { PosicionNodoModel } from '../../../../models/posicion-nodo.model';
import { TrazaMotorModel } from '../../../../models/traza-motor.model';
import { TrazaMotorService } from '../../../../services/traza-motor.service';
import { CATALOGO_MODOS_COLOR, ETAPAS_MOTOR, LINEAS_POR_ETAPA } from '../../constants/motor-explicado.constants';
import { DisposicionGrafoService } from '../../services/disposicion-grafo.service';
import { ALTO_LIENZO, ANCHO_LIENZO } from '../grafo-conflictos/grafo-conflictos.component';

/** Índice de la etapa DSatur dentro de ETAPAS_MOTOR. */
const INDICE_DSATUR = 6;
/** Las etapas lentas (no DSatur) se muestran más tiempo al reproducir. */
const FACTOR_ETAPA = 2.5;

/**
 * Explica visualmente el motor de horarios con la traza real del backend, al
 * estilo de VisuAlgo: todo el algoritmo es una sola línea de tiempo
 * (6 etapas de construcción, cada paso de DSatur y el resultado) que se
 * recorre con controles fijos. En cada momento se ve el grafo, una frase que
 * dice qué cambió y la línea de pseudocódigo que se ejecuta.
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
    { valor: 1500, etiqueta: '0.5×' },
    { valor: 800, etiqueta: '1×' },
    { valor: 350, etiqueta: '2×' },
    { valor: 120, etiqueta: '4×' },
  ];

  protected readonly traza = signal<TrazaMotorModel | null>(null);
  protected readonly posiciones = signal<PosicionNodoModel[]>([]);
  protected readonly cargando = signal(false);
  /** Posición en la línea de tiempo: 0–5 construcción, 6 inicio de DSatur, 7…6+N pasos, 7+N resultado. */
  protected readonly posicion = signal(0);
  protected readonly seleccionado = signal<number | null>(null);
  protected readonly pestana = signal<'algoritmo' | 'nodo'>('algoritmo');
  protected readonly modoManual = signal<ModoColorModel | null>(null);
  protected readonly mostrarEtiquetas = signal(false);
  protected readonly reproduciendo = signal(false);
  protected readonly velocidad = signal(800);

  protected readonly totalPasos = computed(() => this.traza()?.pasos.length ?? 0);
  protected readonly ultimaPosicion = computed(() => INDICE_DSATUR + 1 + this.totalPasos());

  protected readonly indiceEtapa = computed(() => {
    const p = this.posicion();
    if (p <= INDICE_DSATUR) {
      return p;
    }
    return p < this.ultimaPosicion() ? INDICE_DSATUR : INDICE_DSATUR + 1;
  });

  /** Paso de DSatur visible (0 = nada procesado). */
  protected readonly pasoActual = computed(() => {
    const p = this.posicion();
    if (p <= INDICE_DSATUR) {
      return 0;
    }
    return Math.min(p - INDICE_DSATUR, this.totalPasos());
  });

  protected readonly etapa = computed(() => this.etapas[this.indiceEtapa()]);
  protected readonly modoColor = computed(() => this.modoManual() ?? this.etapa().colorPor);
  protected readonly enDsatur = computed(() => this.indiceEtapa() === INDICE_DSATUR);
  protected readonly pasoVisible = computed(() => this.traza()?.pasos[this.pasoActual() - 1] ?? null);

  protected readonly lineasActivas = computed(() => {
    const paso = this.pasoVisible();
    if (this.enDsatur() && paso !== null) {
      return [7, 8, 9, paso.ubicada ? 10 : 11];
    }
    return LINEAS_POR_ETAPA[this.etapa().clave];
  });
  protected readonly lineaPrincipal = computed(() => {
    const lineas = this.lineasActivas();
    return lineas[lineas.length - 1];
  });

  private temporizador: ReturnType<typeof setTimeout> | null = null;

  ejecutar(): void {
    this.detener();
    this.cargando.set(true);
    this.trazaMotorService.obtener().subscribe({
      next: (datos) => {
        this.posiciones.set(this.disposicionGrafoService.calcular(datos, ANCHO_LIENZO, ALTO_LIENZO));
        this.traza.set(datos);
        this.seleccionarNodo(null);
        this.irAPosicion(0);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  irAEtapa(indice: number): void {
    this.detener();
    this.irAPosicion(indice <= INDICE_DSATUR ? indice : this.ultimaPosicion());
  }

  irAPosicion(valor: number): void {
    const nueva = Math.max(0, Math.min(this.ultimaPosicion(), valor));
    if (this.indiceEtapaDe(nueva) !== this.indiceEtapa()) {
      this.modoManual.set(null);
    }
    this.posicion.set(nueva);
  }

  avanzar(): void {
    this.detener();
    this.irAPosicion(this.posicion() + 1);
  }

  retroceder(): void {
    this.detener();
    this.irAPosicion(this.posicion() - 1);
  }

  irAlInicio(): void {
    this.detener();
    this.irAPosicion(0);
  }

  irAlFinal(): void {
    this.detener();
    this.irAPosicion(this.ultimaPosicion());
  }

  /** Lleva la línea de tiempo al paso de DSatur en que se procesó un nodo. */
  verPasoDeNodo(numeroPaso: number): void {
    this.detener();
    this.irAPosicion(INDICE_DSATUR + numeroPaso);
  }

  seleccionarNodo(indice: number | null): void {
    this.seleccionado.set(indice);
    this.pestana.set(indice === null ? 'algoritmo' : 'nodo');
  }

  alternarReproduccion(): void {
    if (this.reproduciendo()) {
      this.detener();
      return;
    }
    if (this.posicion() >= this.ultimaPosicion()) {
      this.irAPosicion(0);
    }
    this.reproduciendo.set(true);
    this.programarSiguiente();
  }

  @HostListener('document:keydown', ['$event'])
  alPresionarTecla(evento: KeyboardEvent): void {
    const objetivo = evento.target as HTMLElement;
    const escribiendo = objetivo.tagName === 'INPUT' || objetivo.tagName === 'SELECT' || objetivo.tagName === 'TEXTAREA';
    if (this.traza() === null || escribiendo) {
      return;
    }
    if (evento.key === 'ArrowRight') {
      this.avanzar();
    } else if (evento.key === 'ArrowLeft') {
      this.retroceder();
    } else if (evento.key === ' ') {
      evento.preventDefault();
      this.alternarReproduccion();
    }
  }

  ngOnDestroy(): void {
    this.detener();
  }

  private programarSiguiente(): void {
    const lenta = this.posicion() <= INDICE_DSATUR || this.posicion() >= this.ultimaPosicion() - 1;
    const espera = lenta ? this.velocidad() * FACTOR_ETAPA : this.velocidad();
    this.temporizador = setTimeout(() => this.pasoAutomatico(), espera);
  }

  private pasoAutomatico(): void {
    if (this.posicion() >= this.ultimaPosicion()) {
      this.detener();
      return;
    }
    this.irAPosicion(this.posicion() + 1);
    this.programarSiguiente();
  }

  private detener(): void {
    if (this.temporizador !== null) {
      clearTimeout(this.temporizador);
      this.temporizador = null;
    }
    this.reproduciendo.set(false);
  }

  private indiceEtapaDe(p: number): number {
    if (p <= INDICE_DSATUR) {
      return p;
    }
    return p < this.ultimaPosicion() ? INDICE_DSATUR : INDICE_DSATUR + 1;
  }
}
