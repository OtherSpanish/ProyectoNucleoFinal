import { IntensidadHorariaModel } from './intensidad-horaria.model';
import { TipoAulaModel } from './tipo-aula.model';

export interface CursoModel {
  id: number | null;
  codigo: string;
  nombre: string;
  semestreId: number | null;
  semestreNumero?: number;
  especialidadId: number | null;
  especialidadNombre?: string;
  tipoAula: TipoAulaModel | null;
  intensidad: IntensidadHorariaModel | null;
  nucleoComun: boolean;
  sesionesSemana?: number;
  duracionHoras?: number;
  horasSemanales?: number;
}
