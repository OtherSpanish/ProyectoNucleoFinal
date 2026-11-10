import { OpcionCatalogoModel } from './opcion-catalogo.model';

/** Respuesta de GET /api/catalogos: datos del dominio que el front solo muestra. */
export interface CatalogosModel {
  roles: OpcionCatalogoModel[];
  tiposAula: OpcionCatalogoModel[];
  dedicaciones: OpcionCatalogoModel[];
  intensidades: OpcionCatalogoModel[];
  dias: OpcionCatalogoModel[];
  horasJornada: number[];
  motivos: OpcionCatalogoModel[];
}
