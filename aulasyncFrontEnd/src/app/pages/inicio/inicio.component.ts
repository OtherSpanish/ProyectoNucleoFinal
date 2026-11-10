import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { menuPara } from '../../core/menu';
import { etiquetaRol } from '../../core/models';
import { HorarioApi } from '../../core/api';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink],
  templateUrl: './inicio.component.html',
})
export class InicioComponent implements OnInit {
  private auth = inject(AuthService);
  private horarioApi = inject(HorarioApi);

  usuario = this.auth.usuario;
  rol = computed(() => etiquetaRol(this.auth.rol()));
  accesos = computed(() => menuPara(this.auth.rol()));
  resumen = signal<{ ubicadas: number; sinUbicar: number } | null>(null);

  ngOnInit(): void {
    this.horarioApi.listar().subscribe({
      next: (sesiones) => {
        const ubicadas = sesiones.filter((s) => s.ubicada).length;
        this.resumen.set({ ubicadas, sinUbicar: sesiones.length - ubicadas });
      },
      error: () => this.resumen.set(null),
    });
  }
}
