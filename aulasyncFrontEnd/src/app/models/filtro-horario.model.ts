/** Filtros de GET /api/horarios (todos opcionales). */
export interface FiltroHorarioModel {
  semestre: number | null;
  cursoId: number | null;
  grupoId: number | null;
  docenteId: number | null;
  aulaId: number | null;
  sedeId: number | null;
  soloUbicadas: boolean | null;
}
