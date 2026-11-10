import { TipoAulaModel } from './tipo-aula.model';

export interface FiltroAulaModel {
  sedeId: number | null;
  tipo: TipoAulaModel | null;
  capacidadMinima: number | null;
  soloDisponibles: boolean | null;
}
