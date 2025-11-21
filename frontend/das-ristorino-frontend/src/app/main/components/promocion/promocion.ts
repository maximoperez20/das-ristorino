import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import type { IPromocion } from '../../api/models/i-promocion';
import { IClick } from '../../api/models/i-click';
import { CommonModule } from '@angular/common';
import { PromocionResource } from '../../api/resources/promocion-resource';
import { Router } from '@angular/router';
import { DateUtilsService } from '../../../core/services/date-utils.service';

@Component({
  selector: 'app-promocion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './promocion.html',
  styleUrls: ['./promocion.scss'],
})
export class PromocionComponent {
  @Input() promocion?: IPromocion;
  @Output() verPromocion = new EventEmitter<IPromocion>();

  private _promocionResource = inject(PromocionResource);
  private _router = inject(Router);
  private _dateUtils = inject(DateUtilsService);


  registrarClickPromocion() {

    const clickData: IClick = {
      nroRestaurante: this.promocion?.nroRestaurante.toString() ?? '',
      nroIdioma: this.promocion?.nroIdioma.toString() ?? '',
      nroContenido: this.promocion?.nroContenido.toString() ?? ''
      //Agregar nroCliente si es necesario
    };

    this._promocionResource.registrarClick(clickData)
      .subscribe({
        next: () =>{
          console.log('Click registrado correctamente')
          this._router.navigate(['/restaurantes', this.promocion?.nroRestaurante]);
        },
        error: (err) => {console.error('Error registrando click', err)
        },
       
      }); 
    }

  formatearFecha(fecha: string | null | undefined): string {
    if (!fecha) return '';
    return this._dateUtils.formatearFecha(fecha);
  }

}
