import { Component, Input, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SemestresApi } from '../../core/api';
import { Semestre } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';

@Component({
  selector: 'app-semestres',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent],
  template: `
    <app-alerta [error]="error" [ok]="ok"></app-alerta>
    <p class="ayuda">El back crea los semestres 1 a 9 la primera vez que arranca; el número debe estar entre 1 y 9.</p>
    <div class="barra">
      <button type="button" class="primario" *ngIf="puedeEditar" (click)="nuevo()">Nuevo semestre</button>
    </div>

    <form class="card" *ngIf="mostrarForm" (ngSubmit)="guardar()">
      <h3>{{ editandoId === null ? 'Nuevo semestre' : 'Editar semestre' }}</h3>
      <div class="fila-form">
        <label>Número (1–9) <input name="numero" type="number" min="1" max="9" [(ngModel)]="form.numero" required></label>
        <label>Nombre <input name="nombre" [(ngModel)]="form.nombre" required></label>
      </div>
      <button type="submit" class="primario">Guardar</button>
      <button type="button" (click)="mostrarForm = false">Cancelar</button>
    </form>

    <p class="vacio" *ngIf="lista.length === 0">No hay semestres.</p>
    <table *ngIf="lista.length > 0">
      <thead><tr><th>Número</th><th>Nombre</th><th *ngIf="puedeEditar"></th></tr></thead>
      <tbody>
        <tr *ngFor="let s of lista">
          <td>{{ s.numero }}</td>
          <td>{{ s.nombre }}</td>
          <td class="acciones" *ngIf="puedeEditar">
            <button type="button" (click)="editar(s)">Editar</button>
            <button type="button" class="peligro" (click)="eliminar(s)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
  `,
})
export class SemestresComponent implements OnInit {
  private api = inject(SemestresApi);
  @Input() puedeEditar = false;

  lista: Semestre[] = [];
  mostrarForm = false;
  editandoId: number | null = null;
  form: Semestre = { numero: 1, nombre: '' };
  error = '';
  ok = '';

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.api.listar().subscribe({ next: (l) => (this.lista = l), error: (e) => (this.error = mensajeError(e)) });
  }

  nuevo(): void {
    this.limpiar();
    this.editandoId = null;
    const siguiente = this.lista.reduce((m, s) => Math.max(m, s.numero), 0) + 1;
    this.form = { numero: Math.min(siguiente, 9), nombre: `Semestre ${Math.min(siguiente, 9)}` };
    this.mostrarForm = true;
  }
  editar(s: Semestre): void { this.limpiar(); this.editandoId = s.id ?? null; this.form = { ...s }; this.mostrarForm = true; }

  guardar(): void {
    this.limpiar();
    const cuerpo: Semestre = { numero: Number(this.form.numero), nombre: this.form.nombre.trim() };
    const p = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    p.subscribe({
      next: (r) => { this.ok = `Semestre ${r.numero} guardado.`; this.mostrarForm = false; this.cargar(); },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  eliminar(s: Semestre): void {
    if (!s.id || !confirm(`¿Eliminar el semestre ${s.numero}?`)) return;
    this.limpiar();
    this.api.eliminar(s.id).subscribe({
      next: () => { this.ok = `Semestre ${s.numero} eliminado.`; this.cargar(); },
      error: (er) => (this.error = mensajeError(er)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
