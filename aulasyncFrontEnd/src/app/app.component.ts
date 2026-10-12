import { Component, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgIf, RouterOutlet, NavbarComponent],
  template: `
    <app-navbar *ngIf="auth.autenticado()"></app-navbar>
    <main class="contenedor">
      <router-outlet></router-outlet>
    </main>
  `,
})
export class AppComponent {
  auth = inject(AuthService);
}
