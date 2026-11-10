/** Opción de un catálogo del backend (GET /api/catalogos). Los campos opcionales dependen del catálogo. */
export interface OpcionCatalogoModel {
  valor: string;
  etiqueta: string;
  descripcion?: string | null;
  horasMaximas?: number | null;
  sesionesSemana?: number | null;
  duracionHoras?: number | null;
  horaCierre?: number | null;
}
