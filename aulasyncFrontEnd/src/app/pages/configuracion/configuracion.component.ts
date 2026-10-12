import { Component, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/data.service';
import { ConfiguracionGeneral } from '../../core/models';

/** CU03 – Parámetros generales sin modificar código (RNF3, CA4, HU9). */
@Component({
  selector: 'app-configuracion',
  standalone: true,
  imports: [NgIf, FormsModule],
  templateUrl: './configuracion.component.html',
})
export class ConfiguracionComponent {
  private data = inject(DataService);

  form: ConfiguracionGeneral = { ...this.data.config() };
  mensaje = '';
  error = '';

  guardar(): void {
    const f = this.form;
    this.mensaje = '';
    if (f.horaInicioSemana >= f.horaFinSemana || f.horaInicioSabado >= f.horaFinSabado) {
      this.error = 'La hora de inicio debe ser menor que la de fin.';
      return;
    }
    if (f.almuerzoInicio >= f.almuerzoFin) {
      this.error = 'El almuerzo debe tener inicio menor que fin.';
      return;
    }
    if (f.sesionesMaxSemana < 1 || f.sesionesMaxSemana > 4 || f.limiteGrupo < 1 || f.duracionSesionHoras < 1) {
      this.error = 'Revisa los valores: sesiones por semana entre 1 y 4, y valores positivos.';
      return;
    }
    this.error = '';
    this.data.config.set({ ...f });
    this.mensaje = 'Configuración guardada.';
  }

  restaurar(): void {
    this.form = { ...this.data.config() };
    this.error = '';
    this.mensaje = '';
  }
}
