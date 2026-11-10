import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { ComiteRoutingModule } from './comite-routing.module';
import { BandejaSinUbicarComponent } from './components/bandeja-sin-ubicar/bandeja-sin-ubicar.component';
import { ConsultaHorarioComponent } from './components/consulta-horario/consulta-horario.component';
import { CursosComponent } from './components/cursos/cursos.component';
import { GruposComponent } from './components/grupos/grupos.component';
import { HorarioComponent } from './components/horario/horario.component';
import { InscripcionGrupoComponent } from './components/inscripcion-grupo/inscripcion-grupo.component';
import { MovimientoSesionComponent } from './components/movimiento-sesion/movimiento-sesion.component';
import { ResumenGeneracionComponent } from './components/resumen-generacion/resumen-generacion.component';
import { VistaPreviaComponent } from './components/vista-previa/vista-previa.component';

/** Pantallas del rol COMITE: cursos, grupos y armado del horario. */
@NgModule({
  declarations: [
    BandejaSinUbicarComponent,
    ConsultaHorarioComponent,
    CursosComponent,
    GruposComponent,
    HorarioComponent,
    InscripcionGrupoComponent,
    MovimientoSesionComponent,
    ResumenGeneracionComponent,
    VistaPreviaComponent,
  ],
  imports: [SharedModule, ComiteRoutingModule],
})
export class ComiteModule {}
