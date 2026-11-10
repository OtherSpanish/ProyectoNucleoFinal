import { Component, OnInit, computed, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocentesApi, EspecialidadesApi, UsuariosApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import {
  DEDICACIONES, Dedicacion, DisponibilidadDocente, Docente, Especialidad, Franja, Usuario, etiquetaDedicacion,
} from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';
import { EditorFranjasComponent } from '../../shared/editor-franjas/editor-franjas.component';

/**
 * Docentes (/api/docentes) y su disponibilidad (/api/docentes/{id}/disponibilidad).
 * ADMIN crea, edita, elimina y define la disponibilidad; COMITE solo consulta.
 */
@Component({
  selector: 'app-docentes',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, AlertaComponent, EditorFranjasComponent],
  templateUrl: './docentes.component.html',
})
export class DocentesComponent implements OnInit {
  private api = inject(DocentesApi);
  private especialidadesApi = inject(EspecialidadesApi);
  private usuariosApi = inject(UsuariosApi);
  private auth = inject(AuthService);

  puedeEditar = computed(() => this.auth.rol() === 'ADMIN');

  docentes: Docente[] = [];
  especialidades: Especialidad[] = [];
  usuariosLibres: Usuario[] = [];
  dedicaciones = DEDICACIONES;
  etiquetaDedicacion = etiquetaDedicacion;
  filtroEspecialidad: number | null = null;

  // formulario
  mostrarForm = false;
  editandoId: number | null = null;
  usuarioId: number | null = null;
  usuarioNombre = '';
  dedicacion: Dedicacion = 'TIEMPO_COMPLETO';
  seleccion = new Set<number>();

  // disponibilidad
  dispDocente: Docente | null = null;
  disp: DisponibilidadDocente | null = null;
  franjasEdit: Franja[] = [];
  dispSucia = false;

  error = '';
  ok = '';

  ngOnInit(): void {
    this.cargarDocentes();
    this.especialidadesApi.listar().subscribe({
      next: (e) => (this.especialidades = e),
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  cargarDocentes(): void {
    this.api.listar(this.filtroEspecialidad).subscribe({
      next: (d) => (this.docentes = d),
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  nombresEspecialidades(d: Docente): string {
    return d.especialidades.map((e) => e.nombre).join(', ');
  }

  // ---------------------------------------------------------------- Formulario
  nuevo(): void {
    this.limpiar();
    this.editandoId = null;
    this.usuarioId = null;
    this.usuarioNombre = '';
    this.dedicacion = 'TIEMPO_COMPLETO';
    this.seleccion = new Set();
    this.mostrarForm = true;
    // Usuarios con rol DOCENTE que aún no están vinculados a un docente.
    this.usuariosApi.listar('DOCENTE').subscribe({
      next: (us) => {
        const vinculados = new Set(this.docentes.map((d) => d.usuario.id));
        this.usuariosLibres = us.filter((u) => !vinculados.has(u.id));
      },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  editar(d: Docente): void {
    this.limpiar();
    this.editandoId = d.id ?? null;
    this.usuarioId = d.usuario.id ?? null;
    this.usuarioNombre = `${d.usuario.nombre} (${d.usuario.correo})`;
    this.dedicacion = d.dedicacion;
    this.seleccion = new Set(d.especialidades.map((e) => e.id as number));
    this.mostrarForm = true;
  }

  cancelar(): void { this.mostrarForm = false; this.editandoId = null; }

  alternar(id: number | undefined, marcado: boolean): void {
    if (id === undefined) return;
    if (marcado) this.seleccion.add(id); else this.seleccion.delete(id);
  }

  guardar(): void {
    this.limpiar();
    if (this.usuarioId === null) { this.error = 'Selecciona el usuario del docente.'; return; }
    if (this.seleccion.size === 0) { this.error = 'Selecciona al menos una especialidad.'; return; }
    const cuerpo = {
      usuario: { id: this.usuarioId },
      dedicacion: this.dedicacion,
      especialidades: [...this.seleccion].map((id) => ({ id })),
    } as unknown as Docente;

    const peticion = this.editandoId === null ? this.api.crear(cuerpo) : this.api.actualizar(this.editandoId, cuerpo);
    peticion.subscribe({
      next: (d) => {
        this.ok = `Docente ${d.usuario.nombre} ${this.editandoId === null ? 'creado' : 'actualizado'}.`;
        this.cancelar();
        this.cargarDocentes();
      },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  eliminar(d: Docente): void {
    if (!d.id || !confirm(`¿Eliminar al docente ${d.usuario.nombre}? Su usuario se conserva.`)) return;
    this.limpiar();
    this.api.eliminar(d.id).subscribe({
      next: () => {
        this.ok = `Docente ${d.usuario.nombre} eliminado.`;
        if (this.dispDocente?.id === d.id) this.cerrarDisponibilidad();
        this.cargarDocentes();
      },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  // ---------------------------------------------------------------- Disponibilidad
  verDisponibilidad(d: Docente): void {
    if (!d.id) return;
    this.limpiar();
    this.dispDocente = d;
    this.disp = null;
    this.api.disponibilidad(d.id).subscribe({
      next: (r) => { this.disp = r; this.franjasEdit = r.franjas; this.dispSucia = false; },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  cerrarDisponibilidad(): void { this.dispDocente = null; this.disp = null; }

  alCambiarFranjas(f: Franja[]): void {
    this.franjasEdit = f;
    this.dispSucia = true;
  }

  get horasMarcadas(): number {
    return this.franjasEdit.reduce((t, f) => t + (f.horaFin - f.horaInicio), 0);
  }

  guardarDisponibilidad(): void {
    if (!this.dispDocente?.id) return;
    this.limpiar();
    const franjas = this.franjasEdit.map((f) => ({ dia: f.dia, horaInicio: f.horaInicio, horaFin: f.horaFin }));
    this.api.guardarDisponibilidad(this.dispDocente.id, franjas).subscribe({
      next: (r) => {
        this.disp = r;
        this.franjasEdit = r.franjas;
        this.dispSucia = false;
        this.ok = `Disponibilidad de ${r.nombreDocente} guardada (${r.horasDisponibles} h).`;
      },
      error: (e) => (this.error = mensajeError(e)),
    });
  }

  private limpiar(): void { this.error = ''; this.ok = ''; }
}
