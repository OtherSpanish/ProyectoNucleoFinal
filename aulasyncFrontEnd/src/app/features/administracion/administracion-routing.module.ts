import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AulasComponent } from './components/aulas/aulas.component';
import { DocentesComponent } from './components/docentes/docentes.component';
import { EspecialidadesComponent } from './components/especialidades/especialidades.component';
import { EstudiantesComponent } from './components/estudiantes/estudiantes.component';
import { ExplicacionMotorComponent } from './components/explicacion-motor/explicacion-motor.component';
import { SedesComponent } from './components/sedes/sedes.component';
import { SemestresComponent } from './components/semestres/semestres.component';
import { UsuariosComponent } from './components/usuarios/usuarios.component';

const routes: Routes = [
  { path: 'usuarios', component: UsuariosComponent, title: 'Usuarios · AulaSync' },
  { path: 'docentes', component: DocentesComponent, title: 'Docentes · AulaSync' },
  { path: 'especialidades', component: EspecialidadesComponent, title: 'Especialidades · AulaSync' },
  { path: 'estudiantes', component: EstudiantesComponent, title: 'Estudiantes · AulaSync' },
  { path: 'sedes', component: SedesComponent, title: 'Sedes · AulaSync' },
  { path: 'aulas', component: AulasComponent, title: 'Aulas · AulaSync' },
  { path: 'motor', component: ExplicacionMotorComponent, title: 'Cómo funciona el motor · AulaSync' },
  { path: 'semestres', component: SemestresComponent, title: 'Semestres · AulaSync' },
  { path: '', pathMatch: 'full', redirectTo: 'usuarios' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdministracionRoutingModule {}
