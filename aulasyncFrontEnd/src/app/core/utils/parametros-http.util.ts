import { HttpParams } from '@angular/common/http';

/**
 * Convierte un objeto de filtros en parámetros de consulta, omitiendo los
 * vacíos para que el backend reciba solo los filtros que el usuario eligió.
 */
export function crearParametros(filtros: object): HttpParams {
  let parametros = new HttpParams();
  for (const [clave, valor] of Object.entries(filtros)) {
    if (valor !== null && valor !== undefined && valor !== '') {
      parametros = parametros.set(clave, String(valor));
    }
  }
  return parametros;
}
