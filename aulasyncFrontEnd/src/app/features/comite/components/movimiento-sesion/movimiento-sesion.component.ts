import { Component, OnInit, inject, input, output, signal } from '@angular/core';

import { NotificacionService } from '../../../../core/services/notificacion.service';
import { AulaModel } from '../../../../models/aula.model';
import { DestinoSesionModel } from '../../../../models/destino-sesion.model';
import { MovimientoSesionModel } from '../../../../models/movimiento-sesion.model';
import { ValidacionMovimientoModel } from '../../../../models/validacion-movimiento.model';
import { EdicionHorarioService } from '../../../../services/edicion-horario.service';

/**
 * Confirma un movimiento hecho con drag & drop: el usuario elige el aula, el
 * backend dice si el movimiento es válido (POST /validar) y, si lo es, se
 * guarda (PATCH).
 */
@Component({
  selector: 'app-movimiento-sesion',
  standalone: false,
  templateUrl: './movimiento-sesion.component.html',
  styleUrl: './movimiento-sesion.component.css',
})
export class MovimientoSesionComponent implements OnInit {
  private readonly edicionHorarioService = inject(EdicionHorarioService);
  private readonly notificacionService = inject(NotificacionService);

  readonly movimiento = input.required<MovimientoSesionModel>();
  readonly aulas = input.required<AulaModel[]>();
  readonly confirmado = output<void>();
  readonly cancelado = output<void>();

  protected readonly validacion = signal<ValidacionMovimientoModel | null>(null);
  protected readonly validando = signal(false);
  protected readonly guardando = signal(false);
  protected aulaId: number | null = null;

  ngOnInit(): void {
    this.aulaId = this.movimiento().sesion.aulaId;
    this.validar();
  }

  validar(): void {
    this.validacion.set(null);
    if (this.aulaId === null) {
      return;
    }
    this.validando.set(true);
    this.edicionHorarioService.validar(this.movimiento().sesion.id, this.destino()).subscribe({
      next: (resultado) => {
        this.validacion.set(resultado);
        this.validando.set(false);
      },
      error: () => this.validando.set(false),
    });
  }

  confirmar(): void {
    this.guardando.set(true);
    const sesion = this.movimiento().sesion;
    this.edicionHorarioService.mover(sesion.id, this.destino()).subscribe({
      next: () => {
        this.guardando.set(false);
        this.notificacionService.exito(`${sesion.cursoCodigo} G${sesion.numeroGrupo} movida.`);
        this.confirmado.emit();
      },
      error: () => this.guardando.set(false),
    });
  }

  private destino(): DestinoSesionModel {
    return {
      dia: this.movimiento().dia,
      horaInicio: this.movimiento().horaInicio,
      aulaId: this.aulaId,
    };
  }
}
