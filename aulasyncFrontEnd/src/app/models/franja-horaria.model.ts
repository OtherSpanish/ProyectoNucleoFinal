import { DiaSemanaModel } from './dia-semana.model';

export interface FranjaHorariaModel {
  id?: number | null;
  dia: DiaSemanaModel | null;
  horaInicio: number | null;
  horaFin: number | null;
}
