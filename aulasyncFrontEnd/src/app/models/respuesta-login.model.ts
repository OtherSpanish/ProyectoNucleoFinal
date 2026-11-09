import { UsuarioModel } from './usuario.model';

export interface RespuestaLoginModel {
  token: string;
  tipo: string;
  expiraEnSegundos: number;
  usuario: UsuarioModel;
}
