import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { DataService } from '../../core/data.service';
import { etiquetaRol } from '../../core/models';

/** CU12 – Inicio de sesión (RF10, CA10). */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  data = inject(DataService);

  usuario = '';
  clave = '';
  error = '';

  rol = etiquetaRol;

  ingresar(): void {
    if (this.auth.login(this.usuario.trim(), this.clave)) {
      this.error = '';
      this.router.navigate(['/inicio']);
    } else {
      this.error = 'Usuario o contraseña incorrectos, o usuario inactivo.';
    }
  }

  usar(usuario: string, clave: string): void {
    this.usuario = usuario;
    this.clave = clave;
  }
}
