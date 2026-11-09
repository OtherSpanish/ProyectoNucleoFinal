import { DiaSemanaModel } from './dia-semana.model';
import { SesionHorarioModel } from './sesion-horario.model';

/** Lo que el usuario soltó y dónde lo soltó en el calendario. */
export interface MovimientoSesionModel {
  sesion: SesionHorarioModel;
  dia: DiaSemanaModel;
  diaNombre: string;
  horaInicio: number;
}
