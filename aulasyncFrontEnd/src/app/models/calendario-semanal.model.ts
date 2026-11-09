import { DiaCalendarioModel } from './dia-calendario.model';
import { SesionHorarioModel } from './sesion-horario.model';

export interface CalendarioSemanalModel {
  descripcion: string;
  prefijo: string;
  horaMinima: number;
  horaMaxima: number;
  dias: DiaCalendarioModel[];
  sinUbicar: SesionHorarioModel[];
  totalUbicadas: number;
  totalSinUbicar: number;
}
