import { RolModel } from './rol.model';

export interface UsuarioModel {
  id: number | null;
  nombre: string;
  correo: string;
  password?: string;
  rol: RolModel | null;
}
