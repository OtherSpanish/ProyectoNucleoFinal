import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { autenticacionGuard } from './core/guards/autenticacion.guard';
import { inicioGuard } from './core/guards/inicio.guard';
import { invitadoGuard } from './core/guards/invitado.guard';
import { rolGuard } from './core/guards/rol.guard';
import { MarcoPrincipalComponent } from './layout/marco-principal/marco-principal.component';

const routes: Routes = [
  {
    path: 'login',
    canActivate: [invitadoGuard],
    loadChildren: () => import('./features/auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: '',
    component: MarcoPrincipalComponent,
    canActivate: [autenticacionGuard],
    children: [
      {
        path: 'admin',
        canActivate: [rolGuard],
        data: { roles: ['ADMIN'] },
        loadChildren: () =>
          import('./features/administracion/administracion.module').then((m) => m.AdministracionModule),
      },
      {
        path: 'comite',
        canActivate: [rolGuard],
        data: { roles: ['COMITE'] },
        loadChildren: () => import('./features/comite/comite.module').then((m) => m.ComiteModule),
      },
      {
        path: 'docente',
        canActivate: [rolGuard],
        data: { roles: ['DOCENTE'] },
        loadChildren: () => import('./features/docente/docente.module').then((m) => m.DocenteModule),
      },
      { path: '', pathMatch: 'full', canActivate: [inicioGuard], children: [] },
    ],
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
