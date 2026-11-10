import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { environment } from '../../environments/environment';
import { CatalogosModel } from '../models/catalogos.model';
import { OpcionCatalogoModel } from '../models/opcion-catalogo.model';

const VACIO: CatalogosModel = {
  roles: [],
  tiposAula: [],
  dedicaciones: [],
  intensidades: [],
  dias: [],
  horasJornada: [],
  motivos: [],
};

/**
 * Consume GET /api/catalogos y guarda la respuesta para toda la sesión, así
 * las listas y nombres (roles, días, dedicaciones…) vienen del backend.
 */
@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/catalogos`;
  private readonly datos = signal<CatalogosModel | null>(null);

  readonly listo = computed(() => this.datos() !== null);
  readonly catalogos = computed(() => this.datos() ?? VACIO);

  cargar(): Observable<CatalogosModel> {
    return this.http.get<CatalogosModel>(this.url).pipe(tap((respuesta) => this.datos.set(respuesta)));
  }

  /** Opciones de un catálogo por su nombre en la respuesta ('roles', 'dias', 'motivos'…). */
  opciones(nombre: keyof Omit<CatalogosModel, 'horasJornada'>): OpcionCatalogoModel[] {
    return this.catalogos()[nombre];
  }
}
