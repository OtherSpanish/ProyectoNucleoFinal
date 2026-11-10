import { Component, computed, inject, model } from '@angular/core';

import { FranjaHorariaModel } from '../../../models/franja-horaria.model';
import { CatalogoService } from '../../../services/catalogo.service';

/**
 * Tabla editable de franjas (día, hora inicio, hora fin). Los días y las horas
 * salen de GET /api/catalogos. No valida nada: el backend responde si una
 * franja es inválida o se traslapa.
 */
@Component({
  selector: 'app-editor-franjas',
  standalone: false,
  templateUrl: './editor-franjas.component.html',
  styleUrl: './editor-franjas.component.css',
})
export class EditorFranjasComponent {
  readonly franjas = model.required<FranjaHorariaModel[]>();

  private readonly catalogoService = inject(CatalogoService);

  protected readonly dias = computed(() => this.catalogoService.opciones('dias'));
  protected readonly horas = computed(() => this.catalogoService.catalogos().horasJornada);

  agregar(): void {
    const nueva: FranjaHorariaModel = { id: null, dia: null, horaInicio: null, horaFin: null };
    this.franjas.set([...this.franjas(), nueva]);
  }

  quitar(indice: number): void {
    const restantes: FranjaHorariaModel[] = [];
    const actuales = this.franjas();
    for (let i = 0; i < actuales.length; i++) {
      if (i !== indice) {
        restantes.push(actuales[i]);
      }
    }
    this.franjas.set(restantes);
  }
}
