import { Component, effect, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgIf, RouterOutlet, NavbarComponent],
  template: `
    <div class="app" [class.con-menu]="auth.autenticado()">
      <app-navbar *ngIf="auth.autenticado()"></app-navbar>
      <main class="contenedor">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
})
export class AppComponent {
  auth = inject(AuthService);
  private refrescado = false;

  constructor() {
    // Al abrir la app con una sesión guardada, se confirma con el back quién es el usuario (rol vigente).
    effect(() => {
      if (this.auth.autenticado() && !this.refrescado) {
        this.refrescado = true;
        queueMicrotask(() => this.auth.refrescar());
      }
      if (!this.auth.autenticado()) this.refrescado = false;
    });
  }
}
