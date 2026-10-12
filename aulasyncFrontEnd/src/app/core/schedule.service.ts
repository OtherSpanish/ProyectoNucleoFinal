import { Injectable, computed, inject, signal } from '@angular/core';
import { AuthService } from './auth.service';
import { DataService } from './data.service';
import {
  Conflicto, DIAS, Docente, REGLAS_TEXTO, ReglaId, RegistroAuditoria, ResultadoValidacion, Sesion,
} from './models';

/**
 * Lógica del horario en el navegador, SOLO para poder probar la interfaz sin backend.
 * En el diseño real esto vive en el backend (MotorHorarioController, EdicionHorarioController
 * y AuditoriaController); aquí se simula con una heurística voraz y las mismas reglas RD1–RD9.
 */
@Injectable({ providedIn: 'root' })
export class ScheduleService {
  private data = inject(DataService);
  private auth = inject(AuthService);
  private contador = 0;

  readonly sesiones = signal<Sesion[]>([]);
  readonly ubicadas = computed(() => this.sesiones().filter((s) => s.estado === 'UBICADA'));
  readonly sinUbicar = computed(() => this.sesiones().filter((s) => s.estado === 'SIN_UBICAR'));

  /** Id de la sesión que se está arrastrando (para pintar la validación en vivo). */
  readonly arrastrando = signal<string | null>(null);

  // Auditoría: solo lectura hacia afuera, no hay métodos para modificar ni borrar (RNF8).
  private readonly _auditoria = signal<RegistroAuditoria[]>([]);
  readonly auditoria = this._auditoria.asReadonly();

  // ---------- Etiquetas ----------
  etiqueta(s: Sesion): string {
    return `${this.data.nombreCurso(s.cursoId)} – s${s.numero}`;
  }
  descripcionPosicion(s: Sesion): string {
    if (s.estado !== 'UBICADA' || s.dia === undefined || s.hora === undefined) return 'Sin ubicar';
    return `${DIAS[s.dia - 1]} ${s.hora}:00–${s.hora + s.duracion}:00 · ${this.data.nombreAula(s.aulaId)}`;
  }
  buscar(id: string | null): Sesion | undefined {
    return id ? this.sesiones().find((s) => s.id === id) : undefined;
  }

  // ---------- Franja general (RD7) ----------
  limitesDia(dia: number): { inicio: number; fin: number } {
    const c = this.data.config();
    return dia === 6
      ? { inicio: c.horaInicioSabado, fin: c.horaFinSabado }
      : { inicio: c.horaInicioSemana, fin: c.horaFinSemana };
  }
  horaHabilitada(dia: number, hora: number): boolean {
    const c = this.data.config();
    const l = this.limitesDia(dia);
    return hora >= l.inicio && hora < l.fin && !(hora >= c.almuerzoInicio && hora < c.almuerzoFin);
  }

  private docenteDisponible(d: Docente, dia: number, hora: number, fin: number): boolean {
    for (let h = hora; h < fin; h++) {
      const cubierta = d.disponibilidad.some((f) => f.dia === dia && f.horaInicio <= h && f.horaFin >= h + 1);
      if (!cubierta) return false;
    }
    return true;
  }

  // ---------- Validación (RF5): RD1, RD2, RD3, RD4, RD6, RD7, RD9 ----------
  validar(s: Sesion, dia: number, hora: number, aulaId?: string): ResultadoValidacion {
    const conflictos: Conflicto[] = [];
    const advertencias: string[] = [];
    const c = this.data.config();
    const fin = hora + s.duracion;
    const lim = this.limitesDia(dia);

    // RD7
    if (hora < lim.inicio || fin > lim.fin) {
      conflictos.push({ regla: 'RD7', mensaje: `Fuera de la franja general (${DIAS[dia - 1]} ${lim.inicio}:00–${lim.fin}:00).` });
    } else if (hora < c.almuerzoFin && fin > c.almuerzoInicio) {
      conflictos.push({ regla: 'RD7', mensaje: `La sesión cruza la hora de almuerzo (${c.almuerzoInicio}:00–${c.almuerzoFin}:00).` });
    }

    // RD4
    const doc = this.data.docentes().find((d) => d.id === s.docenteId);
    if (!doc || !this.docenteDisponible(doc, dia, hora, fin)) {
      conflictos.push({ regla: 'RD4', mensaje: `${doc?.nombre ?? 'El docente'} no declaró disponibilidad en ese horario.` });
    }

    // RD1, RD2, RD3, RD6 contra las demás sesiones ubicadas
    for (const o of this.ubicadas()) {
      if (o.id === s.id || o.dia !== dia || o.hora === undefined) continue;
      const solapa = hora < o.hora + o.duracion && o.hora < fin;
      if (solapa) {
        if (o.docenteId === s.docenteId) {
          conflictos.push({ regla: 'RD1', mensaje: `${this.data.nombreDocente(s.docenteId)} ya dicta ${this.etiqueta(o)} en esa franja.` });
        }
        if (o.grupoId === s.grupoId) {
          conflictos.push({ regla: 'RD2', mensaje: `${this.data.nombreGrupo(s.grupoId)} ya tiene ${this.etiqueta(o)} en esa franja.` });
        }
        if (aulaId && o.aulaId === aulaId) {
          conflictos.push({ regla: 'RD3', mensaje: `El aula ${this.data.nombreAula(aulaId)} ya está ocupada por ${this.etiqueta(o)}.` });
        }
      }
      if (o.seccionId === s.seccionId) {
        conflictos.push({ regla: 'RD6', mensaje: `${this.data.nombreCurso(s.cursoId)} ya tiene otra sesión el ${DIAS[dia - 1]}.` });
      }
    }

    // RD9
    const aula = this.data.aulas().find((a) => a.id === aulaId);
    const curso = this.data.cursos().find((x) => x.id === s.cursoId);
    const grupo = this.data.grupos().find((g) => g.id === s.grupoId);
    if (!aula) {
      conflictos.push({ regla: 'RD9', mensaje: 'No hay un aula compatible con la capacidad y el tipo requeridos.' });
    } else {
      if (grupo && aula.capacidad < grupo.estudiantes) {
        conflictos.push({ regla: 'RD9', mensaje: `${aula.nombre} (capacidad ${aula.capacidad}) no alcanza para ${grupo.nombre} (${grupo.estudiantes}).` });
      }
      if (curso && aula.tipo !== curso.tipoAula) {
        conflictos.push({ regla: 'RD9', mensaje: `${aula.nombre} no es del tipo requerido por ${curso.nombre}.` });
      }
    }

    // RB1 (preferencia, no bloquea): evitar días consecutivos del mismo curso
    const consecutiva = this.ubicadas().some(
      (o) => o.id !== s.id && o.seccionId === s.seccionId && o.dia !== undefined && Math.abs(o.dia - dia) === 1,
    );
    if (consecutiva) advertencias.push('Preferencia RB1: el curso queda en días consecutivos.');

    return { valido: conflictos.length === 0, conflictos, advertencias };
  }

  /** Evalúa un movimiento sin aplicarlo (sirve para la previsualización al arrastrar). */
  evaluar(sesionId: string, dia: number, hora: number, aulaId?: string): { resultado: ResultadoValidacion; aulaId?: string } {
    const s = this.buscar(sesionId);
    if (!s) {
      return { resultado: { valido: false, conflictos: [{ regla: 'GENERAL', mensaje: 'Sesión no encontrada.' }], advertencias: [] } };
    }
    const aula = aulaId ?? s.aulaId ?? this.elegirAula(s, dia, hora);
    return { resultado: this.validar(s, dia, hora, aula), aulaId: aula };
  }

  // ---------- Movimientos (RF4) ----------
  mover(sesionId: string, dia: number, hora: number, aulaId?: string): ResultadoValidacion {
    const { resultado, aulaId: aula } = this.evaluar(sesionId, dia, hora, aulaId);
    const s = this.buscar(sesionId);
    if (!resultado.valido || !s) return resultado;
    if (s.estado === 'UBICADA' && s.dia === dia && s.hora === hora && s.aulaId === aula) return resultado;

    const antes = this.descripcionPosicion(s);
    this.actualizar(s.id, { dia, hora, aulaId: aula, estado: 'UBICADA', causa: undefined });
    const despues = this.buscar(s.id)!;
    this.registrar(despues, `${antes} → ${this.descripcionPosicion(despues)}`);
    return resultado;
  }

  cambiarAula(sesionId: string, aulaId: string): ResultadoValidacion {
    const s = this.buscar(sesionId);
    if (!s || s.dia === undefined || s.hora === undefined) {
      return { valido: false, conflictos: [{ regla: 'GENERAL', mensaje: 'La sesión no está ubicada.' }], advertencias: [] };
    }
    return this.mover(sesionId, s.dia, s.hora, aulaId);
  }

  retirar(sesionId: string): void {
    const s = this.buscar(sesionId);
    if (!s || s.estado !== 'UBICADA') return;
    const antes = this.descripcionPosicion(s);
    this.actualizar(s.id, { estado: 'SIN_UBICAR', dia: undefined, hora: undefined, aulaId: undefined, causa: 'Retirada manualmente de su franja.' });
    this.registrar(this.buscar(s.id)!, `${antes} → Sin ubicar`);
  }

  // ---------- Motor simulado (RF2) ----------
  generarPropuesta(): void {
    const nuevas: Sesion[] = [];
    for (const sec of this.data.secciones()) {
      const curso = this.data.cursos().find((c) => c.id === sec.cursoId);
      if (!curso) continue;
      for (let n = 1; n <= curso.sesionesSemanales; n++) {
        nuevas.push({
          id: `S${++this.contador}`, seccionId: sec.id, cursoId: sec.cursoId, docenteId: sec.docenteId,
          grupoId: sec.grupoId, numero: n, duracion: curso.duracionHoras, estado: 'SIN_UBICAR',
        });
      }
    }
    // Primero las sesiones más restringidas (más vecinos en el grafo de conflictos).
    const grado = (s: Sesion) =>
      nuevas.filter((o) => o.id !== s.id && (o.docenteId === s.docenteId || o.grupoId === s.grupoId)).length;
    const orden = [...nuevas].sort(
      (a, b) => grado(b) - grado(a) || b.duracion - a.duracion || a.id.localeCompare(b.id, undefined, { numeric: true }),
    );
    this.sesiones.set(nuevas);
    for (const s of orden) this.ubicarAutomatico(s.id);
  }

  private ubicarAutomatico(id: string): void {
    const s = this.buscar(id);
    if (!s) return;
    const compatibles = this.aulasCompatibles(s);
    const conteo: Partial<Record<ReglaId, number>> = {};
    let hubo = false; // alguna franja dentro de la disponibilidad del docente

    for (let dia = 1; dia <= 6; dia++) {
      const lim = this.limitesDia(dia);
      for (let hora = lim.inicio; hora + s.duracion <= lim.fin; hora++) {
        if (!this.horaHabilitada(dia, hora)) continue;
        for (const aula of compatibles) {
          if (this.validar(s, dia, hora, aula.id).valido) {
            this.actualizar(id, { dia, hora, aulaId: aula.id, estado: 'UBICADA', causa: undefined });
            return;
          }
        }
        // Para la causa del bloqueo, solo se cuentan las franjas que el docente sí tiene disponibles.
        // Se toma el aula con menos conflictos en esa franja.
        const intentos = (compatibles.length ? compatibles.map((a) => a.id) : [undefined]).map((a) => this.validar(s, dia, hora, a));
        const r = intentos.sort((x, y) => x.conflictos.length - y.conflictos.length)[0];
        const reglas = r.conflictos.map((c) => c.regla);
        if (!reglas.includes('RD4') && !reglas.includes('RD7')) {
          hubo = true;
          for (const regla of new Set(reglas)) conteo[regla] = (conteo[regla] ?? 0) + 1;
        }
      }
    }

    let causa: string;
    if (compatibles.length === 0) {
      causa = `RD9: ${REGLAS_TEXTO.RD9} (no existe un aula compatible).`;
    } else if (!hubo) {
      causa = `RD4: ${REGLAS_TEXTO.RD4}; no hay una franja libre suficiente.`;
    } else {
      const [regla] = (Object.entries(conteo) as [ReglaId, number][]).sort((a, b) => b[1] - a[1])[0];
      causa = `${regla}: ${REGLAS_TEXTO[regla]}.`;
    }
    this.actualizar(id, { estado: 'SIN_UBICAR', causa });
  }

  private aulasCompatibles(s: Sesion) {
    const curso = this.data.cursos().find((c) => c.id === s.cursoId);
    const grupo = this.data.grupos().find((g) => g.id === s.grupoId);
    return this.data.aulas().filter((a) => a.tipo === curso?.tipoAula && a.capacidad >= (grupo?.estudiantes ?? 0));
  }

  private elegirAula(s: Sesion, dia: number, hora: number): string | undefined {
    const compat = this.aulasCompatibles(s);
    return compat.find((a) => this.validar(s, dia, hora, a.id).valido)?.id ?? compat[0]?.id;
  }

  // ---------- Auditoría (RF8, RF11) ----------
  historialDe(sesionId: string | null): RegistroAuditoria[] {
    return this._auditoria()
      .filter((r) => !sesionId || r.sesionId === sesionId)
      .sort((a, b) => a.fecha.localeCompare(b.fecha)); // cronológico (CA7)
  }

  private registrar(s: Sesion, cambio: string): void {
    const u = this.auth.usuario();
    const reg: RegistroAuditoria = {
      id: `A${this._auditoria().length + 1}`,
      sesionId: s.id,
      sesionEtiqueta: this.etiqueta(s),
      usuario: u ? `${u.nombre} (${u.usuario})` : 'desconocido',
      fecha: new Date().toISOString(),
      cambio,
    };
    this._auditoria.update((l) => [...l, reg]);
  }

  private actualizar(id: string, cambios: Partial<Sesion>): void {
    this.sesiones.update((l) => l.map((s) => (s.id === id ? { ...s, ...cambios } : s)));
  }
}
