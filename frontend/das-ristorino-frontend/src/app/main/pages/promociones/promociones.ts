import { Component, inject, OnInit } from '@angular/core';
import { CommonModule} from '@angular/common';
import { IPromocion } from '../../api/models/i-promocion';
import { PromocionComponent } from '../../components/promocion/promocion';
import { PromocionResource } from '../../api/resources/promocion-resource';
import { ActivatedRoute } from '@angular/router';  // ✅ import normal
import { BannerHomeComponent } from '../../components/banner-home/banner-home'; 


@Component({
  selector: 'app-promociones',
  imports: [CommonModule, PromocionComponent, BannerHomeComponent],
  templateUrl: './promociones.html',
  styleUrls: ['./promociones.scss'],
})
export class PromocionesPage implements OnInit {
  
    promocionesLista: IPromocion[] = [];
    agrupadas: IPromocion[][] = [];

  // Inyectar el servicio de promociones (sintaxis moderna)
  private _promocionResource = inject(PromocionResource);
  private _route = inject(ActivatedRoute);

    ngOnInit(): void {
      // Preferir los datos resueltos por la ruta; si no vienen, cargar fallback
      const data = this._route.snapshot.data as { promociones?: IPromocion[] };
      if (data && Array.isArray(data.promociones) && data.promociones.length > 0) {
        this.promocionesLista = data.promociones;
        console.log('Promociones cargadas desde el resolver de la ruta:', this.promocionesLista);
        this.agrupadas = [];
        for (let i = 0; i < this.promocionesLista.length; i += 3) {
          this.agrupadas.push(this.promocionesLista.slice(i, i + 3));
        }
      }
    }

    cargarPromociones() {
      this._promocionResource.obtenerPromociones().subscribe({
        next: (data) => {
          this.promocionesLista = data;
          this.agrupadas = [];
          for (let i = 0; i < data.length; i += 3) {
            this.agrupadas.push(data.slice(i, i + 3));
          }
        },
      });
  }

  getAriaLabelSlide(index: number): string {
    return $localize`Slide ${index}`;
  }
  
}
