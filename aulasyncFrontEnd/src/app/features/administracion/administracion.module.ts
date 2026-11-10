import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { AdministracionRoutingModule } from './administracion-routing.module';
import { AulasComponent } from './components/aulas/aulas.component';
import { BarrasBloqueoComponent } from './components/barras-bloqueo/barras-bloqueo.component';
import { DetalleNodoComponent } from './components/detalle-nodo/detalle-nodo.component';
import { DisponibilidadDocenteComponent } from './components/disponibilidad-docente/disponibilidad-docente.component';
import { DocentesComponent } from './components/docentes/docentes.component';
import { EspecialidadesComponent } from './components/especialidades/especialidades.component';
import { EstudiantesComponent } from './components/estudiantes/estudiantes.component';
import { ExplicacionMotorComponent } from './components/explicacion-motor/explicacion-motor.component';
import { GrafoConflictosComponent } from './components/grafo-conflictos/grafo-conflictos.component';
import { LeyendaGrafoComponent } from './components/leyenda-grafo/leyenda-grafo.component';
import { PasoDsaturComponent } from './components/paso-dsatur/paso-dsatur.component';
import { PseudocodigoMotorComponent } from './components/pseudocodigo-motor/pseudocodigo-motor.component';
import { SedesComponent } from './components/sedes/sedes.component';
import { SemestresComponent } from './components/semestres/semestres.component';
import { UsuariosComponent } from './components/usuarios/usuarios.component';
import { ColorNodoPipe } from './pipes/color-nodo.pipe';

/** Pantallas del rol ADMIN: datos maestros de la facultad y explicación del motor. */
@NgModule({
  declarations: [
    AulasComponent,
    BarrasBloqueoComponent,
    DetalleNodoComponent,
    DisponibilidadDocenteComponent,
    DocentesComponent,
    EspecialidadesComponent,
    EstudiantesComponent,
    ExplicacionMotorComponent,
    GrafoConflictosComponent,
    LeyendaGrafoComponent,
    PasoDsaturComponent,
    PseudocodigoMotorComponent,
    SedesComponent,
    SemestresComponent,
    UsuariosComponent,
    ColorNodoPipe,
  ],
  imports: [SharedModule, AdministracionRoutingModule],
})
export class AdministracionModule {}
