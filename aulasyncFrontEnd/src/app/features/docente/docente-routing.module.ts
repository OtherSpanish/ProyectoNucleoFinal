import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MiDisponibilidadComponent } from './components/mi-disponibilidad/mi-disponibilidad.component';
import { MiHorarioComponent } from './components/mi-horario/mi-horario.component';
import { MisCursosComponent } from './components/mis-cursos/mis-cursos.component';

const routes: Routes = [
  { path: 'horario', component: MiHorarioComponent, title: 'Mi horario · AulaSync' },
  { path: 'cursos', component: MisCursosComponent, title: 'Mis cursos · AulaSync' },
  { path: 'disponibilidad', component: MiDisponibilidadComponent, title: 'Mi disponibilidad · AulaSync' },
  { path: '', pathMatch: 'full', redirectTo: 'horario' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DocenteRoutingModule {}
