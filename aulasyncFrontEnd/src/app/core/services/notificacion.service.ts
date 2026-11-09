import { Injectable, signal } from '@angular/core';

import { NotificacionModel, TipoNotificacionModel } from '../../models/notificacion.model';

const DURACION_MS = 5000;

/** Mensajes emergentes (toasts) para éxito, error e información. */
@Injectable({ providedIn: 'root' })
export class NotificacionService {
  private siguienteId = 1;
  private readonly lista = signal<NotificacionModel[]>([]);

  readonly notificaciones = this.lista.asReadonly();

  exito(mensaje: string): void {
    this.mostrar('exito', mensaje);
  }

  error(mensaje: string): void {
    this.mostrar('error', mensaje);
  }

  info(mensaje: string): void {
    this.mostrar('info', mensaje);
  }

  cerrar(id: number): void {
    const restantes: NotificacionModel[] = [];
    for (const notificacion of this.lista()) {
      if (notificacion.id !== id) {
        restantes.push(notificacion);
      }
    }
    this.lista.set(restantes);
  }

  private mostrar(tipo: TipoNotificacionModel, mensaje: string): void {
    const id = this.siguienteId++;
    this.lista.set([...this.lista(), { id, tipo, mensaje }]);
    setTimeout(() => this.cerrar(id), DURACION_MS);
  }
}
