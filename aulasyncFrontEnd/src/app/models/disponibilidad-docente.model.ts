import { DedicacionModel } from './dedicacion.model';
import { FranjaDocenteModel } from './franja-docente.model';

export interface DisponibilidadDocenteModel {
  docenteId: number;
  nombreDocente: string;
  dedicacion: DedicacionModel;
  horasMaximas: number;
  horasDisponibles: number;
  franjas: FranjaDocenteModel[];
}
