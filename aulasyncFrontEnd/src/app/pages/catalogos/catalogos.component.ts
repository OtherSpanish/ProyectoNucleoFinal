import { Component, computed, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/auth.service';
import { EspecialidadesComponent } from './especialidades.component';
import { SemestresComponent } from './semestres.component';
import { SedesComponent } from './sedes.component';
import { AulasComponent } from './aulas.component';
import { EstudiantesComponent } from './estudiantes.component';

type Pestana = 'especialidades' | 'semestres' | 'sedes' | 'aulas' | 'estudiantes';

/** Catálogos de apoyo. Solo ADMIN puede modificarlos; COMITE los consulta. */
@Component({
  selector: 'app-catalogos',
  standalone: true,
  imports: [NgIf, EspecialidadesComponent, SemestresComponent, SedesComponent, AulasComponent, EstudiantesComponent],
  template: `
    <h2>Catálogos</h2>
    <p class="ayuda">
      Datos base del horario.
      <ng-container *ngIf="!puedeEditar()">Tu rol solo permite consultar.</ng-container>
    </p>

    <div class="pestanas">
      <button type="button" [class.activa]="actual === 'especialidades'" (click)="actual = 'especialidades'">Especialidades</button>
      <button type="button" [class.activa]="actual === 'semestres'" (click)="actual = 'semestres'">Semestres</button>
      <button type="button" [class.activa]="actual === 'sedes'" (click)="actual = 'sedes'">Sedes</button>
      <button type="button" [class.activa]="actual === 'aulas'" (click)="actual = 'aulas'">Aulas</button>
      <button type="button" [class.activa]="actual === 'estudiantes'" (click)="actual = 'estudiantes'">Estudiantes</button>
    </div>

    <div class="pestana-cuerpo">
      <app-especialidades *ngIf="actual === 'especialidades'" [puedeEditar]="puedeEditar()"></app-especialidades>
      <app-semestres *ngIf="actual === 'semestres'" [puedeEditar]="puedeEditar()"></app-semestres>
      <app-sedes *ngIf="actual === 'sedes'" [puedeEditar]="puedeEditar()"></app-sedes>
      <app-aulas *ngIf="actual === 'aulas'" [puedeEditar]="puedeEditar()"></app-aulas>
      <app-estudiantes *ngIf="actual === 'estudiantes'" [puedeEditar]="puedeEditar()"></app-estudiantes>
    </div>
  `,
})
export class CatalogosComponent {
  private auth = inject(AuthService);
  puedeEditar = computed(() => this.auth.rol() === 'ADMIN');
  actual: Pestana = 'especialidades';
}
