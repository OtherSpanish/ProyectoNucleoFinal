import { ConfiguracionGeneral, ResultadoValidacion, Sesion, Usuario } from '../models';

/**
 * Contratos de intercambio con el backend (DTOs).
 * PENDIENTE: alinear nombres y campos con los DTOs reales de Spring Boot.
 * Los modelos de `core/models.ts` se reutilizan donde coinciden con lo que devolverá el backend.
 */

// ---- Seguridad (CU12, RF10) ----
export interface LoginRequest { usuario: string; clave: string; }
/** El backend NO debe devolver la clave; `UsuarioDto` es `Usuario` sin ese campo. */
export type UsuarioDto = Omit<Usuario, 'clave'>;
export interface LoginResponse { token: string; usuario: UsuarioDto; }
export interface UsuarioNuevoRequest { nombre: string; usuario: string; clave: string; rol: Usuario['rol']; }

// ---- Parametrización (CU03) ----
export type ConfiguracionDto = ConfiguracionGeneral;

// ---- Motor (CU04) ----
export interface GenerarPropuestaResponse {
  sesiones: Sesion[]; // todas, ubicadas y sin ubicar (con `causa`)
  tiempoMs: number;
}

// ---- Edición (CU06–CU07) ----
export interface ValidarMovimientoRequest { sesionId: string; dia: number; hora: number; aulaId?: string; }
export type MoverSesionRequest = ValidarMovimientoRequest;
export interface CambiarAulaRequest { sesionId: string; aulaId: string; }
export type ValidacionDto = ResultadoValidacion;

/** Respuesta de un movimiento: resultado de la validación y, si fue válido, la sesión ya actualizada. */
export interface MoverSesionResponse { validacion: ValidacionDto; sesion?: Sesion; }

// ---- Consulta (CU08–CU09) ----
export type FormatoExportacion = 'csv' | 'pdf' | 'xlsx';

// ---- Errores ----
export interface ErrorApi { status: number; mensaje: string; detalle?: string[]; }
