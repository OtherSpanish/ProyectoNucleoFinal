import { Component, inject, input, output, signal } from '@angular/core';

import { CalendarioSemanalModel } from '../../../models/calendario-semanal.model';
import { DiaCalendarioModel } from '../../../models/dia-calendario.model';
import { MovimientoSesionModel } from '../../../models/movimiento-sesion.model';
import { SesionHorarioModel } from '../../../models/sesion-horario.model';
import { ArrastreSesionService } from '../../services/arrastre-sesion.service';

/**
 * Calendario semanal (columnas = días, filas = horas) armado con lo que envía
 * GET /api/horarios/calendario. Si es editable, las sesiones se arrastran y se
 * sueltan en una franja; el componente solo avisa dónde se soltó, la decisión
 * de si el movimiento es válido la toma el backend.
 */
@Component({
  selector: 'app-calendario-semanal',
  standalone: false,
  templateUrl: './calendario-semanal.component.html',
  styleUrl: './calendario-semanal.component.css',
})
export class CalendarioSemanalComponent {
  readonly calendario = input.required<CalendarioSemanalModel>();
  readonly editable = input<boolean>(false);
  readonly soltar = output<MovimientoSesionModel>();

  protected readonly arrastre = inject(ArrastreSesionService);
  protected readonly franjaResaltada = signal<string | null>(null);

  alIniciarArrastre(evento: DragEvent, sesion: SesionHorarioModel): void {
    if (!this.editable()) {
      return;
    }
    if (evento.dataTransfer) {
      evento.dataTransfer.setData('text/plain', String(sesion.id));
      evento.dataTransfer.effectAllowed = 'move';
    }
    this.arrastre.iniciar(sesion);
  }

  alTerminarArrastre(): void {
    this.franjaResaltada.set(null);
    this.arrastre.terminar();
  }

  alArrastrarSobre(evento: DragEvent, clave: string): void {
    if (!this.editable()) {
      return;
    }
    evento.preventDefault();
    if (evento.dataTransfer) {
      evento.dataTransfer.dropEffect = 'move';
    }
    if (this.franjaResaltada() !== clave) {
      this.franjaResaltada.set(clave);
    }
  }

  alSalirDeFranja(): void {
    this.franjaResaltada.set(null);
  }

  alSoltar(evento: DragEvent, dia: DiaCalendarioModel, hora: number): void {
    evento.preventDefault();
    this.franjaResaltada.set(null);
    const sesion = this.arrastre.recibir(evento);
    if (!this.editable() || sesion === null) {
      return;
    }
    this.soltar.emit({ sesion, dia: dia.dia, diaNombre: dia.diaNombre, horaInicio: hora });
  }
}
