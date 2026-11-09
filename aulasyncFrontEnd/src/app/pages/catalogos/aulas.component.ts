import { Component, Input, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AulasApi, SedesApi } from '../../core/api';
import { Aula, Sede, TIPOS_AULA, etiquetaTipoAula } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';

@Component({
  selector: 'app-aulas',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent],
  template: `
    <app-alerta [error]="error" [ok]="ok"></app-alerta>
    <div class="barra">
      <label class="inline">Sede
        <select name="fsede" [(ngModel)]="filtroSede" (change)="cargar()">
          <option [ngValue]="null">Todas</option>
          <option *ngFor="let s of sedes" [ngValue]="s.id">{{ s.nombre }}</option>
        </select>
      </label>
      <button type="button" class="primario" *ngIf="puedeEditar" (click)="nuevo()" [disabled]="sedes.length === 0">Nueva aula</button>
    </div>
    <p class="ayuda" *ngIf="sedes.length === 0">Primero debe existir al menos una sede.</p>

    <form class="card" *ngIf="mostrarForm" (ngSubmit)="guardar()">
      <h3>{{ editandoId === null ? 'Nueva aula' : 'Editar aula' }}</h3>
      <div class="fila-form">
        <label>Nombre <input name="nombre" [(ngModel)]="form.nombre" required></label>
        <label>Sede
          <select name="sede" [(ngModel)]="form.sedeId" required>
            <option *ngFor="let s of sedes" [ngValue]="s.id">{{ s.nombre }}</option>
          </select>
        </label>
        <label>Capacidad (1–300)
          <input name="capacidad" type="number" min="1" max="300" [(ngModel)]="form.capacidad" required>
        </label>
        <label>Tipo
          <select name="tipo" [(ngModel)]="form.tipo">
            <option *ngFor="let t of tipos" [value]="t.valor">{{ t.etiqueta }}</option>
          </select>
        </label>
        <label class="check"><input type="checkbox" name="disponible" [(ngModel)]="form.disponible"> Disponible para el horario</label>
      </div>
      <button type="submit" class="primario">Guardar</button>
      <button type="button" (click)="mostrarForm = false">Cancelar</button>
    </form>

    <p class="vacio" *ngIf="lista.length === 0">No hay aulas.</p>
    <table *ngIf="lista.length > 0">
      <thead><tr><th>Aula</th><th>Sede</th><th>Capacidad</th><th>Tipo</th><th>Disponible</th><th *ngIf="puedeEditar"></th></tr></thead>
      <tbody>
        <tr *ngFor="let a of lista">
          <td>{{ a.nombre }}</td>
          <td>{{ a.sedeNombre }}</td>
          <td>{{ a.capacidad }}</td>
          <td>{{ tipo(a.tipo) }}</td>
          <td>{{ a.disponible ? 'Sí' : 'No' }}</td>
          <td class="acciones" *ngIf="puedeEditar">
            <button type="button" (click)="editar(a)">Editar</button>
            <button type="button" class="peligro" (click)="eliminar(a)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
  `,
})
export class AulasComponent implements OnInit {
  private api = inject(AulasApi);
  private sedesApi = inject(SedesApi);
  @Input() puedeEditar = false;

  lista: Aula[] = [];
  sedes: Sede[] = [];
  filtroSede: number | null = null;
  tipos = TIPOS_AULA;
  tipo = etiquetaTipoAula;

  mostrarForm = false;
  editandoId: number | null = null;
  form: Aula = this.vacia();
  error = '';
  ok = '';

  ngOnInit(): void {
    this.sedesApi.listar().subscribe({ next: (s) => (this.sedes = s), error: (e) => (this.error = mensajeError(e)) });
    this.cargar();
  }

  private vacia(): Aula {
    return { nombre: '', capacidad: 30, tipo: 'CONVENCIONAL', disponible: true, sedeId: this.sedes[0]?.id ?? 0 };
  }

  cargar(): void {
    this.api.listar({ sedeId: this.filtroSede }).subscribe({ next: (l) => (this.lista = l), error: (e) => (this.error = mensajeError(e)) });
  }

  nuevo(): void { this.limpiar(); this.editandoId = null; this.form = this.vacia(); this.mostrarForm = true; }
  editar(a: Aula): void { this.limpiar(); this.editandoId = a.id ?? null; this.form = { ...a }; this.mostrarForm = true; }

  guardar(): void {
    this.limpiar();
    const cuerpo: Aula = {
      nombre: this.form.nombre.trim(),
      capacidad: Number(this.form.capacidad),
      tipo: this.form.tipo,
      disponible: !!this.form.disponible,
      sedeId: Number(this.form.sedeId),
    };
    const p = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    p.subscribe({
      next: (r) => { this.ok = `Aula ${r.nombre} guardada.`; this.mostrarForm = false; this.cargar(); },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  eliminar(a: Aula): void {
    if (!a.id || !confirm(`¿Eliminar el aula ${a.nombre}?`)) return;
    this.limpiar();
    this.api.eliminar(a.id).subscribe({
      next: () => { this.ok = `Aula ${a.nombre} eliminada.`; this.cargar(); },
      error: (er) => (this.error = mensajeError(er)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
