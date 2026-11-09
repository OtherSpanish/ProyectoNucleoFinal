import { Component, Input, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { switchMap } from 'rxjs';
import { SedesApi } from '../../core/api';
import { Franja, Sede, etiquetaDia, formatoHora } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';
import { EditorFranjasComponent } from '../../shared/editor-franjas/editor-franjas.component';

/**
 * Sedes y sus franjas de clase (p. ej. Usaquén 7–13 y 14–22; Chía 8–12 y 13–15).
 * Las franjas se guardan aparte con PUT /api/sedes/{id}/franjas, que reemplaza la lista completa.
 */
@Component({
  selector: 'app-sedes',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent, EditorFranjasComponent],
  template: `
    <app-alerta [error]="error" [ok]="ok"></app-alerta>
    <div class="barra">
      <button type="button" class="primario" *ngIf="puedeEditar" (click)="nueva()">Nueva sede</button>
    </div>

    <form class="card" *ngIf="mostrarForm" (ngSubmit)="guardar()">
      <h3>{{ editandoId === null ? 'Nueva sede' : 'Editar sede' }}</h3>
      <div class="fila-form">
        <label>Nombre <input name="nombre" [(ngModel)]="form.nombre" required></label>
        <label>Días de primer semestre (0–6)
          <input name="dias" type="number" min="0" max="6" [(ngModel)]="form.diasPrimerSemestre" required>
        </label>
        <label class="check">
          <input type="checkbox" name="excl" [(ngModel)]="form.exclusivaPrimerSemestre"> Exclusiva de primer semestre
        </label>
      </div>
      <h3>Franjas de clase de la sede</h3>
      <app-editor-franjas [franjas]="franjasForm" [editable]="true" (cambio)="franjasForm = $event"></app-editor-franjas>
      <button type="submit" class="primario">Guardar</button>
      <button type="button" (click)="mostrarForm = false">Cancelar</button>
    </form>

    <p class="vacio" *ngIf="lista.length === 0">No hay sedes.</p>
    <table *ngIf="lista.length > 0">
      <thead><tr><th>Sede</th><th>1.er semestre</th><th>Franjas</th><th *ngIf="puedeEditar"></th></tr></thead>
      <tbody>
        <tr *ngFor="let s of lista">
          <td>{{ s.nombre }}</td>
          <td>{{ s.exclusivaPrimerSemestre ? 'Exclusiva · ' + s.diasPrimerSemestre + ' días' : 'No exclusiva' }}</td>
          <td>{{ resumen(s) }}</td>
          <td class="acciones" *ngIf="puedeEditar">
            <button type="button" (click)="editar(s)">Editar</button>
            <button type="button" class="peligro" (click)="eliminar(s)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
  `,
})
export class SedesComponent implements OnInit {
  private api = inject(SedesApi);
  @Input() puedeEditar = false;

  lista: Sede[] = [];
  mostrarForm = false;
  editandoId: number | null = null;
  form: Sede = this.vacia();
  franjasForm: Franja[] = [];
  error = '';
  ok = '';

  ngOnInit(): void { this.cargar(); }

  private vacia(): Sede { return { nombre: '', exclusivaPrimerSemestre: false, diasPrimerSemestre: 0, franjas: [] }; }

  cargar(): void {
    this.api.listar().subscribe({ next: (l) => (this.lista = l), error: (e) => (this.error = mensajeError(e)) });
  }

  /** Ej.: «Lun–Vie 07:00–13:00, 14:00–22:00 · Sáb 07:00–13:00» (agrupado por día). */
  resumen(s: Sede): string {
    if (!s.franjas.length) return 'Sin franjas';
    const porDia = new Map<string, string[]>();
    for (const f of s.franjas) {
      const lista = porDia.get(f.dia) ?? [];
      lista.push(`${formatoHora(f.horaInicio)}–${formatoHora(f.horaFin)}`);
      porDia.set(f.dia, lista);
    }
    // Agrupa los días que tienen exactamente las mismas franjas.
    const porTexto = new Map<string, string[]>();
    for (const [dia, tramos] of porDia) {
      const t = tramos.join(', ');
      porTexto.set(t, [...(porTexto.get(t) ?? []), etiquetaDia(dia).slice(0, 3)]);
    }
    return [...porTexto].map(([t, dias]) => `${dias.join(' ')}: ${t}`).join(' · ');
  }

  nueva(): void {
    this.limpiar();
    this.editandoId = null;
    this.form = this.vacia();
    this.franjasForm = [];
    this.mostrarForm = true;
  }

  editar(s: Sede): void {
    this.limpiar();
    this.editandoId = s.id ?? null;
    this.form = { ...s };
    this.franjasForm = s.franjas.map((f) => ({ ...f }));
    this.mostrarForm = true;
  }

  guardar(): void {
    this.limpiar();
    const cuerpo = {
      nombre: this.form.nombre.trim(),
      exclusivaPrimerSemestre: !!this.form.exclusivaPrimerSemestre,
      diasPrimerSemestre: Number(this.form.diasPrimerSemestre),
    };
    const franjas = this.franjasForm.map((f) => ({ dia: f.dia, horaInicio: f.horaInicio, horaFin: f.horaFin }));
    const datos$ = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    datos$.pipe(switchMap((s) => this.api.reemplazarFranjas(s.id as number, franjas))).subscribe({
      next: (s) => { this.ok = `Sede ${s.nombre} guardada.`; this.mostrarForm = false; this.cargar(); },
      error: (e) => { this.error = mensajeError(e); this.cargar(); },
    });
  }

  eliminar(s: Sede): void {
    if (!s.id || !confirm(`¿Eliminar la sede ${s.nombre}?`)) return;
    this.limpiar();
    this.api.eliminar(s.id).subscribe({
      next: () => { this.ok = `Sede ${s.nombre} eliminada.`; this.cargar(); },
      error: (er) => (this.error = mensajeError(er)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
