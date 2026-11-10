import { DiaSemanaModel } from './dia-semana.model';

export interface SesionHorarioModel {
  id: number;
  grupoId: number;
  numeroSesion: number;
  dia: DiaSemanaModel | null;
  horaInicio: number | null;
  aulaId: number | null;
  causa: string | null;
  detalleCausa: string | null;
  ubicada: boolean;
  diaNombre: string | null;
  horaFin: number | null;
  duracionHoras: number;
  cursoId: number;
  cursoCodigo: string;
  cursoNombre: string;
  semestre: number;
  numeroGrupo: number;
  docenteId: number;
  docenteNombre: string;
  aulaNombre: string | null;
  sedeId: number | null;
  sedeNombre: string | null;
}
