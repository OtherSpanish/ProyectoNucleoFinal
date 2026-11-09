import { Component, OnInit, inject, signal } from '@angular/core';

import { ConfirmacionService } from '../../../../core/services/confirmacion.service';
import { NotificacionService } from '../../../../core/services/notificacion.service';
import { AulaModel } from '../../../../models/aula.model';
import { CalendarioSemanalModel } from '../../../../models/calendario-semanal.model';
import { MovimientoSesionModel } from '../../../../models/movimiento-sesion.model';
import { ResultadoGeneracionModel } from '../../../../models/resultado-generacion.model';
import { ResultadoSincronizacionModel } from '../../../../models/resultado-sincronizacion.model';
import { SemestreModel } from '../../../../models/semestre.model';
import { SesionHorarioModel } from '../../../../models/sesion-horario.model';
import { AulaService } from '../../../../services/aula.service';
import { EdicionHorarioService } from '../../../../services/edicion-horario.service';
import { HorarioService } from '../../../../services/horario.service';
import { SemestreService } from '../../../../services/semestre.service';

/**
 * Tablero del comité: genera el horario, lo muestra como calendario y permite
 * reacomodar sesiones con drag & drop. Cada movimiento lo valida el backend.
 */
@Component({
  selector: 'app-horario',
  standalone: false,
  templateUrl: './horario.component.html',
  styleUrl: './horario.component.css',
})
export class HorarioComponent implements OnInit {
  private readonly horarioService = inject(HorarioService);
  private readonly edicionHorarioService = inject(EdicionHorarioService);
  private readonly semestreService = inject(SemestreService);
  private readonly aulaService = inject(AulaService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly confirmacionService = inject(ConfirmacionService);

  protected readonly calendario = signal<CalendarioSemanalModel | null>(null);
  protected readonly semestres = signal<SemestreModel[]>([]);
  protected readonly aulas = signal<AulaModel[]>([]);
  protected readonly cargando = signal(true);
  protected readonly procesando = signal(false);
  protected readonly movimiento = signal<MovimientoSesionModel | null>(null);
  protected readonly resultadoGeneracion = signal<ResultadoGeneracionModel | null>(null);
  protected readonly resultadoSincronizacion = signal<ResultadoSincronizacionModel | null>(null);
  protected semestre: number | null = 1;
  protected prefijo = '';

  ngOnInit(): void {
    this.semestreService.listar().subscribe({ next: (lista) => this.semestres.set(lista) });
    this.aulaService.listar().subscribe({ next: (lista) => this.aulas.set(lista) });
    this.cargarCalendario();
  }

  cargarCalendario(): void {
    this.cargando.set(true);
    this.horarioService.calendario(this.semestre, this.prefijo).subscribe({
      next: (datos) => {
        this.calendario.set(datos);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  cambiarSemestre(): void {
    this.prefijo = '';
    this.cargarCalendario();
  }

  buscarPrefijo(): void {
    this.semestre = null;
    this.cargarCalendario();
  }

  async generar(): Promise<void> {
    const confirmado = await this.confirmacionService.preguntar(
      'Generar horario',
      'El motor (grafo de conflictos + DSatur) armará un horario nuevo y reemplazará el actual, incluidos los ajustes hechos a mano.',
      'Generar y reemplazar',
      true,
    );
    if (!confirmado) {
      return;
    }
    this.procesando.set(true);
    this.horarioService.generar().subscribe({
      next: (resultado) => {
        this.procesando.set(false);
        this.resultadoGeneracion.set(resultado);
        this.notificacionService.exito('Horario generado y guardado.');
        this.cargarCalendario();
      },
      error: () => this.procesando.set(false),
    });
  }

  sincronizar(): void {
    this.procesando.set(true);
    this.horarioService.sincronizar().subscribe({
      next: (resultado) => {
        this.procesando.set(false);
        this.resultadoSincronizacion.set(resultado);
        this.cargarCalendario();
      },
      error: () => this.procesando.set(false),
    });
  }

  alSoltarEnCalendario(movimiento: MovimientoSesionModel): void {
    this.movimiento.set(movimiento);
  }

  alConfirmarMovimiento(): void {
    this.movimiento.set(null);
    this.cargarCalendario();
  }

  alRetirarSesion(sesion: SesionHorarioModel): void {
    this.edicionHorarioService.desubicar(sesion.id).subscribe({
      next: () => {
        this.notificacionService.info(`${sesion.cursoCodigo} G${sesion.numeroGrupo} retirada del horario.`);
        this.cargarCalendario();
      },
    });
  }
}
