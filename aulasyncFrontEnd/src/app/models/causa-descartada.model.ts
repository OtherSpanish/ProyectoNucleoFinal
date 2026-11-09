import { MotivoRechazoModel } from './motivo-rechazo.model';

export interface CausaDescartadaModel {
  motivo: MotivoRechazoModel;
  descripcion: string;
  franjas: number;
}
