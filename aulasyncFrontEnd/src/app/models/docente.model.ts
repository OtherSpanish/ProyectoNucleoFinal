import { DedicacionModel } from './dedicacion.model';
import { EspecialidadModel } from './especialidad.model';
import { UsuarioModel } from './usuario.model';

export interface DocenteModel {
  id: number | null;
  usuario: UsuarioModel;
  dedicacion: DedicacionModel | null;
  especialidades: EspecialidadModel[];
}
