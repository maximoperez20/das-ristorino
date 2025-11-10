import { Component, inject, OnInit } from '@angular/core';
import { CommonModule} from '@angular/common';
import { IPromocion, promocionesLista } from '../../api/models/i-promocion';
import { PromocionComponent } from '../../components/promocion/promocion';
import { PromocionResource } from '../../api/resources/promocion-resource';
import { Router } from '@angular/router';  // ✅ import normal

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
    private _router = inject(Router);  // ✅ así se obtiene la instancia

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

    cargarPromocionesFijas() {
      this.promocionesLista = promocionesLista;
      this.agrupadas = [];
      for (let i = 0; i < this.promocionesLista.length; i += 3) {
        this.agrupadas.push(this.promocionesLista.slice(i, i + 3));
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
  
    registrarClickPromocion(promocion: IPromocion) {
    this._promocionResource.registrarClick({
        nroRestaurante: promocion.nroRestaurante.toString(),
        nroIdioma: promocion.nroIdioma.toString(),
        nroContenido: promocion.nroContenido.toString()
      })
      .subscribe({
        next: () =>{
          console.log('Click registrado correctamente')
          this._router.navigate(['/restaurantes', promocion.nroRestaurante]);

        },
        error: (err) => {console.error('Error registrando click', err)
        },
       
      }); 
    }


}
