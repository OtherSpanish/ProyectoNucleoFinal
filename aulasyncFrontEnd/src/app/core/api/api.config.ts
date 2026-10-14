/**
 * Configuración de la API REST (backend Java + Spring Boot, Sección 2.2 del documento).
 * PENDIENTE: confirmar con el equipo de backend la URL base y las rutas exactas.
 */
export const API_BASE_URL = 'http://localhost:8080/api';

/** Rutas por controlador del diseño (Sección 3.4.2). Ajustar cuando el backend las defina. */
export const ENDPOINTS = {
  seguridad: {
    login: '/auth/login',
    logout: '/auth/logout',
    usuarios: '/usuarios', // GET, POST; /usuarios/{id} PUT, DELETE
  },
  parametrizacion: '/configuracion',
  datosMaestros: {
    cursos: '/cursos',
    secciones: '/secciones',
    docentes: '/docentes', // /docentes/{id}/disponibilidad
    aulas: '/aulas',
    grupos: '/grupos',
  },
  motor: {
    generar: '/horario/generar',
    sinUbicar: '/horario/sin-ubicar',
  },
  edicion: {
    validar: '/horario/validar',
    mover: '/horario/mover',
    cambiarAula: '/horario/cambiar-aula',
    retirar: '/horario/retirar',
  },
  consulta: {
    horario: '/horario',
    porCurso: '/horario/curso', // /horario/curso/{cursoId}
    porDocente: '/horario/docente', // /horario/docente/{docenteId}
    exportar: '/horario/exportar',
  },
  auditoria: '/auditoria', // GET ?sesionId=
} as const;

/** Marca un método de la capa API como aún no conectado. */
export function pendiente(metodo: string, ruta: string): never {
  throw new Error(`[API pendiente] ${metodo} → ${ruta}. Implementar con HttpClient.`);
}
