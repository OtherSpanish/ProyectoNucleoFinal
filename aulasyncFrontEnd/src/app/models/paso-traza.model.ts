import { CausaDescartadaModel } from './causa-descartada.model';
import { DiaSemanaModel } from './dia-semana.model';
import { MotivoRechazoModel } from './motivo-rechazo.model';

export interface PasoTrazaModel {
  numero: number;
  nodo: number;
  saturacion: number;
  grado: number;
  franjasCandidatas: number;
  franjasViables: number;
  bloqueos: CausaDescartadaModel[];
  ubicada: boolean;
  dia: DiaSemanaModel | null;
  diaNombre: string | null;
  horaInicio: number | null;
  aulaNombre: string | null;
  sedeNombre: string | null;
  cargaDia: number | null;
  causa: MotivoRechazoModel | null;
  causaDescripcion: string | null;
}
