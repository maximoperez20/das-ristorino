import { Component, Input, OnInit, OnChanges, SimpleChanges, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestauranteResource } from '../../api/resources/restaurante-resource';
import { IHorariosDisponiblesResponse, IZona, IHorario } from '../../api/models/i-horario-disponible';
import { DateUtilsService } from '../../../core/services/date-utils.service';

export interface HorarioSeleccionado {
  codZona: string;
  nomZona: string;
  horaDesde: string;
  horaHasta: string;
  disponibilidad: number;
  permiteMenores: boolean;
  fecha: Date;
}

@Component({
  selector: 'horarios-disponibles',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './horarios-disponibles.html',
  styleUrls: ['./horarios-disponibles.scss'],
})
export class HorariosDisponiblesComponent implements OnInit, OnChanges {
  @Input() nroRestaurante!: string;
  @Input() nroSucursal!: string;
  @Input() nombreSucursal!: string;
  @Input() fechaSeleccionada!: Date;
  @Output() horarioSeleccionado = new EventEmitter<HorarioSeleccionado>();

  fechaActual: Date = new Date();
  horariosData?: IHorariosDisponiblesResponse;
  loading = false;
  error: string | null = null;
  horarioSeleccionadoActual: { zona: IZona; horario: IHorario } | null = null;

  private _restauranteResource = inject(RestauranteResource);
  private _dateUtils = inject(DateUtilsService);

  actualizarHorarios(horarios: IHorariosDisponiblesResponse): void {
    this.horariosData = horarios;
    this.loading = false;
    this.error = null;
    this.limpiarSeleccion();
  }

  ngOnInit(): void {
    if (this.fechaSeleccionada) {
      this.fechaActual = new Date(this.fechaSeleccionada);
    } else {
      this.fechaActual = new Date();
    }
    
    if (this.nroRestaurante && this.nroSucursal && this.fechaActual) {
      this.cargarHorarios();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['fechaSeleccionada'] && this.fechaSeleccionada) {
      this.fechaActual = new Date(this.fechaSeleccionada);
    }
    
    const hasAllInputs = this.nroRestaurante && this.nroSucursal && this.fechaActual;
    const shouldReload = changes['nroSucursal'] || changes['fechaSeleccionada'] || changes['nroRestaurante'];
    
    if (hasAllInputs && shouldReload) {
      this.cargarHorarios();
    }
  }

  cargarHorarios(): void {
    if (!this.nroRestaurante || !this.nroSucursal || !this.fechaActual) {
      return;
    }

    this.loading = true;
    this.error = null;

    const fechaFormateada = this.formatearFecha(this.fechaActual);

    this._restauranteResource.obtenerHorariosDisponibles({
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.nroSucursal,
      fecha: fechaFormateada
    }).subscribe({
      next: (data) => {
        if (Array.isArray(data)) {
          this.horariosData = {
            fecha: fechaFormateada,
            totalZonas: 0,
            zonas: []
          };
        } else {
          if (!data.zonas) {
            data.zonas = [];
          }
          this.horariosData = data;
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = $localize`Error al cargar horarios disponibles`;
        this.loading = false;
      }
    });
  }

  formatearFecha(fecha: Date): string {
    return this._dateUtils.formatearFechaISO(fecha);
  }

  formatearFechaLegible(fecha: Date): string {
    return this._dateUtils.formatearFechaLegible(fecha);
  }

  cambiarFecha(dias: number): void {
    const nuevaFecha = new Date(this.fechaActual);
    nuevaFecha.setDate(nuevaFecha.getDate() + dias);
    this.fechaActual = nuevaFecha;
    this.cargarHorarios();
  }

  puedeAvanzarFecha(): boolean {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    const fechaMax = new Date(hoy);
    fechaMax.setDate(fechaMax.getDate() + 30);
    return this.fechaActual < fechaMax;
  }

  puedeRetrocederFecha(): boolean {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    return this.fechaActual > hoy;
  }

  onHorarioClick(zona: IZona, horario: IHorario, event: Event): void {
    event.stopPropagation();
    this.seleccionarHorario(zona, horario);
  }

  seleccionarHorario(zona: IZona, horario: IHorario): void {
    if (horario.disponibilidad <= 0) {
      return;
    }

    this.horarioSeleccionadoActual = { zona, horario };
    
    const horarioSeleccionado: HorarioSeleccionado = {
      codZona: zona.codZona,
      nomZona: zona.nomZona,
      horaDesde: horario.horaDesde,
      horaHasta: horario.horaHasta,
      disponibilidad: horario.disponibilidad,
      permiteMenores: zona.permiteMenores,
      fecha: new Date(this.fechaActual)
    };

    this.horarioSeleccionado.emit(horarioSeleccionado);
  }

  limpiarSeleccion(): void {
    this.horarioSeleccionadoActual = null;
  }

  formatearHora(hora: string | null | undefined): string {
    if (!hora) return '';
    try {
      const horaFormateada = hora.split(':').slice(0, 2).join(':');
      return horaFormateada;
    } catch {
      return hora;
    }
  }

  obtenerTextoDisponibilidad(disponibilidad: number | null | undefined): string {
    if (disponibilidad === null || disponibilidad === undefined) return '';
    if (disponibilidad === 1) {
      return $localize`:@@horarios.disponible.1:1 disponible`;
    }
    return `${disponibilidad} ${$localize`:@@horarios.disponibles:disponibles`}`;
  }

  obtenerTextoReservados(yaReservados: number | null | undefined): string {
    if (yaReservados === null || yaReservados === undefined || yaReservados === 0) return '';
    if (yaReservados === 1) {
      return `(${$localize`1 reservado`})`;
    }
    return `(${yaReservados} ${$localize`reservados`})`;
  }
}
