import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { MiDisponibilidadComponent } from './components/mi-disponibilidad/mi-disponibilidad.component';
import { MiHorarioComponent } from './components/mi-horario/mi-horario.component';
import { MisCursosComponent } from './components/mis-cursos/mis-cursos.component';
import { DocenteRoutingModule } from './docente-routing.module';

/** Portal del rol DOCENTE (solo lectura). */
@NgModule({
  declarations: [MiDisponibilidadComponent, MiHorarioComponent, MisCursosComponent],
  imports: [SharedModule, DocenteRoutingModule],
})
export class DocenteModule {}
