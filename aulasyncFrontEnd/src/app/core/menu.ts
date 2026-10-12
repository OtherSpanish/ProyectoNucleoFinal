import { RolTecnico } from './models';

export interface ItemMenu {
  ruta: string;
  etiqueta: string;
  descripcion: string;
  roles: RolTecnico[];
}

// Verificación de la Vista por rol (Tabla 23 del documento).
export const MENU: ItemMenu[] = [
  { ruta: 'datos-maestros', etiqueta: 'Datos maestros', descripcion: 'Cursos, secciones, docentes, aulas y grupos (CU01).', roles: ['COMITE_TACTICO'] },
  { ruta: 'disponibilidad', etiqueta: 'Disponibilidad docente', descripcion: 'Franjas disponibles y dedicación de cada docente (CU02).', roles: ['COMITE_TACTICO'] },
  { ruta: 'generar', etiqueta: 'Generar propuesta', descripcion: 'Propuesta inicial y sesiones sin ubicar (CU04).', roles: ['COMITE_TACTICO'] },
  { ruta: 'editor', etiqueta: 'Editor de horario', descripcion: 'Arrastrar y soltar con validación en tiempo real (CU06–CU07).', roles: ['COMITE_TACTICO'] },
  { ruta: 'calendario', etiqueta: 'Calendario semanal', descripcion: 'Vista del horario generado (CU05).', roles: ['COMITE_TACTICO', 'DIRECCION'] },
  { ruta: 'consulta', etiqueta: 'Consultar horario', descripcion: 'Por curso o por docente, con exportación (CU08–CU09).', roles: ['DIRECCION'] },
  { ruta: 'historial', etiqueta: 'Historial de cambios', descripcion: 'Auditoría de modificaciones por sesión (CU10).', roles: ['DIRECCION', 'ADMINISTRADOR'] },
  { ruta: 'configuracion', etiqueta: 'Configuración', descripcion: 'Franja general, duración y límites de grupo (CU03).', roles: ['ADMINISTRADOR'] },
  { ruta: 'usuarios', etiqueta: 'Usuarios y roles', descripcion: 'Gestión de usuarios y roles (CU12).', roles: ['ADMINISTRADOR'] },
];
