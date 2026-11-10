import { Component, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { PortalDocenteApi } from '../../core/api';
import { Grupo, InscritosGrupo } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';

/** Grupos del docente (GET /api/mi/cursos) y sus estudiantes (GET /api/mi/grupos/{id}/estudiantes). */
@Component({
  selector: 'app-mis-cursos',
  standalone: true,
  imports: [NgFor, NgIf, AlertaComponent],
  template: `
    <h2>Mis cursos</h2>
    <app-alerta [error]="error"></app-alerta>
    <p class="vacio" *ngIf="cargado && grupos.length === 0 && !error">No tienes grupos asignados.</p>

    <table *ngIf="grupos.length > 0">
      <thead><tr><th>Sem.</th><th>Curso</th><th>Grupo</th><th>Inscritos</th><th>Estado</th><th></th></tr></thead>
      <tbody>
        <tr *ngFor="let g of grupos" [class.fila-activa]="detalle?.grupo?.id === g.id">
          <td>{{ g.semestreNumero }}</td>
          <td>{{ g.cursoCodigo }} – {{ g.cursoNombre }}</td>
          <td>{{ g.numero }}</td>
          <td>{{ g.cantidadEstudiantes }}</td>
          <td><span class="chip" [class.ok]="g.abierto" [class.pendiente]="!g.abierto">{{ g.abierto ? 'Abierto' : 'Sin abrir' }}</span></td>
          <td class="acciones"><button type="button" (click)="ver(g)">Ver estudiantes</button></td>
        </tr>
      </tbody>
    </table>

    <div class="card" *ngIf="detalle">
      <h3>Estudiantes de {{ detalle.grupo.cursoCodigo }} · Grupo {{ detalle.grupo.numero }}</h3>
      <p class="resumen">{{ detalle.estudiantes.length }} inscritos · cupos disponibles: {{ detalle.cuposDisponibles }}</p>
      <p class="vacio" *ngIf="detalle.estudiantes.length === 0">Sin estudiantes inscritos.</p>
      <table *ngIf="detalle.estudiantes.length > 0">
        <thead><tr><th>Código</th><th>Nombre</th><th>Correo</th></tr></thead>
        <tbody>
          <tr *ngFor="let e of detalle.estudiantes"><td>{{ e.codigo }}</td><td>{{ e.nombre }}</td><td>{{ e.correo }}</td></tr>
        </tbody>
      </table>
      <button type="button" (click)="detalle = null">Cerrar</button>
    </div>
  `,
})
export class MisCursosComponent implements OnInit {
  private api = inject(PortalDocenteApi);

  grupos: Grupo[] = [];
  detalle: InscritosGrupo | null = null;
  cargado = false;
  error = '';

  ngOnInit(): void {
    this.api.cursos().subscribe({
      next: (g) => { this.grupos = g; this.cargado = true; },
      error: (e) => { this.error = mensajeError(e); this.cargado = true; },
    });
  }

  ver(g: Grupo): void {
    if (!g.id) return;
    this.error = '';
    this.api.estudiantes(g.id).subscribe({
      next: (d) => (this.detalle = d),
      error: (e) => (this.error = mensajeError(e)),
    });
  }
}
