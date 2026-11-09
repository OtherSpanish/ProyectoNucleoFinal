import { ItemMenuModel } from '../../models/item-menu.model';

export const MENU_POR_ROL: Record<string, ItemMenuModel[]> = {
  ADMIN: [
    { etiqueta: 'Usuarios', ruta: '/admin/usuarios', icono: 'usuarios' },
    { etiqueta: 'Docentes', ruta: '/admin/docentes', icono: 'docente' },
    { etiqueta: 'Especialidades', ruta: '/admin/especialidades', icono: 'etiqueta' },
    { etiqueta: 'Estudiantes', ruta: '/admin/estudiantes', icono: 'estudiante' },
    { etiqueta: 'Sedes', ruta: '/admin/sedes', icono: 'sede' },
    { etiqueta: 'Aulas', ruta: '/admin/aulas', icono: 'aula' },
    { etiqueta: 'Semestres', ruta: '/admin/semestres', icono: 'capas' },
    { etiqueta: 'Cómo funciona el motor', ruta: '/admin/motor', icono: 'motor' },
  ],
  COMITE: [
    { etiqueta: 'Horario', ruta: '/comite/horario', icono: 'calendario' },
    { etiqueta: 'Consultar horario', ruta: '/comite/consulta', icono: 'buscar' },
    { etiqueta: 'Vista previa del motor', ruta: '/comite/vista-previa', icono: 'motor' },
    { etiqueta: 'Cursos', ruta: '/comite/cursos', icono: 'libro' },
    { etiqueta: 'Grupos', ruta: '/comite/grupos', icono: 'usuarios' },
  ],
  DOCENTE: [
    { etiqueta: 'Mi horario', ruta: '/docente/horario', icono: 'calendario' },
    { etiqueta: 'Mis cursos', ruta: '/docente/cursos', icono: 'libro' },
    { etiqueta: 'Mi disponibilidad', ruta: '/docente/disponibilidad', icono: 'reloj' },
  ],
};

export const RUTA_INICIO_POR_ROL: Record<string, string> = {
  ADMIN: '/admin/usuarios',
  COMITE: '/comite/horario',
  DOCENTE: '/docente/horario',
};
