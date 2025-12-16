import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaResource } from '../../api/resources/reserva-resource';
import { AuthService } from '../../../core/services/auth-service';
import { Router } from '@angular/router';
import { AppMessageService } from '../../../core/services/app-message-service';
import { DateUtilsService } from '../../../core/services/date-utils.service';
import type { HorarioSeleccionado } from '../horarios-disponibles/horarios-disponibles';
import type { IDominioPreferencia } from '../../api/models/i-dominio-preferencia';
import type { IHorariosDisponiblesResponse } from '../../api/models/i-horario-disponible';
@Component({
  selector: 'app-formulario-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formulario-reserva.html',
  styleUrls: ['./formulario-reserva.scss'],
})
export class FormularioReservaComponent implements OnInit, OnChanges {
  @Input() horarioSeleccionado!: HorarioSeleccionado;
  @Input() nroRestaurante!: string;
  @Input() nroSucursal!: string;
  @Input() especialidadesAlimentarias: IDominioPreferencia[] = [];
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() reservaConfirmada = new EventEmitter<void>();
  @Output() actualizarHorariosDisponibles = new EventEmitter<IHorariosDisponiblesResponse>();
  
  cantAdultos: number = 1;
  cantMenores: number = 0;
  loading = false;
  error: string | null = null;
  especialidadesAlimentariasSeleccionadas: number[] = [];
  observaciones: string | null = null;

  private _reservaResource = inject(ReservaResource);
  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _messageService = inject(AppMessageService);
  private _cdr = inject(ChangeDetectorRef);
  private _dateUtils = inject(DateUtilsService);

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']) {
      // Forzar detección de cambios
      this._cdr.detectChanges();
    }
  }

  cerrar(): void {
    this.visible = false;
    this.visibleChange.emit(false);
    this.error = null;
  }

  confirmarReserva(): void {
    if (!this._auth.isAuthenticated()) {
      this._router.navigate(['/login'], { 
        queryParams: { returnUrl: this._router.url } 
      });
      return;
    }

    if (this.cantAdultos < 1) {
      this.error = $localize`Debe haber al menos 1 adulto`;
      return;
    }

    if (this.cantMenores < 0) {
      this.error = $localize`La cantidad de menores no puede ser negativa`;
      return;
    }

    if (this.cantMenores > 0 && !this.horarioSeleccionado.permiteMenores) {
      this.error = $localize`La zona seleccionada no permite menores`;
      return;
    }

    const totalPersonas = this.cantAdultos + this.cantMenores;
    if (totalPersonas > this.horarioSeleccionado.disponibilidad) {
      this.error = $localize`No hay suficiente disponibilidad. Disponible: ${this.horarioSeleccionado.disponibilidad}, Solicitado: ${totalPersonas}`;
      return;
    }

    this.loading = true;
    this.error = null;

    const fechaFormateada = this.formatearFecha(this.horarioSeleccionado.fecha);
    const horaDesde = this.horarioSeleccionado.horaDesde;

    this._reservaResource.confirmarReserva({
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.nroSucursal,
      codZona: this.horarioSeleccionado.codZona,
      fechaReserva: fechaFormateada,
      horaDesde: horaDesde,
      cantAdultos: this.cantAdultos,
      cantMenores: this.cantMenores,
      observaciones: this.observaciones,
      preferenciasReserva: Array.isArray(this.especialidadesAlimentariasSeleccionadas) 
        ? this.especialidadesAlimentariasSeleccionadas 
        : []
    }).subscribe({
      next: (response) => {
        this.loading = false;
        const mensaje = $localize`Reserva confirmada exitosamente. Código:` + ' ' + response.codigoReserva;
        this._messageService.showSuccess(mensaje);
        this.reservaConfirmada.emit();
        this.cerrar();
        setTimeout(() => {
          this._router.navigate(['/mis-reservas']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        
        const errorResponse = err.body || err.error || err;
        const horarios = errorResponse?.horarios;
        const tieneHorarios = horarios && (
          (horarios.zonas && Array.isArray(horarios.zonas) && horarios.zonas.length > 0) ||
          Array.isArray(horarios)
        );
        
        if (tieneHorarios) {
          const mensajeError = errorResponse.error || errorResponse.message || $localize`El horario seleccionado ya no está disponible. Por favor, seleccione otro horario.`;
          
          this.actualizarHorariosDisponibles.emit(horarios);
          this.cerrar();
          
          setTimeout(() => {
            this._messageService.showMessage({
              text: mensajeError,
              title: $localize`Horario no disponible`,
              type: 'warning'
            });
          }, 100);
        } else {
          this.error = errorResponse?.error || errorResponse?.message || $localize`Error al confirmar la reserva`;
        }
      }
    });
  }

  formatearFecha(fecha: Date): string {
    return this._dateUtils.formatearFechaISO(fecha);
  }

  formatearFechaLegible(fecha: Date): string {
    return this._dateUtils.formatearFechaLegible(fecha);
  }

  get totalPersonas(): number {
    return this.cantAdultos + this.cantMenores;
  }

  get maxPersonas(): number {
    return this.horarioSeleccionado?.disponibilidad || 0;
  }

  getCerrarAriaLabel(): string {
    return $localize`Cerrar`;
  }

  obtenerTextoZona(): string {
    if (!this.horarioSeleccionado?.nomZona) return '';
    return $localize`Zona:` + ' ' + this.horarioSeleccionado.nomZona;
  }

  obtenerTextoFecha(): string {
    if (!this.horarioSeleccionado?.fecha) return '';
    const fechaFormateada = this.formatearFechaLegible(this.horarioSeleccionado.fecha);
    return $localize`Fecha:` + ' ' + fechaFormateada;
  }

  obtenerTextoHorario(): string {
    if (!this.horarioSeleccionado?.horaDesde || !this.horarioSeleccionado?.horaHasta) return '';
    const horaDesde = this.formatearHora(this.horarioSeleccionado.horaDesde);
    const horaHasta = this.formatearHora(this.horarioSeleccionado.horaHasta);
    return $localize`Horario:` + ' ' + horaDesde + ' - ' + horaHasta;
  }

  obtenerTextoDisponibilidad(): string {
    if (!this.horarioSeleccionado?.disponibilidad) return '';
    const disponibilidad = this.horarioSeleccionado.disponibilidad;
    if (disponibilidad === 1) {
      return $localize`Disponibilidad:` + ' ' + disponibilidad + ' ' + $localize`lugar`;
    }
    return $localize`Disponibilidad:` + ' ' + disponibilidad + ' ' + $localize`lugares`;
  }

  obtenerTextoTotalPersonas(): string {
    const total = this.totalPersonas;
    const max = this.maxPersonas;
    if (max === 1) {
      return $localize`Total de personas:` + ' ' + total + ' / ' + max + ' ' + $localize`disponible`;
    }
    return $localize`Total de personas:` + ' ' + total + ' / ' + max + ' ' + $localize`disponibles`;
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

  onCambioEspecialidadAlimentaria(event: Event, especialidad: number): void {
    if (!Array.isArray(this.especialidadesAlimentariasSeleccionadas)) {
      this.especialidadesAlimentariasSeleccionadas = [];
    }
    
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.especialidadesAlimentariasSeleccionadas.push(especialidad);
    } else {
      this.especialidadesAlimentariasSeleccionadas = this.especialidadesAlimentariasSeleccionadas.filter(e => e !== especialidad);
    }
  }
}

