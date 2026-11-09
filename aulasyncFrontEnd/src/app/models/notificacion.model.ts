export type TipoNotificacionModel = 'exito' | 'error' | 'info';

export interface NotificacionModel {
  id: number;
  tipo: TipoNotificacionModel;
  mensaje: string;
}
