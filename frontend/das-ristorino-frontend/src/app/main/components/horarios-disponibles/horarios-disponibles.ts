import { Component, Input, OnInit, OnChanges, SimpleChanges, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestauranteResource } from '../../api/resources/restaurante-resource';
import { IHorariosDisponiblesResponse, IZona, IHorario } from '../../api/models/i-horario-disponible';

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
      console.log('Faltan datos para cargar horarios:', {
        nroRestaurante: this.nroRestaurante,
        nroSucursal: this.nroSucursal,
        fechaActual: this.fechaActual
      });
      return;
    }

    this.loading = true;
    this.error = null;

    const fechaFormateada = this.formatearFecha(this.fechaActual);
    console.log('Cargando horarios con:', {
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.nroSucursal,
      fecha: fechaFormateada
    });

    this._restauranteResource.obtenerHorariosDisponibles({
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.nroSucursal,
      fecha: fechaFormateada
    }).subscribe({
      next: (data) => {
        console.log('Horarios cargados:', data);
        this.horariosData = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar horarios disponibles';
        this.loading = false;
        console.error('Error cargando horarios:', err);
      }
    });
  }

  formatearFecha(fecha: Date): string {
    const year = fecha.getFullYear();
    const month = String(fecha.getMonth() + 1).padStart(2, '0');
    const day = String(fecha.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatearFechaLegible(fecha: Date): string {
    return fecha.toLocaleDateString('es-AR', { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
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
    console.log('=== CLICK EN HORARIO ===');
    console.log('Zona:', zona);
    console.log('Horario:', horario);
    console.log('Disponibilidad:', horario.disponibilidad);
    console.log('Event target:', event.target);
    console.log('Event currentTarget:', event.currentTarget);
    event.stopPropagation();
    this.seleccionarHorario(zona, horario);
  }

  seleccionarHorario(zona: IZona, horario: IHorario): void {
    console.log('=== seleccionarHorario llamado ===');
    if (horario.disponibilidad <= 0) {
      console.log('Horario sin disponibilidad, no se puede seleccionar');
      return; // No permitir seleccionar horarios sin disponibilidad
    }

    console.log('Seleccionando horario:', { zona, horario });
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

    console.log('Emitiendo horarioSeleccionado:', horarioSeleccionado);
    console.log('EventEmitter existe?', !!this.horarioSeleccionado);
    this.horarioSeleccionado.emit(horarioSeleccionado);
    console.log('Evento emitido');
  }

  limpiarSeleccion(): void {
    this.horarioSeleccionadoActual = null;
  }
}
