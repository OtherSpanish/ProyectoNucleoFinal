import { TipoAulaModel } from './tipo-aula.model';

export interface AulaModel {
  id: number | null;
  nombre: string;
  capacidad: number | null;
  tipo: TipoAulaModel | null;
  disponible: boolean;
  sedeId: number | null;
  sedeNombre?: string;
}
