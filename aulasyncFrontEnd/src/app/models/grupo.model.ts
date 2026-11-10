export interface GrupoModel {
  id: number | null;
  numero: number | null;
  cursoId: number | null;
  cursoCodigo?: string;
  cursoNombre?: string;
  semestreNumero?: number;
  docenteId: number | null;
  docenteNombre?: string;
  cantidadEstudiantes?: number;
  abierto?: boolean;
}
