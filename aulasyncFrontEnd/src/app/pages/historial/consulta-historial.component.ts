import { Component, inject } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScheduleService } from '../../core/schedule.service';
import { RegistroAuditoria } from '../../core/models';

/** CU10 – Historial de cambios de una sesión, en orden cronológico (RF8, RF11, CA7). Solo lectura (RNF8). */
@Component({
  selector: 'app-consulta-historial',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, DatePipe],
  templateUrl: './consulta-historial.component.html',
})
export class ConsultaHistorialComponent {
  schedule = inject(ScheduleService);
  sesionId = '';

  get registros(): RegistroAuditoria[] {
    return this.schedule.historialDe(this.sesionId || null);
  }
}
