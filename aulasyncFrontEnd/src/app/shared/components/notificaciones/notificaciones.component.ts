import { Component, inject } from '@angular/core';

import { NotificacionService } from '../../../core/services/notificacion.service';

@Component({
  selector: 'app-notificaciones',
  standalone: false,
  templateUrl: './notificaciones.component.html',
  styleUrl: './notificaciones.component.css',
})
export class NotificacionesComponent {
  protected readonly notificacionService = inject(NotificacionService);
}
