import { Injectable, signal } from '@angular/core';
import {
  Aula, ConfiguracionGeneral, Curso, Docente, GrupoEstudiantes, Seccion, Usuario,
} from './models';

/**
 * Datos maestros en memoria (conjunto sintético, restricción RP2).
 * Cuando exista el backend Spring Boot, aquí se reemplaza por llamadas HttpClient.
 * Los datos de ejemplo salen de los casos CP1 y CP2 del documento (Sección 4.3).
 */
@Injectable({ providedIn: 'root' })
export class DataService {
  private contador = 100;
  nuevoId(prefijo: string): string {
    return `${prefijo}${++this.contador}`;
  }

  readonly usuarios = signal<Usuario[]>([
    { id: 'U1', nombre: 'Comité Táctico (demo)', usuario: 'comite', clave: 'comite123', rol: 'COMITE_TACTICO', activo: true },
    { id: 'U2', nombre: 'Director del Programa (demo)', usuario: 'direccion', clave: 'direccion123', rol: 'DIRECCION', activo: true },
    { id: 'U3', nombre: 'Administrador (demo)', usuario: 'admin', clave: 'admin123', rol: 'ADMINISTRADOR', activo: true },
  ]);

  readonly config = signal<ConfiguracionGeneral>({
    horaInicioSemana: 7,
    horaFinSemana: 22,
    horaInicioSabado: 7,
    horaFinSabado: 13,
    almuerzoInicio: 12,
    almuerzoFin: 13,
    duracionSesionHoras: 2,
    sesionesMaxSemana: 4,
    limiteGrupo: 40,
    toleranciaPorcentaje: 10,
  });

  readonly docentes = signal<Docente[]>([
    {
      id: 'D1', nombre: 'Docente A', dedicacion: 'TIEMPO_COMPLETO',
      disponibilidad: [
        { dia: 1, horaInicio: 7, horaFin: 13 },
        { dia: 2, horaInicio: 7, horaFin: 18 },
        { dia: 3, horaInicio: 7, horaFin: 18 },
        { dia: 4, horaInicio: 7, horaFin: 18 },
        { dia: 5, horaInicio: 7, horaFin: 18 },
      ],
    },
    {
      id: 'D2', nombre: 'Docente B', dedicacion: 'MEDIO_TIEMPO',
      disponibilidad: [1, 2, 3, 4, 5].map((dia) => ({ dia, horaInicio: 7, horaFin: 18 })),
    },
    {
      id: 'D3', nombre: 'Docente C', dedicacion: 'UN_CUARTO',
      disponibilidad: [{ dia: 1, horaInicio: 7, horaFin: 11 }],
    },
  ]);

  readonly aulas = signal<Aula[]>([
    { id: 'A1', nombre: 'A-204', capacidad: 40, tipo: 'CONVENCIONAL' },
    { id: 'A2', nombre: 'Lab-03', capacidad: 40, tipo: 'COMPUTO' },
    { id: 'A3', nombre: 'A-205', capacidad: 40, tipo: 'CONVENCIONAL' },
  ]);

  readonly grupos = signal<GrupoEstudiantes[]>([
    { id: 'G1', nombre: 'Grupo X', semestre: 4, estudiantes: 32 },
    { id: 'G2', nombre: 'Grupo Y', semestre: 6, estudiantes: 28 },
    { id: 'G3', nombre: 'Grupo Z', semestre: 5, estudiantes: 30 },
  ]);

  readonly cursos = signal<Curso[]>([
    { id: 'C1', nombre: 'Estructuras de Datos', sesionesSemanales: 2, duracionHoras: 2, tipoAula: 'CONVENCIONAL' },
    { id: 'C2', nombre: 'Cálculo Diferencial', sesionesSemanales: 2, duracionHoras: 2, tipoAula: 'CONVENCIONAL' },
    { id: 'C3', nombre: 'Bases de Datos 1', sesionesSemanales: 2, duracionHoras: 2, tipoAula: 'CONVENCIONAL' },
    { id: 'C4', nombre: 'Ingeniería de Software', sesionesSemanales: 1, duracionHoras: 4, tipoAula: 'COMPUTO' },
    { id: 'C5', nombre: 'Redes', sesionesSemanales: 2, duracionHoras: 2, tipoAula: 'CONVENCIONAL' },
  ]);

  readonly secciones = signal<Seccion[]>([
    { id: 'SC1', cursoId: 'C1', numero: 1, docenteId: 'D1', grupoId: 'G1' },
    { id: 'SC2', cursoId: 'C2', numero: 1, docenteId: 'D1', grupoId: 'G1' },
    { id: 'SC3', cursoId: 'C3', numero: 1, docenteId: 'D2', grupoId: 'G1' },
    { id: 'SC4', cursoId: 'C4', numero: 1, docenteId: 'D2', grupoId: 'G2' },
    { id: 'SC5', cursoId: 'C5', numero: 1, docenteId: 'D3', grupoId: 'G3' },
  ]);

  // ---- Utilidades de consulta ----
  nombreCurso(id: string): string { return this.cursos().find((c) => c.id === id)?.nombre ?? '—'; }
  nombreDocente(id: string): string { return this.docentes().find((d) => d.id === id)?.nombre ?? '—'; }
  nombreGrupo(id: string): string { return this.grupos().find((g) => g.id === id)?.nombre ?? '—'; }
  nombreAula(id?: string): string { return this.aulas().find((a) => a.id === id)?.nombre ?? '—'; }

  actualizarDocente(id: string, cambios: Partial<Docente>): void {
    this.docentes.update((l) => l.map((d) => (d.id === id ? { ...d, ...cambios } : d)));
  }
}
