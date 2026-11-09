import { DiaSemanaModel } from './dia-semana.model';

export interface FranjaHorariaModel {
  id?: number | null;
  dia: DiaSemanaModel;
  horaInicio: number;
  horaFin: number;
}
