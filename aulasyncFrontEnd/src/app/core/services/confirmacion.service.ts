import { Injectable, signal } from '@angular/core';

import { ConfirmacionModel } from '../../models/confirmacion.model';

/** Diálogo de confirmación único para toda la aplicación. */
@Injectable({ providedIn: 'root' })
export class ConfirmacionService {
  private readonly actual = signal<ConfirmacionModel | null>(null);
  private resolver: ((respuesta: boolean) => void) | null = null;

  readonly confirmacion = this.actual.asReadonly();

  preguntar(titulo: string, mensaje: string, textoConfirmar = 'Confirmar', peligrosa = false): Promise<boolean> {
    this.actual.set({ titulo, mensaje, textoConfirmar, peligrosa });
    return new Promise<boolean>((resolve) => {
      this.resolver = resolve;
    });
  }

  responder(respuesta: boolean): void {
    if (this.resolver) {
      this.resolver(respuesta);
    }
    this.resolver = null;
    this.actual.set(null);
  }
}
