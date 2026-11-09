import { FiltroAristasModel, ModoColorModel } from './modo-color.model';

/** Una etapa de la explicación del motor (texto y forma de dibujar el grafo). */
export interface EtapaMotorModel {
  clave: string;
  titulo: string;
  resumen: string;
  parrafos: string[];
  colorPor: ModoColorModel;
  aristas: FiltroAristasModel;
}
