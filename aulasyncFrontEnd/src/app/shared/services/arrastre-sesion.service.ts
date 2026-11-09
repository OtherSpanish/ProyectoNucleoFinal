import { Injectable, signal } from '@angular/core';

import { SesionHorarioModel } from '../../models/sesion-horario.model';

/**
 * Recuerda qué sesión se está arrastrando, para que el calendario y la bandeja
 * de "sin ubicar" sepan qué soltaron sobre ellos.
 */
@Injectable({ providedIn: 'root' })
export class ArrastreSesionService {
  private sesionArrastrada: SesionHorarioModel | null = null;
  private readonly activo = signal(false);

  /** Solo para estilos (atenuar tarjetas mientras se arrastra). */
  readonly arrastrando = this.activo.asReadonly();

  iniciar(sesion: SesionHorarioModel): void {
    this.sesionArrastrada = sesion;
    // El estilo se aplica en el siguiente ciclo: cambiar el DOM dentro de dragstart cancela el arrastre en Chrome.
    setTimeout(() => this.activo.set(this.sesionArrastrada !== null));
  }

  sesion(): SesionHorarioModel | null {
    return this.sesionArrastrada;
  }

  /**
   * Devuelve la sesión soltada solo si coincide con el id que viaja en el
   * evento (evita usar una sesión vieja si se suelta texto, un archivo o un
   * arrastre que no empezó en AulaSync) y cierra el arrastre.
   */
  recibir(evento: DragEvent): SesionHorarioModel | null {
    const sesion = this.sesionArrastrada;
    const idEnEvento = evento.dataTransfer ? evento.dataTransfer.getData('text/plain') : '';
    this.terminar();
    if (sesion === null || idEnEvento !== String(sesion.id)) {
      return null;
    }
    return sesion;
  }

  terminar(): void {
    this.sesionArrastrada = null;
    this.activo.set(false);
  }
}
