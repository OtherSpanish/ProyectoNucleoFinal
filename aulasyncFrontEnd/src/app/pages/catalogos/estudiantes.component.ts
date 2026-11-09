import { Component, Input, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EstudiantesApi } from '../../core/api';
import { Estudiante } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';

@Component({
  selector: 'app-estudiantes',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent],
  template: `
    <app-alerta [error]="error" [ok]="ok"></app-alerta>
    <div class="barra">
      <label class="inline">Buscar
        <input name="buscar" [(ngModel)]="texto" placeholder="Código, nombre o correo">
      </label>
      <button type="button" class="primario" *ngIf="puedeEditar" (click)="nuevo()">Nuevo estudiante</button>
    </div>

    <form class="card" *ngIf="mostrarForm" (ngSubmit)="guardar()">
      <h3>{{ editandoId === null ? 'Nuevo estudiante' : 'Editar estudiante' }}</h3>
      <div class="fila-form">
        <label>Código <input name="codigo" [(ngModel)]="form.codigo" required></label>
        <label>Nombre <input name="nombre" [(ngModel)]="form.nombre" required></label>
        <label>Correo (opcional) <input name="correo" type="email" [(ngModel)]="form.correo"></label>
      </div>
      <button type="submit" class="primario">Guardar</button>
      <button type="button" (click)="mostrarForm = false">Cancelar</button>
    </form>

    <p class="vacio" *ngIf="filtrados().length === 0">No hay estudiantes{{ texto ? ' que coincidan' : '' }}.</p>
    <p class="resumen" *ngIf="lista.length > 0">{{ filtrados().length }} de {{ lista.length }} estudiantes.</p>
    <table *ngIf="filtrados().length > 0">
      <thead><tr><th>Código</th><th>Nombre</th><th>Correo</th><th *ngIf="puedeEditar"></th></tr></thead>
      <tbody>
        <tr *ngFor="let e of filtrados()">
          <td>{{ e.codigo }}</td>
          <td>{{ e.nombre }}</td>
          <td>{{ e.correo }}</td>
          <td class="acciones" *ngIf="puedeEditar">
            <button type="button" (click)="editar(e)">Editar</button>
            <button type="button" class="peligro" (click)="eliminar(e)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
  `,
})
export class EstudiantesComponent implements OnInit {
  private api = inject(EstudiantesApi);
  @Input() puedeEditar = false;

  lista: Estudiante[] = [];
  texto = '';
  mostrarForm = false;
  editandoId: number | null = null;
  form: Estudiante = { codigo: '', nombre: '', correo: '' };
  error = '';
  ok = '';

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.listar().subscribe({ next: (l) => (this.lista = l), error: (e) => (this.error = mensajeError(e)) });
  }

  filtrados(): Estudiante[] {
    const t = this.texto.trim().toLowerCase();
    if (!t) return this.lista;
    return this.lista.filter((e) => `${e.codigo} ${e.nombre} ${e.correo ?? ''}`.toLowerCase().includes(t));
  }

  nuevo(): void { this.limpiar(); this.editandoId = null; this.form = { codigo: '', nombre: '', correo: '' }; this.mostrarForm = true; }
  editar(e: Estudiante): void { this.limpiar(); this.editandoId = e.id ?? null; this.form = { ...e, correo: e.correo ?? '' }; this.mostrarForm = true; }

  guardar(): void {
    this.limpiar();
    const cuerpo: Estudiante = {
      codigo: this.form.codigo.trim(),
      nombre: this.form.nombre.trim(),
      correo: this.form.correo?.trim() || null,
    };
    const p = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    p.subscribe({
      next: (r) => { this.ok = `Estudiante ${r.nombre} guardado.`; this.mostrarForm = false; this.cargar(); },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  eliminar(e: Estudiante): void {
    if (!e.id || !confirm(`¿Eliminar al estudiante ${e.nombre}?`)) return;
    this.limpiar();
    this.api.eliminar(e.id).subscribe({
      next: () => { this.ok = `Estudiante ${e.nombre} eliminado.`; this.cargar(); },
      error: (er) => (this.error = mensajeError(er)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
