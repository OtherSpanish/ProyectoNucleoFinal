import { EtapaMotorModel } from '../../../models/etapa-motor.model';
import { OpcionModel } from '../../../models/opcion.model';

/** Etapas de la explicación, en el mismo orden en que trabaja MotorHorario. */
export const ETAPAS_MOTOR: EtapaMotorModel[] = [
  {
    clave: 'nodos',
    titulo: 'Sesiones como nodos',
    resumen: 'Cada sesión semanal de un grupo abierto es un punto del grafo.',
    parrafos: [
      'El motor solo toma los grupos abiertos, es decir, los que alcanzan el mínimo de inscritos. Los demás quedan por fuera y se reportan aparte.',
      'Cada sesión de la semana se vuelve un nodo: un curso de 3 sesiones genera 3 nodos para cada uno de sus grupos.',
      'Aquí el color indica el semestre del curso.',
    ],
    colorPor: 'semestre',
    aristas: 'ninguna',
  },
  {
    clave: 'docente',
    titulo: 'Conflicto por docente',
    resumen: 'Un docente no puede estar en dos clases a la vez.',
    parrafos: [
      'Si dos sesiones tienen el mismo docente, se unen con una arista: en el horario final nunca podrán cruzarse.',
      'Los nodos se pintan con un color por docente, así se ven los grupos de sesiones que dependen de la misma persona.',
    ],
    colorPor: 'docente',
    aristas: 'docente',
  },
  {
    clave: 'grupo',
    titulo: 'Conflicto por grupo',
    resumen: 'Las sesiones de un mismo grupo comparten estudiantes.',
    parrafos: [
      'Las varias sesiones semanales de un grupo las ven los mismos estudiantes, así que también se unen entre sí.',
      'El color ahora identifica a cada grupo.',
    ],
    colorPor: 'grupo',
    aristas: 'grupo',
  },
  {
    clave: 'bloque',
    titulo: 'Conflicto por bloque',
    resumen: 'Materias del mismo número de grupo y semestres cercanos no se cruzan.',
    parrafos: [
      'Un bloque reúne el mismo número de grupo en semestres iguales o consecutivos: por ejemplo Cálculo I G1 y Programación I G1. Sus estudiantes suelen coincidir, así que esas materias se unen con una arista.',
      'Los grupos de una misma materia (G1 y G2 de Cálculo I) no se unen: pueden ir a la misma hora en aulas distintas.',
      'El color indica el número de bloque (G1, G2…).',
    ],
    colorPor: 'bloque',
    aristas: 'bloque',
  },
  {
    clave: 'grafo',
    titulo: 'Grafo de conflictos completo',
    resumen: 'Las tres reglas juntas forman el grafo que hay que colorear.',
    parrafos: [
      'Con las aristas por docente, grupo y bloque ya está el grafo completo. Dos nodos vecinos no pueden recibir franjas que se crucen.',
      'El grado de un nodo es su cantidad de vecinos y mide qué tan conflictiva es la sesión. Por eso el tamaño y la intensidad del color crecen con el grado.',
    ],
    colorPor: 'grado',
    aristas: 'todas',
  },
  {
    clave: 'dsu',
    titulo: 'Conjuntos disjuntos (DSU)',
    resumen: 'Un DSU agrupa las sesiones de cada grupo para repartirlas en la semana.',
    parrafos: [
      'Una estructura Disjoint Set Union une las sesiones de un mismo grupo en una componente. El anillo blanco marca la raíz de cada componente.',
      'Al ubicar una sesión, el motor consulta su componente para que dos sesiones del mismo grupo no caigan el mismo día y queden repartidas en la semana.',
    ],
    colorPor: 'grupo',
    aristas: 'grupo',
  },
  {
    clave: 'dsatur',
    titulo: 'Coloreo con DSatur',
    resumen: 'En cada paso se ubica la sesión más difícil de ubicar.',
    parrafos: [
      'Colorear el grafo es darle a cada nodo una franja (día, hora y sede) sin que dos vecinos queden en franjas que se crucen.',
      'En cada paso DSatur elige la sesión más restringida: la de mayor saturación, que en este motor es la cantidad de franjas ya bloqueadas. Si hay empate, gana la de mayor grado.',
      'Luego revisa las reglas en orden y la ubica en la franja viable del día con menos carga. Es un algoritmo voraz: no deshace decisiones anteriores.',
    ],
    colorPor: 'dsatur',
    aristas: 'actual',
  },
  {
    clave: 'resultado',
    titulo: 'Resultado',
    resumen: 'El color final de cada nodo es el día que recibió.',
    parrafos: [
      'Cada nodo ubicado se pinta con el color de su día. Los que quedaron en rojo no encontraron ninguna franja viable; su causa principal es la regla que más franjas les bloqueó.',
      'Ese resultado es el que el comité ve en el tablero de horario y luego ajusta con drag & drop.',
    ],
    colorPor: 'dia',
    aristas: 'ninguna',
  },
];

/** Reglas que el motor revisa para cada franja, en su orden de evaluación. */
export const REGLAS_MOTOR: OpcionModel[] = [
  { valor: 'DOCENTE_NO_DISPONIBLE', etiqueta: 'Disponibilidad del docente' },
  { valor: 'CARGA_DOCENTE', etiqueta: 'Tope de horas del docente' },
  { valor: 'SEDE_NO_PERMITIDA', etiqueta: 'Sede permitida ese día' },
  { valor: 'CHOQUE_DOCENTE', etiqueta: 'Choque con el docente' },
  { valor: 'CHOQUE_GRUPO', etiqueta: 'Choque con su grupo' },
  { valor: 'CHOQUE_BLOQUE', etiqueta: 'Choque con su bloque' },
  { valor: 'MISMO_DIA_GRUPO', etiqueta: 'Un día por grupo (DSU)' },
  { valor: 'SIN_AULA', etiqueta: 'Aula libre y compatible' },
];

export const CATALOGO_MODOS_COLOR: OpcionModel[] = [
  { valor: 'semestre', etiqueta: 'Semestre' },
  { valor: 'docente', etiqueta: 'Docente' },
  { valor: 'grupo', etiqueta: 'Grupo (componente DSU)' },
  { valor: 'bloque', etiqueta: 'Bloque (número de grupo)' },
  { valor: 'grado', etiqueta: 'Grado (cantidad de conflictos)' },
  { valor: 'dsatur', etiqueta: 'Estado en DSatur' },
  { valor: 'dia', etiqueta: 'Día asignado' },
];

export const COLORES_SEMESTRE: string[] = [
  '#e8890c', '#5c7a2e', '#2f7f86', '#8a4fa3', '#c2453a', '#3d5ea8', '#9a7b12', '#b2547c', '#4b5563',
];

export const COLORES_BLOQUE: string[] = ['#e8890c', '#2f7f86', '#8a4fa3', '#5c7a2e', '#c2453a', '#3d5ea8'];

export const COLORES_DIA: Record<string, string> = {
  MONDAY: '#3d5ea8',
  TUESDAY: '#2f7f86',
  WEDNESDAY: '#5c7a2e',
  THURSDAY: '#9a7b12',
  FRIDAY: '#8a4fa3',
  SATURDAY: '#b2547c',
};

/** Escala de grado: [grado mínimo, color]. */
export const ESCALA_GRADO: [number, string][] = [
  [0, '#f5d9a8'],
  [5, '#f0b45c'],
  [10, '#e8890c'],
  [15, '#b8650a'],
  [20, '#7a3f05'],
];

export const COLOR_ARISTA_DOCENTE = '#3d5ea8';
export const COLOR_ARISTA_GRUPO = '#e8890c';
export const COLOR_ARISTA_BLOQUE = '#8a4fa3';
export const COLOR_PENDIENTE = '#d4d6cb';
export const COLOR_ACTUAL = '#e8890c';
export const COLOR_SIN_UBICAR = '#c2361f';
