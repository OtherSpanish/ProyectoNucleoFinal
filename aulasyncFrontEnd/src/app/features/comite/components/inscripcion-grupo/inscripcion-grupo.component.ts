import { Component, OnInit, inject, input, signal } from '@angular/core';

import { NotificacionService } from '../../../../core/services/notificacion.service';
import { EstudianteModel } from '../../../../models/estudiante.model';
import { InscritosGrupoModel } from '../../../../models/inscritos-grupo.model';
import { EstudianteService } from '../../../../services/estudiante.service';
import { InscripcionService } from '../../../../services/inscripcion.service';

/** Lista de inscritos de un grupo: buscar estudiantes, agregarlos y quitarlos. */
@Component({
  selector: 'app-inscripcion-grupo',
  standalone: false,
  templateUrl: './inscripcion-grupo.component.html',
  styleUrl: './inscripcion-grupo.component.css',
})
export class InscripcionGrupoComponent implements OnInit {
  private readonly inscripcionService = inject(InscripcionService);
  private readonly estudianteService = inject(EstudianteService);
  private readonly notificacionService = inject(NotificacionService);

  readonly grupoId = input.required<number>();

  protected readonly inscritos = signal<InscritosGrupoModel | null>(null);
  protected readonly resultados = signal<EstudianteModel[]>([]);
  protected prefijo = '';

  ngOnInit(): void {
    this.inscripcionService.obtener(this.grupoId()).subscribe({ next: (datos) => this.inscritos.set(datos) });
  }

  buscar(): void {
    this.estudianteService.buscarPorPrefijo(this.prefijo).subscribe({
      next: (lista) => this.resultados.set(lista),
    });
  }

  agregar(estudiante: EstudianteModel): void {
    if (estudiante.id === null) {
      return;
    }
    this.inscripcionService.agregar(this.grupoId(), estudiante.id).subscribe({
      next: (datos) => {
        this.inscritos.set(datos);
        this.notificacionService.exito(`${estudiante.nombre} inscrito.`);
      },
    });
  }

  quitar(estudiante: EstudianteModel): void {
    if (estudiante.id === null) {
      return;
    }
    this.inscripcionService.quitar(this.grupoId(), estudiante.id).subscribe({
      next: (datos) => {
        this.inscritos.set(datos);
        this.notificacionService.info(`${estudiante.nombre} retirado del grupo.`);
      },
    });
  }
}
