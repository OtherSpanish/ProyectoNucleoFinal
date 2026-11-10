import { Component, computed, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { menuPara, rutaInicial } from '../../core/menu';
import { etiquetaRol } from '../../core/models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  private auth = inject(AuthService);

  usuario = this.auth.usuario;
  hayLogo = true;
  menu = computed(() => menuPara(this.auth.rol()));
  inicio = computed(() => rutaInicial(this.auth.rol()));
  rol = computed(() => etiquetaRol(this.auth.rol()));

  salir(): void {
    this.auth.logout();
  }
}
