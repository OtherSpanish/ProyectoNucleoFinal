import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth.guard';

// Los roles de cada ruta replican los @PreAuthorize del back.
export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent) },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      {
        path: 'inicio', canActivate: [roleGuard], data: { roles: ['ADMIN', 'COMITE'] },
        loadComponent: () => import('./pages/inicio/inicio.component').then((m) => m.InicioComponent),
      },

      // Administrador
      {
        path: 'usuarios', canActivate: [roleGuard], data: { roles: ['ADMIN'] },
        loadComponent: () => import('./pages/usuarios/usuarios.component').then((m) => m.UsuariosComponent),
      },

      // Administrador y comité (el comité solo consulta docentes y catálogos)
      {
        path: 'docentes', canActivate: [roleGuard], data: { roles: ['ADMIN', 'COMITE'] },
        loadComponent: () => import('./pages/docentes/docentes.component').then((m) => m.DocentesComponent),
      },
      {
        path: 'catalogos', canActivate: [roleGuard], data: { roles: ['ADMIN', 'COMITE'] },
        loadComponent: () => import('./pages/catalogos/catalogos.component').then((m) => m.CatalogosComponent),
      },
      {
        path: 'cursos', canActivate: [roleGuard], data: { roles: ['ADMIN', 'COMITE'] },
        loadComponent: () => import('./pages/cursos/cursos.component').then((m) => m.CursosComponent),
      },
      {
        path: 'grupos', canActivate: [roleGuard], data: { roles: ['ADMIN', 'COMITE'] },
        loadComponent: () => import('./pages/grupos/grupos.component').then((m) => m.GruposComponent),
      },
      {
        path: 'calendario', canActivate: [roleGuard], data: { roles: ['ADMIN', 'COMITE'] },
        loadComponent: () => import('./pages/calendario/calendario.component').then((m) => m.CalendarioComponent),
      },
      {
        path: 'consulta', canActivate: [roleGuard], data: { roles: ['ADMIN', 'COMITE'] },
        loadComponent: () => import('./pages/consulta/consulta.component').then((m) => m.ConsultaComponent),
      },

      // Comité táctico
      {
        path: 'generar', canActivate: [roleGuard], data: { roles: ['COMITE'] },
        loadComponent: () => import('./pages/generar/generar.component').then((m) => m.GenerarComponent),
      },
      {
        path: 'editor', canActivate: [roleGuard], data: { roles: ['COMITE'] },
        loadComponent: () => import('./pages/editor/editor.component').then((m) => m.EditorComponent),
      },

      // Docente
      {
        path: 'mi-horario', canActivate: [roleGuard], data: { roles: ['DOCENTE'] },
        loadComponent: () => import('./pages/calendario/mi-horario.component').then((m) => m.MiHorarioComponent),
      },
      {
        path: 'mis-cursos', canActivate: [roleGuard], data: { roles: ['DOCENTE'] },
        loadComponent: () => import('./pages/mis-cursos/mis-cursos.component').then((m) => m.MisCursosComponent),
      },
      {
        path: 'mi-disponibilidad', canActivate: [roleGuard], data: { roles: ['DOCENTE'] },
        loadComponent: () => import('./pages/mi-disponibilidad/mi-disponibilidad.component').then((m) => m.MiDisponibilidadComponent),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
