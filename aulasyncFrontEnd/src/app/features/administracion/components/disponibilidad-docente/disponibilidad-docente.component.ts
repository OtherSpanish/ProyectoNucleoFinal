import { Component, OnInit, inject, input, output, signal } from '@angular/core';

import { NotificacionService } from '../../../../core/services/notificacion.service';
import { DisponibilidadDocenteModel } from '../../../../models/disponibilidad-docente.model';
import { FranjaDocenteModel } from '../../../../models/franja-docente.model';
import { DisponibilidadService } from '../../../../services/disponibilidad.service';

/** Edita las franjas semanales en que un docente puede dictar clase. */
@Component({
  selector: 'app-disponibilidad-docente',
  standalone: false,
  templateUrl: './disponibilidad-docente.component.html',
  styleUrl: './disponibilidad-docente.component.css',
})
export class DisponibilidadDocenteComponent implements OnInit {
  private readonly disponibilidadService = inject(DisponibilidadService);
  private readonly notificacionService = inject(NotificacionService);

  readonly docenteId = input.required<number>();
  readonly guardado = output<void>();

  protected readonly disponibilidad = signal<DisponibilidadDocenteModel | null>(null);
  protected readonly franjas = signal<FranjaDocenteModel[]>([]);
  protected readonly guardando = signal(false);

  ngOnInit(): void {
    this.disponibilidadService.obtener(this.docenteId()).subscribe({
      next: (respuesta) => this.mostrar(respuesta),
    });
  }

  guardar(): void {
    this.guardando.set(true);
    this.disponibilidadService.reemplazar(this.docenteId(), this.franjas()).subscribe({
      next: (respuesta) => {
        this.mostrar(respuesta);
        this.guardando.set(false);
        this.notificacionService.exito('Disponibilidad actualizada.');
        this.guardado.emit();
      },
      error: () => this.guardando.set(false),
    });
  }

  private mostrar(respuesta: DisponibilidadDocenteModel): void {
    const copias: FranjaDocenteModel[] = [];
    for (const franja of respuesta.franjas) {
      copias.push({ ...franja });
    }
    this.disponibilidad.set(respuesta);
    this.franjas.set(copias);
  }
}
