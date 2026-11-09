import { DiaSemanaModel } from './dia-semana.model';
import { SesionHorarioModel } from './sesion-horario.model';

export interface DiaCalendarioModel {
  dia: DiaSemanaModel;
  diaNombre: string;
  sesiones: SesionHorarioModel[];
}
