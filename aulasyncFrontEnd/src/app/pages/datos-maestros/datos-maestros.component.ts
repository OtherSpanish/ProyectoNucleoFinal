import { Component, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/data.service';
import {
  DEDICACIONES, TIPOS_AULA, TipoAula, TipoDedicacion, etiquetaDedicacion, etiquetaTipoAula,
} from '../../core/models';

type Pestana = 'cursos' | 'secciones' | 'docentes' | 'aulas' | 'grupos';

/** CU01 – Gestión de datos maestros (RF1): cursos, secciones, docentes, aulas y grupos. */
@Component({
  selector: 'app-datos-maestros',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './datos-maestros.component.html',
})
export class DatosMaestrosComponent {
  data = inject(DataService);

  pestanas: { id: Pestana; etiqueta: string }[] = [
    { id: 'cursos', etiqueta: 'Cursos' },
    { id: 'secciones', etiqueta: 'Secciones' },
    { id: 'docentes', etiqueta: 'Docentes' },
    { id: 'aulas', etiqueta: 'Aulas' },
    { id: 'grupos', etiqueta: 'Grupos' },
  ];
  activa: Pestana = 'cursos';
  error = '';

  tiposAula = TIPOS_AULA;
  dedicaciones = DEDICACIONES;
  etqAula = etiquetaTipoAula;
  etqDedicacion = etiquetaDedicacion;

  // Formularios
  curso = { nombre: '', sesionesSemanales: 2, duracionHoras: 2, tipoAula: 'CONVENCIONAL' as TipoAula };
  seccion = { cursoId: '', docenteId: '', grupoId: '' };
  docente = { nombre: '', dedicacion: 'TIEMPO_COMPLETO' as TipoDedicacion };
  aula = { nombre: '', capacidad: 40, tipo: 'CONVENCIONAL' as TipoAula };
  grupo = { nombre: '', semestre: 1, estudiantes: 30 };

  cambiar(p: Pestana): void {
    this.activa = p;
    this.error = '';
  }

  agregarCurso(): void {
    const max = this.data.config().sesionesMaxSemana;
    if (!this.curso.nombre.trim()) return this.fallo('El nombre del curso es obligatorio.');
    if (this.curso.sesionesSemanales < 1 || this.curso.sesionesSemanales > max) {
      return this.fallo(`Las sesiones semanales deben estar entre 1 y ${max}.`);
    }
    this.data.cursos.update((l) => [...l, { id: this.data.nuevoId('C'), ...this.curso, nombre: this.curso.nombre.trim() }]);
    this.curso.nombre = '';
    this.error = '';
  }

  agregarSeccion(): void {
    const s = this.seccion;
    if (!s.cursoId || !s.docenteId || !s.grupoId) return this.fallo('Selecciona curso, docente y grupo.');
    const numero = this.data.secciones().filter((x) => x.cursoId === s.cursoId).length + 1;
    this.data.secciones.update((l) => [...l, { id: this.data.nuevoId('SC'), numero, ...s }]);
    this.error = '';
  }

  agregarDocente(): void {
    if (!this.docente.nombre.trim()) return this.fallo('El nombre del docente es obligatorio.');
    this.data.docentes.update((l) => [
      ...l,
      { id: this.data.nuevoId('D'), nombre: this.docente.nombre.trim(), dedicacion: this.docente.dedicacion, disponibilidad: [] },
    ]);
    this.docente.nombre = '';
    this.error = '';
  }

  agregarAula(): void {
    if (!this.aula.nombre.trim() || this.aula.capacidad < 1) return this.fallo('Indica nombre y capacidad válidos.');
    this.data.aulas.update((l) => [...l, { id: this.data.nuevoId('A'), ...this.aula, nombre: this.aula.nombre.trim() }]);
    this.aula.nombre = '';
    this.error = '';
  }

  agregarGrupo(): void {
    const c = this.data.config();
    const tope = Math.floor(c.limiteGrupo * (1 + c.toleranciaPorcentaje / 100));
    if (!this.grupo.nombre.trim()) return this.fallo('El nombre del grupo es obligatorio.');
    if (this.grupo.estudiantes < 1 || this.grupo.estudiantes > tope) {
      return this.fallo(`El grupo no puede superar ${tope} estudiantes (límite ${c.limiteGrupo} + ${c.toleranciaPorcentaje}%).`);
    }
    this.data.grupos.update((l) => [...l, { id: this.data.nuevoId('G'), ...this.grupo, nombre: this.grupo.nombre.trim() }]);
    this.grupo.nombre = '';
    this.error = '';
  }

  eliminarCurso(id: string): void { this.data.cursos.update((l) => l.filter((x) => x.id !== id)); }
  eliminarSeccion(id: string): void { this.data.secciones.update((l) => l.filter((x) => x.id !== id)); }
  eliminarDocente(id: string): void { this.data.docentes.update((l) => l.filter((x) => x.id !== id)); }
  eliminarAula(id: string): void { this.data.aulas.update((l) => l.filter((x) => x.id !== id)); }
  eliminarGrupo(id: string): void { this.data.grupos.update((l) => l.filter((x) => x.id !== id)); }

  private fallo(msg: string): void {
    this.error = msg;
  }
}
