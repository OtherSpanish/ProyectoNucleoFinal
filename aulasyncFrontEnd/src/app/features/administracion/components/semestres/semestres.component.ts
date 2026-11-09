import { Component, OnInit, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { SemestreModel } from '../../../../models/semestre.model';
import { SemestreService } from '../../../../services/semestre.service';

@Component({
  selector: 'app-semestres',
  standalone: false,
  templateUrl: './semestres.component.html',
  styleUrl: './semestres.component.css',
})
export class SemestresComponent implements OnInit {
  private readonly semestreService = inject(SemestreService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly semestres = signal<SemestreModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly guardando = signal(false);
  protected readonly panelAbierto = signal(false);
  protected formulario: SemestreModel = this.crearVacio();

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.semestreService.listar().subscribe({
      next: (lista) => {
        this.semestres.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  nuevo(): void {
    this.formulario = this.crearVacio();
    this.panelAbierto.set(true);
  }

  editar(semestre: SemestreModel): void {
    this.formulario = { ...semestre };
    this.panelAbierto.set(true);
  }

  guardar(): void {
    this.guardando.set(true);
    const id = this.formulario.id;
    const peticion =
      id === null ? this.semestreService.crear(this.formulario) : this.semestreService.actualizar(id, this.formulario);
    peticion.subscribe({
      next: () => {
        this.notificacionService.exito('Semestre guardado.');
        this.guardando.set(false);
        this.panelAbierto.set(false);
        this.cargar();
      },
      error: () => this.guardando.set(false),
    });
  }

  async eliminar(semestre: SemestreModel): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Eliminar semestre',
      `¿Eliminar el semestre ${semestre.numero}?`,
      'Eliminar',
      true,
    );
    if (!confirmado || semestre.id === null) {
      return;
    }
    this.semestreService.eliminar(semestre.id).subscribe({
      next: () => {
        this.notificacionService.exito('Semestre eliminado.');
        this.cargar();
      },
    });
  }

  private crearVacio(): SemestreModel {
    return { id: null, numero: null, nombre: '' };
  }
}
