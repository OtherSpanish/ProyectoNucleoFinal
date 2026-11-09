export interface EstudianteModel {
  id: number | null;
  codigo: string;
  nombre: string;
  correo: string;
  semestreId: number | null;
  semestreNumero?: number | null;
}
