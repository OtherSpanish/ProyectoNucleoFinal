// Modelos del dominio (Sección 3.4.1 del documento de diseño).

export type RolTecnico = 'COMITE_TACTICO' | 'DIRECCION' | 'ADMINISTRADOR';
export const ROLES: { valor: RolTecnico; etiqueta: string }[] = [
  { valor: 'COMITE_TACTICO', etiqueta: 'Comité Táctico' },
  { valor: 'DIRECCION', etiqueta: 'Dirección del Programa' },
  { valor: 'ADMINISTRADOR', etiqueta: 'Administrador' },
];
export function etiquetaRol(r: RolTecnico): string {
  return ROLES.find((x) => x.valor === r)?.etiqueta ?? r;
}

export type TipoDedicacion = 'TIEMPO_COMPLETO' | 'TRES_CUARTOS' | 'MEDIO_TIEMPO' | 'UN_CUARTO';
export const DEDICACIONES: { valor: TipoDedicacion; etiqueta: string }[] = [
  { valor: 'TIEMPO_COMPLETO', etiqueta: 'Tiempo completo' },
  { valor: 'TRES_CUARTOS', etiqueta: 'Tres cuartos' },
  { valor: 'MEDIO_TIEMPO', etiqueta: 'Medio tiempo' },
  { valor: 'UN_CUARTO', etiqueta: 'Un cuarto' },
];
export function etiquetaDedicacion(d: TipoDedicacion): string {
  return DEDICACIONES.find((x) => x.valor === d)?.etiqueta ?? d;
}

export type TipoAula = 'CONVENCIONAL' | 'COMPUTO' | 'MOBILIARIO_MOVIL';
export const TIPOS_AULA: { valor: TipoAula; etiqueta: string }[] = [
  { valor: 'CONVENCIONAL', etiqueta: 'Salón convencional' },
  { valor: 'COMPUTO', etiqueta: 'Sala de cómputo' },
  { valor: 'MOBILIARIO_MOVIL', etiqueta: 'Mobiliario móvil' },
];
export function etiquetaTipoAula(t: TipoAula): string {
  return TIPOS_AULA.find((x) => x.valor === t)?.etiqueta ?? t;
}

// Días: 1 = lunes ... 6 = sábado
export const DIAS = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
export const NUM_DIAS = [1, 2, 3, 4, 5, 6];

export interface Usuario {
  id: string;
  nombre: string;
  usuario: string;
  clave: string; // solo para la maqueta; en producción la autenticación la hace el backend
  rol: RolTecnico;
  activo: boolean;
}

export interface FranjaDisponible {
  dia: number;
  horaInicio: number;
  horaFin: number;
}

export interface Docente {
  id: string;
  nombre: string;
  dedicacion: TipoDedicacion;
  disponibilidad: FranjaDisponible[];
}

export interface Aula {
  id: string;
  nombre: string;
  capacidad: number;
  tipo: TipoAula;
}

export interface GrupoEstudiantes {
  id: string;
  nombre: string;
  semestre: number;
  estudiantes: number;
}

export interface Curso {
  id: string;
  nombre: string;
  sesionesSemanales: number; // 1 a 4
  duracionHoras: number; // 2 por defecto, 4 en cursos específicos
  tipoAula: TipoAula;
}

export interface Seccion {
  id: string;
  cursoId: string;
  numero: number;
  docenteId: string;
  grupoId: string;
}

export interface ConfiguracionGeneral {
  horaInicioSemana: number;
  horaFinSemana: number;
  horaInicioSabado: number;
  horaFinSabado: number;
  almuerzoInicio: number;
  almuerzoFin: number;
  duracionSesionHoras: number;
  sesionesMaxSemana: number;
  limiteGrupo: number;
  toleranciaPorcentaje: number;
}

export type EstadoSesion = 'SIN_UBICAR' | 'UBICADA';

export interface Sesion {
  id: string;
  seccionId: string;
  cursoId: string;
  docenteId: string;
  grupoId: string;
  numero: number;
  duracion: number;
  estado: EstadoSesion;
  dia?: number;
  hora?: number;
  aulaId?: string;
  causa?: string; // causa del bloqueo si está sin ubicar
}

export interface RegistroAuditoria {
  id: string;
  sesionId: string;
  sesionEtiqueta: string;
  usuario: string;
  fecha: string; // ISO
  cambio: string;
}

export type ReglaId = 'RD1' | 'RD2' | 'RD3' | 'RD4' | 'RD6' | 'RD7' | 'RD9' | 'GENERAL';
export const REGLAS_TEXTO: Record<ReglaId, string> = {
  RD1: 'el docente ya tiene una sesión en esa franja',
  RD2: 'el grupo ya tiene una sesión en esa franja',
  RD3: 'el aula ya está ocupada en esa franja',
  RD4: 'fuera de la disponibilidad declarada del docente',
  RD6: 'el curso ya tiene una sesión ese día',
  RD7: 'fuera de la franja general o en la hora de almuerzo',
  RD9: 'el aula no tiene la capacidad o el tipo requerido',
  GENERAL: 'error general',
};

export interface Conflicto {
  regla: ReglaId;
  mensaje: string;
}

export interface ResultadoValidacion {
  valido: boolean;
  conflictos: Conflicto[];
  advertencias: string[]; // preferencias no bloqueantes (RB1)
}
