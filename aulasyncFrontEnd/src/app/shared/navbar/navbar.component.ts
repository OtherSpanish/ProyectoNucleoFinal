import { Component, computed, inject } from '@angular/core';
import { NgFor } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { MENU } from '../../core/menu';
import { etiquetaRol } from '../../core/models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NgFor, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  usuario = this.auth.usuario;
  menu = computed(() => {
    const u = this.auth.usuario();
    return u ? MENU.filter((m) => m.roles.includes(u.rol)) : [];
  });

  rol(): string {
    const u = this.auth.usuario();
    return u ? etiquetaRol(u.rol) : '';
  }

  salir(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
