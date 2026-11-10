import { DiaSemanaModel } from './dia-semana.model';

/** Cuerpo que se envía al validar o mover una sesión (dia, horaInicio, aulaId). */
export interface DestinoSesionModel {
  dia: DiaSemanaModel;
  horaInicio: number;
  aulaId: number | null;
}
