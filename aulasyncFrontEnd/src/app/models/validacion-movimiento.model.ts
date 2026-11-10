import { MotivoRechazoModel } from './motivo-rechazo.model';

export interface ValidacionMovimientoModel {
  sesionId: number;
  valido: boolean;
  motivos: MotivoRechazoModel[];
  mensajes: string[];
}
