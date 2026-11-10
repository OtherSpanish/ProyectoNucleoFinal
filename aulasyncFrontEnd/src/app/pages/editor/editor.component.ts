import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { of, switchMap } from 'rxjs';
import { AulasApi, CursosApi, GruposApi, HorarioApi } from '../../core/api';
import { Aula, Curso, Dia, Grupo, SesionHorario, etiquetaDia, etiquetaTipoAula, formatoHora } from '../../core/models';
import { mensajeError } from '../../core/errores';
import { AlertaComponent } from '../../shared/alerta/alerta.component';
import { CalendarioSemanalComponent, CeldaEstado } from '../../shared/calendario-semanal/calendario-semanal.component';
import { PanelSinUbicarComponent } from '../../shared/panel-sin-ubicar/panel-sin-ubicar.component';
import { semestresDe } from '../../shared/horario-util';

/**
 * Editor del horario guardado (solo COMITE).
 * - Al pasar una sesión sobre una celda se llama a POST /horarios/sesiones/{id}/validar y la celda se pinta
 *   de verde (válido) o rojo (inválido).
 * - Al soltarla se valida de nuevo y, si es válida, se aplica con PATCH /horarios/sesiones/{id}.
 * - «Retirar» devuelve la sesión a «sin ubicar» (DELETE /horarios/sesiones/{id}/ubicacion).
 * El back exige siempre un aula destino: se usa la del panel «Aula destino» (por defecto, la actual o una compatible).
 */
@Component({
  selector: 'app-editor',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, RouterLink, AlertaComponent, CalendarioSemanalComponent, PanelSinUbicarComponent],
  templateUrl: './editor.component.html',
})
export class EditorComponent implements OnInit, OnDestroy {
  private api = inject(HorarioApi);
  private aulasApi = inject(AulasApi);
  private cursosApi = inject(CursosApi);
  private gruposApi = inject(GruposApi);

  sesiones: SesionHorario[] = [];
  aulas: Aula[] = [];
  cursos: Curso[] = [];
  grupos: Grupo[] = [];

  semestres: number[] = [];
  filtroSemestre: number | null = null;
  filtroGrupo: number | null = null;

  seleccionId: number | null = null;
  aulaDestino: number | null = null;
  private aulaElegida = new Map<number, number>();

  // validación en vivo
  celdaEstado: CeldaEstado | null = null;
  mensajesHover: string[] = [];
  private cache = new Map<string, { valido: boolean; mensajes: string[] }>();
  private temporizador: ReturnType<typeof setTimeout> | null = null;
  private ultimaConsulta = 0;

  cargando = true;
  error = '';
  ok = '';
  lista: string[] | null = null;

  ngOnInit(): void {
    this.aulasApi.listar().subscribe({ next: (a) => (this.aulas = a), error: (e) => (this.error = mensajeError(e)) });
    this.cursosApi.listar().subscribe({ next: (c) => (this.cursos = c), error: (e) => (this.error = mensajeError(e)) });
    this.gruposApi.listar().subscribe({ next: (g) => (this.grupos = g), error: (e) => (this.error = mensajeError(e)) });
    this.recargar();
  }

  ngOnDestroy(): void { if (this.temporizador) clearTimeout(this.temporizador); }

  recargar(): void {
    this.cargando = true;
    this.api.listar().subscribe({
      next: (s) => {
        this.sesiones = s;
        this.semestres = semestresDe(s);
        if (this.filtroSemestre === null || !this.semestres.includes(this.filtroSemestre)) {
          this.filtroSemestre = this.semestres[0] ?? null;
        }
        this.cache.clear();
        this.refiltrar();
        this.cargando = false;
      },
      error: (e) => { this.error = mensajeError(e); this.cargando = false; },
    });
  }

  // ---------------------------------------------------------------- Filtros
  private pasa(s: SesionHorario): boolean {
    return (this.filtroSemestre === null || s.semestre === this.filtroSemestre)
      && (this.filtroGrupo === null || s.numeroGrupo === this.filtroGrupo);
  }
  /** Listas ya filtradas: se recalculan solo al cambiar datos o filtros (no en cada ciclo de detección). */
  delCalendario: SesionHorario[] = [];
  sinUbicar: SesionHorario[] = [];
  numerosGrupo: number[] = [];

  refiltrar(): void {
    this.delCalendario = this.sesiones.filter((s) => s.ubicada && this.pasa(s));
    this.sinUbicar = this.sesiones.filter((s) => !s.ubicada && this.pasa(s));
    this.numerosGrupo = [...new Set(this.sesiones.map((s) => s.numeroGrupo))].sort((a, b) => a - b);
  }
  get seleccionada(): SesionHorario | undefined { return this.sesiones.find((s) => s.id === this.seleccionId); }

  // ---------------------------------------------------------------- Aula destino
  etiquetaAula(a: Aula): string {
    return `${a.nombre} · ${a.sedeNombre} · ${a.capacidad} pers. · ${etiquetaTipoAula(a.tipo)}${a.disponible ? '' : ' (no disponible)'}`;
  }

  esCompatible(a: Aula, s: SesionHorario): boolean {
    const curso = this.cursos.find((c) => c.id === s.cursoId);
    const grupo = this.grupos.find((g) => g.id === s.grupoId);
    if (!curso) return true;
    return a.disponible && a.tipo === curso.tipoAula && a.capacidad >= (grupo?.cantidadEstudiantes ?? 0);
  }

  /** Aula por defecto: la elegida antes, la actual, la de otra sesión del grupo o la compatible más pequeña. */
  private aulaPorDefecto(s: SesionHorario): number | null {
    const elegida = this.aulaElegida.get(s.id);
    if (elegida) return elegida;
    if (s.aulaId) return s.aulaId;
    const hermana = this.sesiones.find((x) => x.grupoId === s.grupoId && x.ubicada && x.aulaId);
    if (hermana?.aulaId) {
      const a = this.aulas.find((x) => x.id === hermana.aulaId);
      if (a && this.esCompatible(a, s)) return a.id as number;
    }
    const compatibles = this.aulas.filter((a) => this.esCompatible(a, s)).sort((a, b) => a.capacidad - b.capacidad);
    return (compatibles[0]?.id ?? this.aulas[0]?.id ?? null) as number | null;
  }

  seleccionar(id: number): void {
    this.seleccionId = id;
    const s = this.sesiones.find((x) => x.id === id);
    this.aulaDestino = s ? this.aulaPorDefecto(s) : null;
    this.celdaEstado = null;
    this.mensajesHover = [];
  }

  alCambiarAula(id: number | null): void {
    this.aulaDestino = id;
    if (this.seleccionId !== null && id !== null) this.aulaElegida.set(this.seleccionId, id);
    this.cache.clear();
  }

  // ---------------------------------------------------------------- Validación en vivo
  alSobre(e: { sesionId: number; dia: Dia; hora: number }): void {
    const s = this.sesiones.find((x) => x.id === e.sesionId);
    const aulaId = s ? this.aulaParaMover(s) : null;
    if (!s || aulaId === null) return;
    const duracion = s.duracionHoras;
    const clave = `${s.id}|${e.dia}|${e.hora}|${aulaId}`;
    const previa = this.cache.get(clave);
    if (previa) {
      this.celdaEstado = { dia: e.dia, hora: e.hora, duracion, valido: previa.valido };
      this.mensajesHover = previa.mensajes;
      return;
    }
    this.celdaEstado = { dia: e.dia, hora: e.hora, duracion, valido: null };
    this.mensajesHover = [];
    if (this.temporizador) clearTimeout(this.temporizador);
    const consulta = ++this.ultimaConsulta;
    this.temporizador = setTimeout(() => {
      this.api.validar(s.id, { dia: e.dia, horaInicio: e.hora, aulaId }).subscribe({
        next: (v) => {
          this.cache.set(clave, { valido: v.valido, mensajes: v.mensajes });
          if (consulta === this.ultimaConsulta) {
            this.celdaEstado = { dia: e.dia, hora: e.hora, duracion, valido: v.valido };
            this.mensajesHover = v.mensajes;
          }
        },
        error: () => { if (consulta === this.ultimaConsulta) this.celdaEstado = null; },
      });
    }, 150);
  }

  private aulaParaMover(s: SesionHorario): number | null {
    return s.id === this.seleccionId && this.aulaDestino !== null ? this.aulaDestino : this.aulaPorDefecto(s);
  }

  // ---------------------------------------------------------------- Mover
  alSoltar(e: { sesionId: number; dia: Dia; hora: number }): void {
    this.limpiar();
    this.celdaEstado = null;
    const s = this.sesiones.find((x) => x.id === e.sesionId);
    const aulaId = s ? this.aulaParaMover(s) : null;
    if (!s) return;
    if (aulaId === null) { this.error = 'No hay aulas registradas para ubicar la sesión.'; return; }
    const destino = { dia: e.dia, horaInicio: e.hora, aulaId };
    const etiqueta = `${s.cursoCodigo} G${s.numeroGrupo}`;

    this.api.validar(s.id, destino).pipe(
      switchMap((v) => {
        if (!v.valido) {
          this.error = `No se pudo mover ${etiqueta} a ${etiquetaDia(e.dia)} ${formatoHora(e.hora)}:`;
          this.lista = v.mensajes;
          return of(null);
        }
        return this.api.mover(s.id, destino);
      }),
    ).subscribe({
      next: (nueva) => {
        if (!nueva) return;
        this.reemplazar(nueva);
        this.seleccionar(nueva.id);
        this.ok = `${etiqueta} movida a ${etiquetaDia(e.dia)} ${formatoHora(e.hora)} en ${nueva.aulaNombre} (${nueva.sedeNombre}).`;
      },
      error: (er) => (this.error = mensajeError(er)),
    });
  }

  retirar(): void {
    const s = this.seleccionada;
    if (!s) return;
    this.limpiar();
    this.api.desubicar(s.id).subscribe({
      next: (nueva) => {
        this.reemplazar(nueva);
        this.ok = `${s.cursoCodigo} G${s.numeroGrupo} retirada del calendario; quedó sin ubicar.`;
      },
      error: (er) => (this.error = mensajeError(er)),
    });
  }

  private reemplazar(nueva: SesionHorario): void {
    this.sesiones = this.sesiones.map((x) => (x.id === nueva.id ? nueva : x));
    this.cache.clear();
    this.refiltrar();
  }

  private limpiar(): void { this.error = ''; this.ok = ''; this.lista = null; }
}
