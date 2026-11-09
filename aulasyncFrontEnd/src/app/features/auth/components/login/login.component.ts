import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { SesionService } from '../../../../core/services/sesion.service';
import { CredencialesModel } from '../../../../models/credenciales.model';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly sesionService = inject(SesionService);
  private readonly router = inject(Router);

  protected credenciales: CredencialesModel = { correo: '', password: '' };
  protected readonly enviando = signal(false);

  iniciarSesion(): void {
    this.enviando.set(true);
    this.authService.iniciarSesion(this.credenciales).subscribe({
      next: (respuesta) => {
        this.sesionService.iniciar(respuesta);
        this.router.navigate(['/']);
      },
      error: () => this.enviando.set(false),
    });
  }
}
