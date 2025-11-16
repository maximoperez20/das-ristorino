import { Component, Input, OnInit, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestauranteResource } from '../../api/resources/restaurante-resource';
import { IHorariosDisponiblesResponse } from '../../api/models/i-horario-disponible';

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

  fechaActual: Date = new Date();
  horariosData?: IHorariosDisponiblesResponse;
  loading = false;
  error: string | null = null;

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
}
