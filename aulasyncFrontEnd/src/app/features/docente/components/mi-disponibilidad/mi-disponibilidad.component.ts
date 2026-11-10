import { Component, OnInit, inject, signal } from '@angular/core';

import { DisponibilidadDocenteModel } from '../../../../models/disponibilidad-docente.model';
import { PortalDocenteService } from '../../../../services/portal-docente.service';

@Component({
  selector: 'app-mi-disponibilidad',
  standalone: false,
  templateUrl: './mi-disponibilidad.component.html',
  styleUrl: './mi-disponibilidad.component.css',
})
export class MiDisponibilidadComponent implements OnInit {
  private readonly portalDocenteService = inject(PortalDocenteService);

  protected readonly disponibilidad = signal<DisponibilidadDocenteModel | null>(null);
  protected readonly cargando = signal(true);

  ngOnInit(): void {
    this.portalDocenteService.miDisponibilidad().subscribe({
      next: (datos) => {
        this.disponibilidad.set(datos);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }
}
