import { Component, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { ScheduleService } from '../../core/schedule.service';
import { CalendarioSemanalComponent } from '../../shared/calendario-semanal/calendario-semanal.component';

/** CU05 – Visualizar el horario en un calendario semanal (RF3). Solo lectura. */
@Component({
  selector: 'app-calendario-page',
  standalone: true,
  imports: [NgIf, CalendarioSemanalComponent],
  templateUrl: './calendario-page.component.html',
})
export class CalendarioPageComponent {
  schedule = inject(ScheduleService);
}
