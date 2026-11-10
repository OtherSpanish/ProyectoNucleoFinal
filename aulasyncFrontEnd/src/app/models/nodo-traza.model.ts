import { DiaSemanaModel } from './dia-semana.model';
import { MotivoRechazoModel } from './motivo-rechazo.model';
import { TipoAulaModel } from './tipo-aula.model';

export interface NodoTrazaModel {
  indice: number;
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
  tipoAula: TipoAulaModel;
  vecinosDocente: number[];
  vecinosGrupo: number[];
  vecinosBloque: number[];
  grado: number;
  raizDsu: number;
  miembrosComponente: number[];
  orden: number;
  ubicada: boolean;
  dia: DiaSemanaModel | null;
  diaNombre: string | null;
  horaInicio: number | null;
  horaFin: number | null;
  aulaNombre: string | null;
  sedeNombre: string | null;
  causa: MotivoRechazoModel | null;
  causaDescripcion: string | null;
}
