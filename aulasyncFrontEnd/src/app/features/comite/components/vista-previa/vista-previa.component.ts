import { Component, inject, signal } from '@angular/core';

import { ResultadoGeneracionModel } from '../../../../models/resultado-generacion.model';
import { MotorHorarioService } from '../../../../services/motor-horario.service';

/** Corre el motor sin guardar nada, para revisar el resultado antes de generar. */
@Component({
  selector: 'app-vista-previa',
  standalone: false,
  templateUrl: './vista-previa.component.html',
  styleUrl: './vista-previa.component.css',
})
export class VistaPreviaComponent {
  private readonly motorHorarioService = inject(MotorHorarioService);

  protected readonly resultado = signal<ResultadoGeneracionModel | null>(null);
  protected readonly ejecutando = signal(false);

  ejecutar(): void {
    this.ejecutando.set(true);
    this.motorHorarioService.generarVistaPrevia().subscribe({
      next: (datos) => {
        this.resultado.set(datos);
        this.ejecutando.set(false);
      },
      error: () => this.ejecutando.set(false),
    });
  }
}
