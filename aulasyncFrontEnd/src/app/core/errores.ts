import { HttpErrorResponse } from '@angular/common/http';
import { API_BASE_URL } from './api/api.config';

/**
 * Convierte cualquier error en un texto para mostrar al usuario.
 * El back responde siempre `{ estado, error, mensaje, fecha }` (LanzadorException); se usa `mensaje`.
 */
export function mensajeError(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) {
      return `No hay conexión con el servidor (${API_BASE_URL}). Verifica que el back esté encendido y que CORS permita este origen.`;
    }
    const cuerpo = err.error;
    if (cuerpo && typeof cuerpo === 'object' && typeof cuerpo.mensaje === 'string' && cuerpo.mensaje) {
      return cuerpo.mensaje;
    }
    if (typeof cuerpo === 'string' && cuerpo.trim()) return cuerpo;
    if (err.status === 401) return 'Debes iniciar sesión o tu sesión expiró.';
    if (err.status === 403) return 'Tu rol no tiene permiso para realizar esta operación.';
    if (err.status === 404) return 'No se encontró el recurso solicitado.';
    return `Error ${err.status} del servidor.`;
  }
  if (err instanceof Error) return err.message;
  return 'Ocurrió un error inesperado.';
}
