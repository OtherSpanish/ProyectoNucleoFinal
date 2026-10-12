import { Component, computed, inject } from '@angular/core';
import { NgFor } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { MENU } from '../../core/menu';
import { etiquetaRol } from '../../core/models';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [NgFor, RouterLink],
  templateUrl: './inicio.component.html',
})
export class InicioComponent {
  private auth = inject(AuthService);
  usuario = this.auth.usuario;
  accesos = computed(() => {
    const u = this.auth.usuario();
    return u ? MENU.filter((m) => m.roles.includes(u.rol)) : [];
  });
  rol(): string {
    const u = this.auth.usuario();
    return u ? etiquetaRol(u.rol) : '';
  }
}
