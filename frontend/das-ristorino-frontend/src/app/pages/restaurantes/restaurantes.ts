import { Component, inject, OnInit } from '@angular/core';
import { IRestaurante } from '../../api/models/i-restaurante';
import { RestauranteResource } from '../../api/resources/restaurante-resource';

@Component({
  selector: 'app-restaurantes',
  imports: [],
  templateUrl: './restaurantes.html',
  styleUrls: ['./restaurantes.scss'],
})
export class RestaurantesPage implements OnInit {

  restaurantesLista: IRestaurante[] = [];

  private _restauranteResource = inject(RestauranteResource);

  ngOnInit(): void {
    this.cargarRestaurantes();
  }

  cargarRestaurantes() {
    this._restauranteResource.obtenerRestaurantes().subscribe({
      next: (data) => {
        this.restaurantesLista = data;
      },
    });
  }
  
}
