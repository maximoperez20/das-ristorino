import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import type { IPromocion } from '../../api/models/i-promocion';
import { CommonModule, DatePipe } from '@angular/common';
import { PromocionResource } from '../../api/resources/promocion-resource';
import { Router } from '@angular/router';  // ✅ import normal


@Component({
  selector: 'app-promocion',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './promocion.html',
  styleUrls: ['./promocion.scss'],
})
export class PromocionComponent {
  @Input() promocion?: IPromocion;
  @Output() verPromocion = new EventEmitter<IPromocion>();

  private _promocionResource = inject(PromocionResource);
  private _router = inject(Router);  // ✅ así se obtiene la instancia


  onVerPromocion() {
    if (this.promocion) {
      this.verPromocion.emit(this.promocion);
    }
  }

      registrarClickPromocion() {
    this._promocionResource.registrarClick({
        nroRestaurante: this.promocion?.nroRestaurante.toString() || '',
        nroIdioma: this.promocion?.nroIdioma.toString() || '',
        nroContenido: this.promocion?.nroContenido.toString() || ''
      })
      .subscribe({
        next: () =>{
          console.log('Click registrado correctamente')
          this._router.navigate(['/restaurantes', this.promocion?.nroRestaurante]);

        },
        error: (err) => {console.error('Error registrando click', err)
        },
       
      }); 
    }

}
