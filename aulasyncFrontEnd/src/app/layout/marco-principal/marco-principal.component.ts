import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { MENU_POR_ROL } from '../../core/constants/navegacion.constants';
import { SesionService } from '../../core/services/sesion.service';
import { ItemMenuModel } from '../../models/item-menu.model';
import { AuthService } from '../../services/auth.service';

/** Estructura de la app autenticada: barra lateral por rol y área de contenido. */
@Component({
  selector: 'app-marco-principal',
  standalone: false,
  templateUrl: './marco-principal.component.html',
  styleUrl: './marco-principal.component.css',
})
export class MarcoPrincipalComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly sesionService = inject(SesionService);

  protected readonly menuAbierto = signal(false);
  protected readonly menu = computed<ItemMenuModel[]>(() => MENU_POR_ROL[this.sesionService.rol() ?? ''] ?? []);

  ngOnInit(): void {
    this.authService.obtenerUsuarioActual().subscribe({
      next: (usuario) => this.sesionService.actualizarUsuario(usuario),
    });
  }

  alternarMenu(): void {
    this.menuAbierto.set(!this.menuAbierto());
  }

  cerrarSesion(): void {
    this.sesionService.cerrar();
    this.router.navigate(['/login']);
  }
}
