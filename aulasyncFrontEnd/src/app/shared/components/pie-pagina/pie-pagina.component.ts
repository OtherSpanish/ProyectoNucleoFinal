import { Component } from '@angular/core';

@Component({
  selector: 'app-pie-pagina',
  standalone: false,
  template: `<footer>
    <span>Desarrollado por <strong>Aulastral</strong></span>
    <span class="separador">·</span>
    <span>© 2026 Universidad El Bosque. Todos los derechos reservados.</span>
  </footer>`,
  styles: [
    `footer { display: flex; flex-wrap: wrap; justify-content: center; gap: 4px 8px; padding: 16px; font-size: 12.5px;
      color: var(--texto-suave); border-top: 1px solid var(--borde); }
     strong { color: var(--olivo-900); }
     .separador { opacity: 0.5; }`,
  ],
})
export class PiePaginaComponent {}
