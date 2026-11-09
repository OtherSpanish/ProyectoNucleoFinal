import { SesionGeneradaModel } from './sesion-generada.model';

export interface ResultadoGeneracionModel {
  gruposProgramados: number;
  gruposNoAbiertos: string[];
  totalSesiones: number;
  sesionesUbicadas: number;
  sesionesSinUbicar: number;
  ubicadas: SesionGeneradaModel[];
  sinUbicar: SesionGeneradaModel[];
  avisos: string[];
}
