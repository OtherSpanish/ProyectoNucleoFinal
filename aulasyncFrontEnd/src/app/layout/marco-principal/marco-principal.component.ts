import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { CLAVE_MENU_COLAPSADO } from '../../core/constants/almacenamiento.constants';
import { MENU_POR_ROL } from '../../core/constants/navegacion.constants';
import { SesionService } from '../../core/services/sesion.service';
import { ItemMenuModel } from '../../models/item-menu.model';
import { AuthService } from '../../services/auth.service';
import { CatalogoService } from '../../services/catalogo.service';

const CONSULTA_MOVIL = '(max-width: 900px)';

/**
 * Estructura de la app autenticada: barra lateral plegable, contenido y pie
 * de página siempre visible. Antes de mostrar las pantallas carga los
 * catálogos del backend (GET /api/catalogos).
 */
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
  protected readonly catalogoService = inject(CatalogoService);

  protected readonly colapsado = signal(this.leerColapsado());
  protected readonly menuMovilAbierto = signal(false);
  protected readonly errorCatalogos = signal(false);
  protected readonly menu = computed<ItemMenuModel[]>(() => MENU_POR_ROL[this.sesionService.rol() ?? ''] ?? []);

  ngOnInit(): void {
    this.authService.obtenerUsuarioActual().subscribe({
      next: (usuario) => this.sesionService.actualizarUsuario(usuario),
    });
    this.cargarCatalogos();
  }

  cargarCatalogos(): void {
    this.errorCatalogos.set(false);
    this.catalogoService.cargar().subscribe({
      error: () => this.errorCatalogos.set(true),
    });
  }

  /** En escritorio pliega la barra lateral; en celular la abre encima del contenido. */
  alternarMenu(): void {
    if (window.matchMedia(CONSULTA_MOVIL).matches) {
      this.menuMovilAbierto.set(!this.menuMovilAbierto());
      return;
    }
    this.colapsado.set(!this.colapsado());
    this.guardarColapsado(this.colapsado());
  }

  cerrarMenuMovil(): void {
    this.menuMovilAbierto.set(false);
  }

  cerrarSesion(): void {
    this.sesionService.cerrar();
    this.router.navigate(['/login']);
  }

  private leerColapsado(): boolean {
    try {
      return localStorage.getItem(CLAVE_MENU_COLAPSADO) === 'si';
    } catch {
      return false;
    }
  }

  private guardarColapsado(valor: boolean): void {
    try {
      localStorage.setItem(CLAVE_MENU_COLAPSADO, valor ? 'si' : 'no');
    } catch {
      // Sin almacenamiento: la preferencia dura solo esta visita.
    }
  }
}
