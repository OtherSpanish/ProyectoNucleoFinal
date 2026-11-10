import { Injectable, signal } from '@angular/core';

/** Estado compartido del arrastre (calendario ↔ panel de sesiones sin ubicar). */
@Injectable({ providedIn: 'root' })
export class DragService {
  /** Id de la sesión que se está arrastrando, o null. */
  readonly sesionId = signal<number | null>(null);
}
