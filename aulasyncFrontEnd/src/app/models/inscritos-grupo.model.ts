import { EstudianteModel } from './estudiante.model';
import { GrupoModel } from './grupo.model';

export interface InscritosGrupoModel {
  grupo: GrupoModel;
  estudiantes: EstudianteModel[];
  minimoInscritos: number;
  faltantesParaAbrir: number;
  maximoInscritos: number;
  cuposDisponibles: number;
}
