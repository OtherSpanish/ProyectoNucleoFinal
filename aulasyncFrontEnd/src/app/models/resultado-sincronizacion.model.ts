export interface ResultadoSincronizacionModel {
  hayHorario: boolean;
  sesionesCreadas: number;
  sesionesEliminadas: number;
  sesionesDesubicadas: number;
  cambios: string[];
}
