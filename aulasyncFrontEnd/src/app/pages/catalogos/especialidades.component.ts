import { Component, Input, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EspecialidadesApi } from '../../core/api';
import { Especialidad } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';

@Component({
  selector: 'app-especialidades',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent],
  template: `
    <app-alerta [error]="error" [ok]="ok"></app-alerta>
    <div class="barra">
      <button type="button" class="primario" *ngIf="puedeEditar" (click)="nuevo()">Nueva especialidad</button>
    </div>

    <form class="card" *ngIf="mostrarForm" (ngSubmit)="guardar()">
      <h3>{{ editandoId === null ? 'Nueva especialidad' : 'Editar especialidad' }}</h3>
      <div class="fila-form">
        <label>Nombre <input name="nombre" [(ngModel)]="form.nombre" required></label>
        <label class="ancho">Descripción <input name="descripcion" [(ngModel)]="form.descripcion"></label>
      </div>
      <button type="submit" class="primario">Guardar</button>
      <button type="button" (click)="mostrarForm = false">Cancelar</button>
    </form>

    <p class="vacio" *ngIf="lista.length === 0">No hay especialidades.</p>
    <table *ngIf="lista.length > 0">
      <thead><tr><th>Nombre</th><th>Descripción</th><th *ngIf="puedeEditar"></th></tr></thead>
      <tbody>
        <tr *ngFor="let e of lista">
          <td>{{ e.nombre }}</td>
          <td>{{ e.descripcion }}</td>
          <td class="acciones" *ngIf="puedeEditar">
            <button type="button" (click)="editar(e)">Editar</button>
            <button type="button" class="peligro" (click)="eliminar(e)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
  `,
})
export class EspecialidadesComponent implements OnInit {
  private api = inject(EspecialidadesApi);
  @Input() puedeEditar = false;

  lista: Especialidad[] = [];
  mostrarForm = false;
  editandoId: number | null = null;
  form: Especialidad = { nombre: '', descripcion: '' };
  error = '';
  ok = '';

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.listar().subscribe({ next: (l) => (this.lista = l), error: (e) => (this.error = mensajeError(e)) });
  }

  nuevo(): void { this.limpiar(); this.editandoId = null; this.form = { nombre: '', descripcion: '' }; this.mostrarForm = true; }
  editar(e: Especialidad): void { this.limpiar(); this.editandoId = e.id ?? null; this.form = { ...e }; this.mostrarForm = true; }

  guardar(): void {
    this.limpiar();
    const cuerpo: Especialidad = { nombre: this.form.nombre.trim(), descripcion: this.form.descripcion?.trim() || null };
    const p = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    p.subscribe({
      next: (r) => { this.ok = `Especialidad «${r.nombre}» guardada.`; this.mostrarForm = false; this.cargar(); },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  eliminar(e: Especialidad): void {
    if (!e.id || !confirm(`¿Eliminar la especialidad «${e.nombre}»?`)) return;
    this.limpiar();
    this.api.eliminar(e.id).subscribe({
      next: () => { this.ok = `Especialidad «${e.nombre}» eliminada.`; this.cargar(); },
      error: (er) => (this.error = mensajeError(er)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
