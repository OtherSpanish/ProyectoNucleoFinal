import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { InicioComponent } from './pages/inicio/inicio.component';
import { DatosMaestrosComponent } from './pages/datos-maestros/datos-maestros.component';
import { DisponibilidadDocenteComponent } from './pages/disponibilidad/disponibilidad-docente.component';
import { GenerarPropuestaComponent } from './pages/generar-propuesta/generar-propuesta.component';
import { EditorDragDropComponent } from './pages/editor/editor-drag-drop.component';
import { CalendarioPageComponent } from './pages/calendario/calendario-page.component';
import { ConsultaHorarioComponent } from './pages/consulta/consulta-horario.component';
import { ConsultaHistorialComponent } from './pages/historial/consulta-historial.component';
import { ConfiguracionComponent } from './pages/configuracion/configuracion.component';
import { GestionUsuariosComponent } from './pages/usuarios/gestion-usuarios.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      { path: 'inicio', component: InicioComponent },

      // Comité Táctico
      { path: 'datos-maestros', component: DatosMaestrosComponent, canActivate: [roleGuard], data: { roles: ['COMITE_TACTICO'] } },
      { path: 'disponibilidad', component: DisponibilidadDocenteComponent, canActivate: [roleGuard], data: { roles: ['COMITE_TACTICO'] } },
      { path: 'generar', component: GenerarPropuestaComponent, canActivate: [roleGuard], data: { roles: ['COMITE_TACTICO'] } },
      { path: 'editor', component: EditorDragDropComponent, canActivate: [roleGuard], data: { roles: ['COMITE_TACTICO'] } },

      // Comité Táctico y Dirección
      { path: 'calendario', component: CalendarioPageComponent, canActivate: [roleGuard], data: { roles: ['COMITE_TACTICO', 'DIRECCION'] } },

      // Dirección
      { path: 'consulta', component: ConsultaHorarioComponent, canActivate: [roleGuard], data: { roles: ['DIRECCION'] } },

      // Dirección y Administrador
      { path: 'historial', component: ConsultaHistorialComponent, canActivate: [roleGuard], data: { roles: ['DIRECCION', 'ADMINISTRADOR'] } },

      // Administrador
      { path: 'configuracion', component: ConfiguracionComponent, canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] } },
      { path: 'usuarios', component: GestionUsuariosComponent, canActivate: [roleGuard], data: { roles: ['ADMINISTRADOR'] } },
    ],
  },
  { path: '**', redirectTo: '' },
];
