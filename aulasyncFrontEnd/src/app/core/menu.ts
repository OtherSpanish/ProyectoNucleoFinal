import { Rol } from './models';

export interface ItemMenu {
  ruta: string;
  etiqueta: string;
  descripcion: string;
  roles: Rol[];
}

/** Opciones del menú por rol. Los permisos coinciden con los @PreAuthorize del back. */
export const MENU: ItemMenu[] = [
  // ---- Docente
  { ruta: 'mi-horario', etiqueta: 'Mi horario', descripcion: 'Tu calendario semanal de clases.', roles: ['DOCENTE'] },
  { ruta: 'mis-cursos', etiqueta: 'Mis cursos', descripcion: 'Los grupos que dictas y sus estudiantes.', roles: ['DOCENTE'] },
  { ruta: 'mi-disponibilidad', etiqueta: 'Mi disponibilidad', descripcion: 'Las franjas en que puedes dictar clase.', roles: ['DOCENTE'] },

  // ---- Comité táctico
  { ruta: 'cursos', etiqueta: 'Cursos', descripcion: 'Materias por semestre, tipo de aula e intensidad horaria.', roles: ['COMITE', 'ADMIN'] },
  { ruta: 'grupos', etiqueta: 'Grupos e inscritos', descripcion: 'Grupos de cada curso, docente asignado y estudiantes inscritos.', roles: ['COMITE', 'ADMIN'] },
  { ruta: 'generar', etiqueta: 'Generar horario', descripcion: 'Propuesta del motor, horario guardado y sesiones sin ubicar.', roles: ['COMITE'] },
  { ruta: 'editor', etiqueta: 'Editor de horario', descripcion: 'Arrastrar y soltar sesiones con validación en tiempo real.', roles: ['COMITE'] },

  // ---- Administrador
  { ruta: 'usuarios', etiqueta: 'Usuarios', descripcion: 'Cuentas de acceso y roles del sistema.', roles: ['ADMIN'] },

  // ---- Compartidas
  { ruta: 'docentes', etiqueta: 'Docentes', descripcion: 'Docentes, dedicación, especialidades y disponibilidad.', roles: ['ADMIN', 'COMITE'] },
  { ruta: 'catalogos', etiqueta: 'Catálogos', descripcion: 'Especialidades, semestres, sedes, aulas y estudiantes.', roles: ['ADMIN', 'COMITE'] },
  { ruta: 'calendario', etiqueta: 'Calendario semanal', descripcion: 'El horario guardado en un calendario por semestre.', roles: ['ADMIN', 'COMITE'] },
  { ruta: 'consulta', etiqueta: 'Consultar horario', descripcion: 'Listado con filtros y exportación a CSV.', roles: ['ADMIN', 'COMITE'] },
];

export function menuPara(rol: Rol | null): ItemMenu[] {
  return rol ? MENU.filter((m) => m.roles.includes(rol)) : [];
}

/** Primera pantalla de cada rol. */
export function rutaInicial(rol: Rol | null): string {
  return rol === 'DOCENTE' ? '/mi-horario' : '/inicio';
}
