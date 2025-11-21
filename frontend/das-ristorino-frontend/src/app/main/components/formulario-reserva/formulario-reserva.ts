import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservaResource } from '../../api/resources/reserva-resource';
import { AuthService } from '../../../core/services/auth-service';
import { Router } from '@angular/router';
import { AppMessageService } from '../../../core/services/app-message-service';
import { DateUtilsService } from '../../../core/services/date-utils.service';
import type { HorarioSeleccionado } from '../horarios-disponibles/horarios-disponibles';

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
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() reservaConfirmada = new EventEmitter<void>();

  cantAdultos: number = 1;
  cantMenores: number = 0;
  loading = false;
  error: string | null = null;

  private _reservaResource = inject(ReservaResource);
  private _auth = inject(AuthService);
  private _router = inject(Router);
  private _messageService = inject(AppMessageService);
  private _cdr = inject(ChangeDetectorRef);
  private _dateUtils = inject(DateUtilsService);

  ngOnInit(): void {
    console.log('=== FormularioReservaComponent ngOnInit ===');
    console.log('visible:', this.visible);
    console.log('horarioSeleccionado:', this.horarioSeleccionado);
    console.log('nroRestaurante:', this.nroRestaurante);
    console.log('nroSucursal:', this.nroSucursal);
    // La verificación de autenticación ahora se hace en el componente padre
    // antes de abrir el modal, así que aquí solo validamos si el modal está visible
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log('=== FormularioReservaComponent ngOnChanges ===');
    console.log('visible:', this.visible);
    if (changes['visible']) {
      console.log('Cambio en visible:', changes['visible'].previousValue, '->', changes['visible'].currentValue);
      // Forzar detección de cambios
      this._cdr.detectChanges();
      if (changes['visible'].currentValue && !changes['visible'].firstChange) {
        console.log('Visible cambió a true, el modal debería mostrarse');
      }
    }
    if (changes['horarioSeleccionado']) {
      console.log('Cambio en horarioSeleccionado:', changes['horarioSeleccionado'].currentValue);
    }
  }

  cerrar(): void {
    console.log('Cerrando formulario de reserva');
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
      this.error = 'Debe haber al menos 1 adulto';
      return;
    }

    if (this.cantMenores < 0) {
      this.error = 'La cantidad de menores no puede ser negativa';
      return;
    }

    if (this.cantMenores > 0 && !this.horarioSeleccionado.permiteMenores) {
      this.error = 'La zona seleccionada no permite menores';
      return;
    }

    const totalPersonas = this.cantAdultos + this.cantMenores;
    if (totalPersonas > this.horarioSeleccionado.disponibilidad) {
      this.error = `No hay suficiente disponibilidad. Disponible: ${this.horarioSeleccionado.disponibilidad}, Solicitado: ${totalPersonas}`;
      return;
    }

    this.loading = true;
    this.error = null;

    const fechaFormateada = this.formatearFecha(this.horarioSeleccionado.fecha);
    const horaDesde = this.horarioSeleccionado.horaDesde;

    // codZona es el código interno de Ristorino (no el cod_zona_restaurante externo del SOAP)
    // El backend ya mapea el cod_zona_restaurante al cod_zona interno antes de devolverlo al frontend
    this._reservaResource.confirmarReserva({
      nroRestaurante: this.nroRestaurante,
      nroSucursal: this.nroSucursal,
      codZona: this.horarioSeleccionado.codZona, // Código interno de Ristorino
      fechaReserva: fechaFormateada,
      horaDesde: horaDesde,
      cantAdultos: this.cantAdultos,
      cantMenores: this.cantMenores
    }).subscribe({
      next: (response) => {
        this.loading = false;
        this._messageService.showSuccess(
          `Reserva confirmada exitosamente. Código: ${response.codigoReserva}`
        );
        this.reservaConfirmada.emit();
        this.cerrar();
        // Redirigir a mis reservas después de un breve delay
        setTimeout(() => {
          this._router.navigate(['/mis-reservas']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.error || err.error?.message || 'Error al confirmar la reserva';
        console.error('Error confirmando reserva:', err);
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
}

