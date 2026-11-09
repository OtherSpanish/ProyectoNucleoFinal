import { SesionGenerada, SesionHorario } from '../core/models';

/** Valores distintos y ordenados (para llenar selectores de filtro a partir de las sesiones). */
export function semestresDe(sesiones: SesionHorario[]): number[] {
  return [...new Set(sesiones.map((s) => s.semestre))].sort((a, b) => a - b);
}

/**
 * Convierte las sesiones de una propuesta del motor (que aún no tienen id) al formato del calendario.
 * Se usan ids negativos para no confundirlas con sesiones guardadas.
 */
export function generadasASesiones(lista: SesionGenerada[], ubicadas: boolean): SesionHorario[] {
  return lista.map((g, i) => ({
    id: -(i + 1) * (ubicadas ? 1 : 1000),
    grupoId: g.grupoId,
    numeroSesion: g.numeroSesion,
    dia: g.dia ?? null,
    horaInicio: g.horaInicio ?? null,
    horaFin: g.horaFin ?? null,
    duracionHoras: g.duracionHoras,
    aulaId: g.aulaId ?? null,
    aulaNombre: g.aulaNombre ?? null,
    sedeId: g.sedeId ?? null,
    sedeNombre: g.sedeNombre ?? null,
    causa: g.causa ?? null,
    detalleCausa: null,
    ubicada: ubicadas,
    diaNombre: g.diaNombre ?? null,
    cursoId: g.cursoId,
    cursoCodigo: g.cursoCodigo,
    cursoNombre: g.cursoNombre,
    semestre: g.semestre,
    numeroGrupo: g.numeroGrupo,
    docenteId: g.docenteId,
    docenteNombre: g.docenteNombre,
  }));
}

/** Descarga un archivo CSV (con BOM para que Excel respete las tildes). */
export function descargarCsv(nombre: string, filas: (string | number | null | undefined)[][]): void {
  const esc = (v: string | number | null | undefined) => {
    const t = v === null || v === undefined ? '' : String(v);
    return /[",;\n]/.test(t) ? `"${t.replace(/"/g, '""')}"` : t;
  };
  const texto = filas.map((f) => f.map(esc).join(',')).join('\r\n');
  const blob = new Blob(['﻿' + texto], { type: 'text/csv;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = nombre;
  a.click();
  URL.revokeObjectURL(url);
}
