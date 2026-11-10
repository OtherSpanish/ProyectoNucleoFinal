import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { TrazaMotorModel } from '../models/traza-motor.model';

/** Consume GET /api/motor/traza (solo ADMIN). */
@Injectable({ providedIn: 'root' })
export class TrazaMotorService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/motor/traza`;

  obtener(): Observable<TrazaMotorModel> {
    return this.http.get<TrazaMotorModel>(this.url);
  }
}
