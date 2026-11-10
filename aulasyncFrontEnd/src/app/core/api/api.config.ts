/**
 * URL base del back (Spring Boot, `server.port=8081` en application.properties).
 * Si cambias el puerto o despliegas el back en otro lado, es el único lugar que hay que tocar.
 *
 * Recuerda: el back solo acepta peticiones del origen definido en `aulasync.cors.origenes`
 * (por defecto http://localhost:4200).
 */
export const API_BASE_URL = 'http://localhost:8081/api';
