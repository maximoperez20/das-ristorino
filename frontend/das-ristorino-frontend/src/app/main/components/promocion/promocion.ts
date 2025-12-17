import { Component, Input, Output, EventEmitter, inject, ChangeDetectionStrategy } from '@angular/core';
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
  changeDetection: ChangeDetectionStrategy.OnPush,
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
        next: () => {
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

  get textoValidoHasta(): string {
    const fechaFormateada = this.formatearFecha(this.promocion?.fechaFin);
    if (!fechaFormateada) return '';
    return $localize`Válido hasta:` + ' ' + fechaFormateada;
  }

    obtenerClaseBadgeProposito(proposito: string | null | undefined): string {

    if (!proposito) return 'bg-secondary';

    const colores = ['bg-success', 'bg-primary', 'bg-warning text-dark', 'bg-danger'];

    return colores[Math.floor(Math.random() * 5)];
    
  }

}
