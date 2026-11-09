import { FranjaSedeModel } from './franja-sede.model';

export interface SedeModel {
  id: number | null;
  nombre: string;
  exclusivaPrimerSemestre: boolean;
  diasPrimerSemestre: number | null;
  franjas: FranjaSedeModel[];
}
