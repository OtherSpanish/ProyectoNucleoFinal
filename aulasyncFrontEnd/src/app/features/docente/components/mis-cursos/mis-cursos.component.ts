import { Component, OnInit, inject, signal } from '@angular/core';

import { GrupoModel } from '../../../../models/grupo.model';
import { InscritosGrupoModel } from '../../../../models/inscritos-grupo.model';
import { PortalDocenteService } from '../../../../services/portal-docente.service';

@Component({
  selector: 'app-mis-cursos',
  standalone: false,
  templateUrl: './mis-cursos.component.html',
  styleUrl: './mis-cursos.component.css',
})
export class MisCursosComponent implements OnInit {
  private readonly portalDocenteService = inject(PortalDocenteService);

  protected readonly grupos = signal<GrupoModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly grupoSeleccionado = signal<GrupoModel | null>(null);
  protected readonly inscritos = signal<InscritosGrupoModel | null>(null);

  ngOnInit(): void {
    this.portalDocenteService.misCursos().subscribe({
      next: (lista) => {
        this.grupos.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  verEstudiantes(grupo: GrupoModel): void {
    if (grupo.id === null) {
      return;
    }
    this.inscritos.set(null);
    this.grupoSeleccionado.set(grupo);
    this.portalDocenteService.estudiantesDeGrupo(grupo.id).subscribe({
      next: (datos) => this.inscritos.set(datos),
    });
  }
}
