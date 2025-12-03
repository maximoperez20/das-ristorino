import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResenaResource } from '../../api/resources/resena-resource';
import { AuthService } from '../../../core/services/auth-service';
import { Router } from '@angular/router';
import { AppMessageService } from '../../../core/services/app-message-service';
import { DateUtilsService } from '../../../core/services/date-utils.service';
import type { IResena } from '../../api/models/i-resena';
import type { IResenaRequest } from '../../api/models/i-resena-request';

@Component({
  selector: 'app-agregar-resena',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agregar-resena.html',
  styleUrls: ['./agregar-resena.scss'],
})
export class FormularioReservaComponent implements OnInit, OnChanges {
  @Input() nroReserva!: string;
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() resenaCreada = new EventEmitter<void>(); // Nuevo output
  
  calificacionSeleccionada: number = 0;
  comentario: string = '';
  loading: boolean = false;
  
  private _resenaResource = inject(ResenaResource);
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
    // Limpiar formulario al cerrar
    this.calificacionSeleccionada = 0;
    this.comentario = '';
  }

  onSubmit(e: Event): void {
    e.preventDefault();
    
    if (!this.calificacionSeleccionada || this.calificacionSeleccionada < 1 || this.calificacionSeleccionada > 5) {
      this._messageService.showError($localize`Por favor selecciona una calificación entre 1 y 5`);
      return;
    }

    if (!this.comentario || this.comentario.trim().length === 0) {
      this._messageService.showError($localize`Por favor ingresa un comentario`);
      return;
    }

    this.loading = true;
    
    const resenaRequest: IResenaRequest = {
      nroReserva: this.nroReserva,
      calificacion: this.calificacionSeleccionada,
      comentario: this.comentario.trim(),
    };

    this._resenaResource.crearResena(resenaRequest).subscribe({
      next: () => {
        this.loading = false;
        this._messageService.showSuccess($localize`Reseña creada exitosamente`);
        this.resenaCreada.emit();
        this.cerrar();
        // Limpiar formulario
        this.calificacionSeleccionada = 0;
        this.comentario = '';
      },
      error: (err) => {
        this.loading = false;
        console.error('Error al crear reseña:', err);
        this._messageService.showError($localize`Error al crear la reseña`);
      }
    });
  }
}

