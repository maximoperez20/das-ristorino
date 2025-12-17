import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// import type { IReserva } from "../../api/models/i-reserva";
import { ReservaResource } from '../../api/resources/reserva-resource';
import { AppMessageService } from '../../../core/services/app-message-service';

@Component({
  selector: 'app-cancelar-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cancelar-reserva.html',
  styleUrls: ['./cancelar-reserva.scss'],
})

export class CancelarReservaComponent implements OnInit, OnChanges {
  @Input() nroReserva!: string;
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() reservaCancelada = new EventEmitter<void>();

  razonCancelacion: string = '';
  loading = false;
  error: string | null = null;

  private _reservaResource = inject(ReservaResource);
  private _cdr = inject(ChangeDetectorRef);
  private _messageService = inject(AppMessageService);

  ngOnInit(): void {
    // Inicialización si es necesaria
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      // Resetear formulario cuando se abre el modal
      this.razonCancelacion = '';
      this.error = null;
      this.loading = false;
      this._cdr.detectChanges();
    }
  }

  cerrar(): void {
    this.visible = false;
    this.visibleChange.emit(false);
  }
  
  confirmarCancelacionReserva(): void {
    if (!this.razonCancelacion.trim()) {
      this.error = 'La razón de cancelación es obligatoria';
      return;
    }

    this.loading = true;
    this.error = null;
    
    this._reservaResource.cancelarReserva({ 
      nroReserva: this.nroReserva, 
      razonCancelacion: this.razonCancelacion.trim() 
    }).subscribe({
      next: (response: boolean) => {
        this.loading = false;
        if (response) {
          this._messageService.showSuccess('Reserva cancelada exitosamente');
          this.reservaCancelada.emit();
          this.cerrar();
        } else {
          this.error = 'Error al cancelar la reserva. Por favor, intente nuevamente.';
          this._messageService.showError('Error al cancelar reserva');
        }
      },
      error: () => {
        this.loading = false;
        this.error = 'Error al cancelar la reserva. Por favor, intente nuevamente.';
        this._messageService.showError('Error al cancelar reserva');
      }
    });
  }
}