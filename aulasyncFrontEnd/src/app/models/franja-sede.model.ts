import { FranjaHorariaModel } from './franja-horaria.model';

export interface FranjaSedeModel extends FranjaHorariaModel {
  sedeId?: number | null;
}
