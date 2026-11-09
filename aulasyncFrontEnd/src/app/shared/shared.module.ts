import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { CalendarioSemanalComponent } from './components/calendario-semanal/calendario-semanal.component';
import { DialogoConfirmacionComponent } from './components/dialogo-confirmacion/dialogo-confirmacion.component';
import { EditorFranjasComponent } from './components/editor-franjas/editor-franjas.component';
import { EncabezadoPaginaComponent } from './components/encabezado-pagina/encabezado-pagina.component';
import { EstadoVacioComponent } from './components/estado-vacio/estado-vacio.component';
import { IconoComponent } from './components/icono/icono.component';
import { NotificacionesComponent } from './components/notificaciones/notificaciones.component';
import { PanelLateralComponent } from './components/panel-lateral/panel-lateral.component';
import { PiePaginaComponent } from './components/pie-pagina/pie-pagina.component';
import { TarjetaSesionComponent } from './components/tarjeta-sesion/tarjeta-sesion.component';
import { VentanaDetalleComponent } from './components/ventana-detalle/ventana-detalle.component';
import { EtiquetaPipe } from './pipes/etiqueta.pipe';
import { FilaCalendarioPipe } from './pipes/fila-calendario.pipe';
import { HoraPipe } from './pipes/hora.pipe';
import { RangoHorasPipe } from './pipes/rango-horas.pipe';

const DECLARACIONES = [
  CalendarioSemanalComponent,
  DialogoConfirmacionComponent,
  EditorFranjasComponent,
  EncabezadoPaginaComponent,
  EstadoVacioComponent,
  IconoComponent,
  NotificacionesComponent,
  PanelLateralComponent,
  PiePaginaComponent,
  TarjetaSesionComponent,
  VentanaDetalleComponent,
  EtiquetaPipe,
  FilaCalendarioPipe,
  HoraPipe,
  RangoHorasPipe,
];

/** Piezas reutilizables por todos los módulos de funcionalidad. */
@NgModule({
  declarations: DECLARACIONES,
  imports: [CommonModule, FormsModule, RouterModule],
  exports: [CommonModule, FormsModule, RouterModule, ...DECLARACIONES],
})
export class SharedModule {}
