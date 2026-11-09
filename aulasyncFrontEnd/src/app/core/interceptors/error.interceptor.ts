import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, throwError } from 'rxjs';

import { RespuestaErrorModel } from '../../models/respuesta-error.model';
import { NotificacionService } from '../services/notificacion.service';
import { SesionService } from '../services/sesion.service';

/**
 * Muestra el mensaje que envía el backend (RespuestaError.mensaje) y cierra la
 * sesión si el token ya no es válido. El error se re-lanza para que cada
 * componente pueda apagar sus indicadores de carga.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  private readonly notificacionService = inject(NotificacionService);
  private readonly sesionService = inject(SesionService);
  private readonly router = inject(Router);

  intercept(peticion: HttpRequest<unknown>, siguiente: HttpHandler): Observable<HttpEvent<unknown>> {
    return siguiente.handle(peticion).pipe(catchError((error: HttpErrorResponse) => this.manejar(error)));
  }

  private manejar(error: HttpErrorResponse): Observable<never> {
    const cuerpo = error.error as RespuestaErrorModel | null;
    if (error.status === 0) {
      this.notificacionService.error('No se pudo conectar con el servidor de AulaSync.');
    } else if (cuerpo && cuerpo.mensaje) {
      this.notificacionService.error(cuerpo.mensaje);
    } else {
      this.notificacionService.error(`Error ${error.status}: ${error.statusText}`);
    }
    if (error.status === 401 && this.sesionService.estaAutenticado()) {
      this.sesionService.cerrar();
      this.router.navigate(['/login']);
    }
    return throwError(() => error);
  }
}
