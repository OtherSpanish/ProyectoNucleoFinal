// Modelos del front. Reflejan 1:1 los DTO y modelos de respuesta del back (paquetes dto/ y model/).

// ---------------------------------------------------------------- Roles
export type Rol = 'ADMIN' | 'COMITE' | 'DOCENTE';
export const ROLES: { valor: Rol; etiqueta: string }[] = [
  { valor: 'ADMIN', etiqueta: 'Administrador' },
  { valor: 'COMITE', etiqueta: 'Comité táctico' },
  { valor: 'DOCENTE', etiqueta: 'Docente' },
];
export function etiquetaRol(r: Rol | undefined | null): string {
  return ROLES.find((x) => x.valor === r)?.etiqueta ?? (r ?? '');
}

// ---------------------------------------------------------------- Días y horas
export type Dia = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY';
export const DIAS: { valor: Dia; etiqueta: string; corto: string }[] = [
  { valor: 'MONDAY', etiqueta: 'Lunes', corto: 'Lun' },
  { valor: 'TUESDAY', etiqueta: 'Martes', corto: 'Mar' },
  { valor: 'WEDNESDAY', etiqueta: 'Miércoles', corto: 'Mié' },
  { valor: 'THURSDAY', etiqueta: 'Jueves', corto: 'Jue' },
  { valor: 'FRIDAY', etiqueta: 'Viernes', corto: 'Vie' },
  { valor: 'SATURDAY', etiqueta: 'Sábado', corto: 'Sáb' },
];
export function etiquetaDia(d: Dia | string | null | undefined): string {
  return DIAS.find((x) => x.valor === d)?.etiqueta ?? (d ?? '');
}

/** Jornada institucional (JornadaInstitucional.java): abre a las 7, cierra a las 22 (sábado a las 13). */
export const HORA_APERTURA = 7;
export const HORA_CIERRE = 22;
export const HORA_CIERRE_SABADO = 13;
export function horaCierre(d: Dia): number {
  return d === 'SATURDAY' ? HORA_CIERRE_SABADO : HORA_CIERRE;
}
export function formatoHora(h: number | null | undefined): string {
  return h === null || h === undefined ? '' : `${String(h).padStart(2, '0')}:00`;
}

// ---------------------------------------------------------------- Enums de negocio
export type TipoAula = 'CONVENCIONAL' | 'SALA_COMPUTO' | 'MOBILIARIO_MOVIL';
export const TIPOS_AULA: { valor: TipoAula; etiqueta: string }[] = [
  { valor: 'CONVENCIONAL', etiqueta: 'Salón convencional' },
  { valor: 'SALA_COMPUTO', etiqueta: 'Sala de cómputo' },
  { valor: 'MOBILIARIO_MOVIL', etiqueta: 'Mobiliario móvil' },
];
export function etiquetaTipoAula(t: TipoAula | string | null | undefined): string {
  return TIPOS_AULA.find((x) => x.valor === t)?.etiqueta ?? (t ?? '');
}

export type Dedicacion = 'TIEMPO_COMPLETO' | 'TRES_CUARTOS' | 'MEDIO_TIEMPO' | 'UN_CUARTO';
export const DEDICACIONES: { valor: Dedicacion; etiqueta: string; horas: number }[] = [
  { valor: 'TIEMPO_COMPLETO', etiqueta: 'Tiempo completo (40 h)', horas: 40 },
  { valor: 'TRES_CUARTOS', etiqueta: 'Tres cuartos (30 h)', horas: 30 },
  { valor: 'MEDIO_TIEMPO', etiqueta: 'Medio tiempo (20 h)', horas: 20 },
  { valor: 'UN_CUARTO', etiqueta: 'Un cuarto (10 h)', horas: 10 },
];
export function etiquetaDedicacion(d: Dedicacion | string | null | undefined): string {
  return DEDICACIONES.find((x) => x.valor === d)?.etiqueta ?? (d ?? '');
}

export type Intensidad =
  | 'UNA_SESION_DOS_HORAS'
  | 'DOS_SESIONES_DOS_HORAS'
  | 'TRES_SESIONES_DOS_HORAS'
  | 'UNA_SESION_CUATRO_HORAS';
export const INTENSIDADES: { valor: Intensidad; etiqueta: string }[] = [
  { valor: 'UNA_SESION_DOS_HORAS', etiqueta: '1 sesión de 2 h (2 h/sem)' },
  { valor: 'DOS_SESIONES_DOS_HORAS', etiqueta: '2 sesiones de 2 h (4 h/sem)' },
  { valor: 'TRES_SESIONES_DOS_HORAS', etiqueta: '3 sesiones de 2 h (6 h/sem)' },
  { valor: 'UNA_SESION_CUATRO_HORAS', etiqueta: '1 sesión de 4 h (4 h/sem)' },
];
export function etiquetaIntensidad(i: Intensidad | string | null | undefined): string {
  return INTENSIDADES.find((x) => x.valor === i)?.etiqueta ?? (i ?? '');
}

/** Motivos de rechazo del motor (MotivoRechazo.java) + causa manual de EdicionHorarioService. */
export const MOTIVOS: Record<string, string> = {
  DOCENTE_NO_DISPONIBLE: 'El docente no tiene disponibilidad en esa franja.',
  CARGA_DOCENTE: 'El docente alcanzó su tope de horas semanales.',
  SEDE_NO_PERMITIDA: 'La sede no corresponde a ese día para este grupo.',
  CHOQUE_DOCENTE: 'El docente ya tiene otra clase a esa hora.',
  CHOQUE_GRUPO: 'El grupo ya tiene otra sesión a esa hora.',
  CHOQUE_BLOQUE: 'Se cruza con otra materia del mismo bloque (mismo número de grupo, semestre igual o consecutivo).',
  MISMO_DIA_GRUPO: 'El grupo ya tiene una sesión ese día.',
  SIN_AULA: 'No hay un aula libre del tipo y la capacidad requeridos en esa sede.',
  FUERA_DE_FRANJA_SEDE: 'La sede no tiene clases a esa hora (almuerzo o fuera de la jornada).',
  AULA_NO_DISPONIBLE: 'El aula está marcada como no disponible.',
  AULA_INCOMPATIBLE: 'El aula no es del tipo que exige el curso o no tiene capacidad para los inscritos.',
  AULA_OCUPADA: 'El aula ya tiene otra clase a esa hora.',
  DESUBICADA_MANUALMENTE: 'Retirada del horario manualmente por el comité.',
};
export function describirMotivo(m: string | null | undefined): string {
  return m ? (MOTIVOS[m] ?? m) : '';
}

// ---------------------------------------------------------------- Entidades
export interface Usuario {
  id?: number;
  nombre: string;
  correo: string;
  /** Solo se envía (login / crear / cambiar contraseña); el back nunca la devuelve. */
  password?: string;
  rol: Rol;
}

export interface RespuestaLogin {
  token: string;
  tipo: string;
  expiraEnSegundos: number;
  usuario: Usuario;
}

export interface Especialidad {
  id?: number;
  nombre: string;
  descripcion?: string | null;
}

export interface Semestre {
  id?: number;
  numero: number;
  nombre: string;
}

export interface Franja {
  id?: number;
  dia: Dia;
  horaInicio: number;
  horaFin: number;
}
export type FranjaSede = Franja & { sedeId?: number };
export type FranjaDocente = Franja & { docenteId?: number };

export interface Sede {
  id?: number;
  nombre: string;
  exclusivaPrimerSemestre: boolean;
  diasPrimerSemestre: number;
  franjas: FranjaSede[];
}

export interface Aula {
  id?: number;
  nombre: string;
  capacidad: number;
  tipo: TipoAula;
  disponible: boolean;
  sedeId: number;
  sedeNombre?: string;
}

export interface Estudiante {
  id?: number;
  codigo: string;
  nombre: string;
  correo?: string | null;
}

export interface Docente {
  id?: number;
  usuario: Usuario;
  dedicacion: Dedicacion;
  especialidades: Especialidad[];
}

export interface DisponibilidadDocente {
  docenteId: number;
  nombreDocente: string;
  dedicacion: Dedicacion;
  horasMaximas: number;
  horasDisponibles: number;
  franjas: FranjaDocente[];
}

export interface Curso {
  id?: number;
  codigo: string;
  nombre: string;
  semestreId: number;
  semestreNumero?: number;
  especialidadId: number;
  especialidadNombre?: string;
  tipoAula: TipoAula;
  intensidad: Intensidad;
  nucleoComun: boolean;
  sesionesSemana?: number;
  duracionHoras?: number;
  horasSemanales?: number;
}

export interface Grupo {
  id?: number;
  numero: number;
  cursoId: number;
  cursoCodigo?: string;
  cursoNombre?: string;
  semestreNumero?: number;
  docenteId: number;
  docenteNombre?: string;
  cantidadEstudiantes?: number;
  abierto?: boolean;
}

export interface InscritosGrupo {
  grupo: Grupo;
  estudiantes: Estudiante[];
  minimoInscritos: number;
  faltantesParaAbrir: number;
  maximoInscritos: number;
  cuposDisponibles: number;
}

// ---------------------------------------------------------------- Horario
export interface SesionHorario {
  id: number;
  grupoId: number;
  numeroSesion: number;
  dia: Dia | null;
  horaInicio: number | null;
  horaFin: number | null;
  duracionHoras: number;
  aulaId: number | null;
  aulaNombre?: string | null;
  sedeId?: number | null;
  sedeNombre?: string | null;
  causa?: string | null;
  detalleCausa?: string | null;
  ubicada: boolean;
  diaNombre?: string | null;
  cursoId: number;
  cursoCodigo: string;
  cursoNombre: string;
  semestre: number;
  numeroGrupo: number;
  docenteId: number;
  docenteNombre: string;
}

export interface DiaCalendario {
  dia: Dia;
  diaNombre: string;
  sesiones: SesionHorario[];
}

export interface CalendarioSemanal {
  descripcion: string;
  prefijo?: string | null;
  horaMinima: number;
  horaMaxima: number;
  dias: DiaCalendario[];
  sinUbicar: SesionHorario[];
  totalUbicadas: number;
  totalSinUbicar: number;
}

export interface ValidacionMovimiento {
  sesionId: number;
  valido: boolean;
  motivos: string[];
  mensajes: string[];
}

export interface CausaDescartada {
  motivo: string;
  descripcion: string;
  franjas: number;
}

export interface SesionGenerada {
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
  dia?: Dia | null;
  diaNombre?: string | null;
  horaInicio?: number | null;
  horaFin?: number | null;
  aulaId?: number | null;
  aulaNombre?: string | null;
  sedeId?: number | null;
  sedeNombre?: string | null;
  causa?: string | null;
  causaDescripcion?: string | null;
  desglose?: CausaDescartada[];
}

export interface ResultadoGeneracion {
  gruposProgramados: number;
  gruposNoAbiertos: string[];
  totalSesiones: number;
  sesionesUbicadas: number;
  sesionesSinUbicar: number;
  ubicadas: SesionGenerada[];
  sinUbicar: SesionGenerada[];
  avisos: string[];
}

export interface ResultadoSincronizacion {
  hayHorario: boolean;
  sesionesCreadas: number;
  sesionesEliminadas: number;
  sesionesDesubicadas: number;
  cambios: string[];
}

export interface FiltroHorario {
  semestre?: number | null;
  cursoId?: number | null;
  grupoId?: number | null;
  docenteId?: number | null;
  aulaId?: number | null;
  sedeId?: number | null;
  soloUbicadas?: boolean | null;
}

/** Cuerpo de error uniforme del back (RespuestaError.java). */
export interface ErrorApi {
  estado: number;
  error: string;
  mensaje: string;
  fecha: string;
}
