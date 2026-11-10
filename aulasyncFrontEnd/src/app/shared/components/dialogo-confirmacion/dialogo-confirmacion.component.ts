import { Component, inject } from '@angular/core';

import { ConfirmacionService } from '../../../core/services/confirmacion.service';

@Component({
  selector: 'app-dialogo-confirmacion',
  standalone: false,
  templateUrl: './dialogo-confirmacion.component.html',
  styleUrl: './dialogo-confirmacion.component.css',
})
export class DialogoConfirmacionComponent {
  protected readonly confirmacionService = inject(ConfirmacionService);
}
