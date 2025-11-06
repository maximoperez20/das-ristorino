import { Component, inject, OnInit } from '@angular/core';
import { CommonModule} from '@angular/common';
import { IPromocion } from '../../api/models/i-promocion';
import { PromocionComponent } from '../../components/promocion/promocion';
import { PromocionResource } from '../../api/resources/promocion-resource';

@Component({
  selector: 'app-promociones',
  imports: [CommonModule, PromocionComponent],
  templateUrl: './promociones.html',
  styleUrls: ['./promociones.scss'],
})
export class PromocionesPage implements OnInit {
  
    promocionesLista: IPromocion[] = [];
    agrupadas: IPromocion[][] = [];

    // Inyectar el servicio de promociones (sintaxis moderna)
    private _promocionResource = inject(PromocionResource);

    ngOnInit(): void {
      this.cargarPromociones();
    }

    // cargarPromociones(){
    //   this._promocionService.obtenerPromociones().subscribe({
    //     next: (data) => {
    //       // Aquí puedes manejar los datos recibidos
    //       console.log(data);
    //       this.promocionesLista = data;
    //     },
    //     error: (error) => {
    //       // Aquí puedes manejar errores
    //       console.error('Error al obtener promociones:', error);
    //     }
    //   });
    // }

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
  
    registrarClickPromocion(promocion: IPromocion) {
    this._promocionResource.registrarClick({
        nroRestaurante: promocion.nroRestaurante.toString(),
        nroIdioma: promocion.nroIdioma.toString(),
        nroContenido: promocion.nroContenido.toString()
      })
      .subscribe({
        next: () => console.log('Click registrado correctamente'),
        error: (err) => console.error('Error registrando click', err)
      }); 
    }


}
