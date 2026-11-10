import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ConsultaHorarioComponent } from './components/consulta-horario/consulta-horario.component';
import { CursosComponent } from './components/cursos/cursos.component';
import { GruposComponent } from './components/grupos/grupos.component';
import { HorarioComponent } from './components/horario/horario.component';
import { VistaPreviaComponent } from './components/vista-previa/vista-previa.component';

const routes: Routes = [
  { path: 'horario', component: HorarioComponent, title: 'Horario · AulaSync' },
  { path: 'consulta', component: ConsultaHorarioComponent, title: 'Consultar horario · AulaSync' },
  { path: 'vista-previa', component: VistaPreviaComponent, title: 'Vista previa · AulaSync' },
  { path: 'cursos', component: CursosComponent, title: 'Cursos · AulaSync' },
  { path: 'grupos', component: GruposComponent, title: 'Grupos · AulaSync' },
  { path: '', pathMatch: 'full', redirectTo: 'horario' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ComiteRoutingModule {}
