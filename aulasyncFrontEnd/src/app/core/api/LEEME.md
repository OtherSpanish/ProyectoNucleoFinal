# Capa API (pendiente de conectar)

Un servicio por controlador del diseño (Sección 3.4.2). Ningún componente los usa todavía;
la app sigue funcionando con `DataService` y `ScheduleService` en memoria.

| Servicio API | Reemplaza | Casos de uso |
|---|---|---|
| SeguridadApi | AuthService (login), usuarios en DataService | CU12 |
| ParametrizacionApi | `DataService.config` | CU03 |
| DatosMaestrosApi | cursos, secciones, docentes, aulas, grupos de DataService | CU01, CU02 |
| MotorHorarioApi | `ScheduleService.generarPropuesta()` | CU04 |
| EdicionHorarioApi | `ScheduleService.validar/mover/cambiarAula/retirar` | CU06, CU07 |
| ConsultaHorarioApi | `ScheduleService.sesiones` y exportación CSV local | CU05, CU08, CU09 |
| AuditoriaApi | `ScheduleService.auditoria` | CU10 |

## Pasos para conectar
1. Confirmar con backend la URL base y rutas; ajustar `api.config.ts` y `dto.ts`.
2. En `app.config.ts`, activar `provideHttpClient(withInterceptors([authInterceptor]))`.
3. Implementar cada método con `this.http` (hay una línea de ejemplo comentada en `SeguridadApi.login`).
4. Cambiar los servicios en memoria para que llamen a la API y guarden el resultado en sus signals;
   así los componentes no cambian.
5. Quitar la heurística simulada y la validación local de `ScheduleService`.
