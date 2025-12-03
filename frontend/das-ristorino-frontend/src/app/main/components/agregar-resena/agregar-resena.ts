import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResenasResource } from '../../api/resources/resenas-resource';
import { AuthService } from '../../../core/services/auth-service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppMessageService } from '../../../core/services/app-message-service';
import { DateUtilsService } from '../../../core/services/date-utils.service';
import { IResena } from '../../api/models/i-resenas';

@Component({
  selector: 'app-agregar-resena',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agregar-resena.html',
  styleUrls: ['./agregar-resena.scss'],
})
export class AgregarResenaComponent implements OnInit, OnChanges {
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>()
  @Output() resenaAgregada = new EventEmitter<void>();

  comentario: string = '';
  valoracion: number = 5;
  loading = false;
  error: string | null = null;  

  private _resenasResource = inject(ResenasResource);
  private _auth = inject(AuthService)
  private _router = inject(Router);
  private _route = inject(ActivatedRoute);
  private _messageService = inject(AppMessageService)
  private _cdr = inject(ChangeDetectorRef);
  private idReserva: string | null = null;

  ngOnInit(): void {
    // Si se carga el componente por la ruta directa, mostrar el modal automáticamente
    try {
      // Abrir modal automáticamente si se llega por la ruta dedicada
      const url = this._router && (this._router as any).url;
      if (url && url.indexOf('/mis-reservas/nueva-resena') !== -1) {
        this.visible = true;
      }

      // Leer idReserva desde query params
      const qp = this._route.snapshot.queryParamMap;
      this.idReserva = qp.get('idReserva');
    } catch (e) {
      // noop
    }
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
    this.comentario = '';
    this.valoracion = 5;

    // Si estamos en la ruta dedicada a nueva reseña, regresar a la lista de reservas
    try {
      const url = this._router && (this._router as any).url;
      if (url && url.indexOf('/mis-reservas/nueva-resena') !== -1) {
        this._router.navigate(['/mis-reservas']);
      }
    } catch (e) {
      // noop
    }
  }

  agregarResena(): void {
    if (!this._auth.isAuthenticated()) {
      this._router.navigate(['/login'], { queryParams: { returnUrl: this._router.url } });
      return;
    }

    this.loading = true;
    this.error = null;

    if(this.comentario.trim().length === 0){
      this.error = 'El comentario no puede estar vacío.';
      this.loading = false;
      return;
    }

    if(this.valoracion < 1 || this.valoracion > 5){
      this.error = 'La valoración debe estar entre 1 y 5.';
      this.loading = false;
      return; 
    }
    const idReserva = this.idReserva;
    if (!idReserva) {
      this.error = 'No se encontró la reserva para asociar la reseña.';
      this.loading = false;
      return;
    }
    this._resenasResource.agregarResenaASucursal({ 
      idReserva,
      comentario: this.comentario,
      valoracion: this.valoracion
     }).subscribe({
      next: () => {
        this.loading = false;  
        const mensaje = $localize`Reseña agregada con éxito.`;
        this._messageService.showSuccess(mensaje);        
        this.resenaAgregada.emit();
        this.cerrar();
        setTimeout(() => {
          this._router.navigate(['/mis-reservas']);
        }, 1000);
      },
      error: (err) => {        
        this.loading = false;
        const errorResponse = err.body || err.error|| err;
        this.error = errorResponse.message || 'Error al agregar la reseña.';        
      }
    });
  }

  getCerrarAriaLabel(): string {
    return $localize`Cerrar`;
  }
}