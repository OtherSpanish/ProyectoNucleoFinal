import { AristaTrazaModel } from './arista-traza.model';
import { NodoTrazaModel } from './nodo-traza.model';
import { PasoTrazaModel } from './paso-traza.model';

/** Respuesta de GET /api/motor/traza: grafo, DSU y pasos de DSatur de una ejecución. */
export interface TrazaMotorModel {
  gruposProgramados: number;
  gruposNoAbiertos: string[];
  totalDocentes: number;
  totalSedes: number;
  totalAulas: number;
  totalAristas: number;
  aristasPorDocente: number;
  aristasPorGrupo: number;
  aristasPorBloque: number;
  totalComponentes: number;
  sesionesUbicadas: number;
  sesionesSinUbicar: number;
  nodos: NodoTrazaModel[];
  aristas: AristaTrazaModel[];
  pasos: PasoTrazaModel[];
  avisos: string[];
}
