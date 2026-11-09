import { FranjaHorariaModel } from './franja-horaria.model';

export interface FranjaDocenteModel extends FranjaHorariaModel {
  docenteId?: number | null;
}
