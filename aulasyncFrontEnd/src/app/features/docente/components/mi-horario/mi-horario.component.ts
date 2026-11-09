import { Component, OnInit, inject, signal } from '@angular/core';

import { CalendarioSemanalModel } from '../../../../models/calendario-semanal.model';
import { PortalDocenteService } from '../../../../services/portal-docente.service';

@Component({
  selector: 'app-mi-horario',
  standalone: false,
  templateUrl: './mi-horario.component.html',
  styleUrl: './mi-horario.component.css',
})
export class MiHorarioComponent implements OnInit {
  private readonly portalDocenteService = inject(PortalDocenteService);

  protected readonly calendario = signal<CalendarioSemanalModel | null>(null);
  protected readonly cargando = signal(true);

  ngOnInit(): void {
    this.portalDocenteService.miHorario().subscribe({
      next: (datos) => {
        this.calendario.set(datos);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }
}
