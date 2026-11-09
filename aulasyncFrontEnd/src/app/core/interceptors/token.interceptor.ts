import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { SesionService } from '../services/sesion.service';

/** Agrega el encabezado Authorization: Bearer <token> a cada petición. */
@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  private readonly sesionService = inject(SesionService);

  intercept(peticion: HttpRequest<unknown>, siguiente: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.sesionService.token();
    if (!token) {
      return siguiente.handle(peticion);
    }
    const autorizada = peticion.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
    return siguiente.handle(autorizada);
  }
}
