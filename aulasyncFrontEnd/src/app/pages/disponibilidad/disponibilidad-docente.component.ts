import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/data.service';
import { DEDICACIONES, DIAS, Docente, NUM_DIAS, TipoDedicacion } from '../../core/models';

/** CU02 – Disponibilidad y dedicación del docente (RF6, CA3). La declara el Comité en nombre del docente. */
@Component({
  selector: 'app-disponibilidad-docente',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './disponibilidad-docente.component.html',
})
export class DisponibilidadDocenteComponent {
  data = inject(DataService);

  dias = DIAS;
  numDias = NUM_DIAS;
  dedicaciones = DEDICACIONES;

  docenteId = this.data.docentes()[0]?.id ?? '';
  nueva = { dia: 1, horaInicio: 7, horaFin: 13 };
  error = '';

  get docente(): Docente | undefined {
    return this.data.docentes().find((d) => d.id === this.docenteId);
  }

  cambiarDedicacion(valor: TipoDedicacion): void {
    if (this.docente) this.data.actualizarDocente(this.docente.id, { dedicacion: valor });
  }

  agregar(): void {
    const d = this.docente;
    if (!d) return;
    const f = this.nueva;
    if (f.horaInicio >= f.horaFin) {
      this.error = 'La hora de inicio debe ser menor que la hora de fin.';
      return;
    }
    this.error = '';
    this.data.actualizarDocente(d.id, { disponibilidad: [...d.disponibilidad, { ...f }] });
  }

  quitar(indice: number): void {
    const d = this.docente;
    if (!d) return;
    this.data.actualizarDocente(d.id, { disponibilidad: d.disponibilidad.filter((_, i) => i !== indice) });
  }
}
