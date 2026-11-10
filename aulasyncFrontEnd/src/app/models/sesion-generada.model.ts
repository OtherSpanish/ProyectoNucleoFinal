import { CausaDescartadaModel } from './causa-descartada.model';
import { DiaSemanaModel } from './dia-semana.model';
import { MotivoRechazoModel } from './motivo-rechazo.model';

export interface SesionGeneradaModel {
  grupoId: number;
  numeroSesion: number;
  cursoId: number;
  cursoCodigo: string;
  cursoNombre: string;
  semestre: number;
  numeroGrupo: number;
  nucleoComun: boolean;
  docenteId: number;
  docenteNombre: string;
  estudiantes: number;
  duracionHoras: number;
  dia: DiaSemanaModel | null;
  diaNombre: string | null;
  horaInicio: number | null;
  horaFin: number | null;
  aulaId: number | null;
  aulaNombre: string | null;
  sedeId: number | null;
  sedeNombre: string | null;
  causa: MotivoRechazoModel | null;
  causaDescripcion: string | null;
  desglose: CausaDescartadaModel[];
}
