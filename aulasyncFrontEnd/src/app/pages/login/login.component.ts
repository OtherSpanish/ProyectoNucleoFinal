import { Component, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { rutaInicial } from '../../core/menu';
import { mensajeError } from '../../core/errores';

/** Inicio de sesión: POST /api/auth/login con { correo, password }. */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [NgIf, FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  correo = '';
  password = '';
  error = '';
  aviso = this.auth.aviso;
  cargando = false;

  ingresar(): void {
    if (!this.correo.trim() || !this.password) {
      this.error = 'Escribe tu correo y tu contraseña.';
      return;
    }
    this.cargando = true;
    this.error = '';
    this.auth.login(this.correo, this.password).subscribe({
      next: (r) => {
        this.cargando = false;
        this.router.navigate([rutaInicial(r.usuario.rol)]);
      },
      error: (e) => {
        this.cargando = false;
        this.error = mensajeError(e);
      },
    });
  }
}
