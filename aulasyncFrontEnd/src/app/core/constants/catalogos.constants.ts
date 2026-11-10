import { OpcionModel } from '../../models/opcion.model';

/** Etiquetas legibles de los enums del backend (solo presentación). */
export const CATALOGO_ROLES: OpcionModel[] = [
  { valor: 'ADMIN', etiqueta: 'Administrador' },
  { valor: 'COMITE', etiqueta: 'Comité táctico' },
  { valor: 'DOCENTE', etiqueta: 'Docente' },
];

export const CATALOGO_TIPOS_AULA: OpcionModel[] = [
  { valor: 'CONVENCIONAL', etiqueta: 'Convencional' },
  { valor: 'SALA_COMPUTO', etiqueta: 'Sala de cómputo' },
  { valor: 'MOBILIARIO_MOVIL', etiqueta: 'Mobiliario móvil' },
];

export const CATALOGO_DEDICACIONES: OpcionModel[] = [
  { valor: 'TIEMPO_COMPLETO', etiqueta: 'Tiempo completo (40 h)' },
  { valor: 'TRES_CUARTOS', etiqueta: 'Tres cuartos (30 h)' },
  { valor: 'MEDIO_TIEMPO', etiqueta: 'Medio tiempo (20 h)' },
  { valor: 'UN_CUARTO', etiqueta: 'Un cuarto (10 h)' },
];

export const CATALOGO_INTENSIDADES: OpcionModel[] = [
  { valor: 'UNA_SESION_DOS_HORAS', etiqueta: '1 sesión × 2 h' },
  { valor: 'DOS_SESIONES_DOS_HORAS', etiqueta: '2 sesiones × 2 h' },
  { valor: 'TRES_SESIONES_DOS_HORAS', etiqueta: '3 sesiones × 2 h' },
  { valor: 'UNA_SESION_CUATRO_HORAS', etiqueta: '1 sesión × 4 h' },
];

export const CATALOGO_DIAS: OpcionModel[] = [
  { valor: 'MONDAY', etiqueta: 'Lunes' },
  { valor: 'TUESDAY', etiqueta: 'Martes' },
  { valor: 'WEDNESDAY', etiqueta: 'Miércoles' },
  { valor: 'THURSDAY', etiqueta: 'Jueves' },
  { valor: 'FRIDAY', etiqueta: 'Viernes' },
  { valor: 'SATURDAY', etiqueta: 'Sábado' },
];

/** Horas en las que se puede iniciar o terminar una franja (jornada 7:00 a 22:00). */
export const HORAS_JORNADA: number[] = [
  7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
];

export const CATALOGOS: Record<string, OpcionModel[]> = {
  rol: CATALOGO_ROLES,
  tipoAula: CATALOGO_TIPOS_AULA,
  dedicacion: CATALOGO_DEDICACIONES,
  intensidad: CATALOGO_INTENSIDADES,
  dia: CATALOGO_DIAS,
};
