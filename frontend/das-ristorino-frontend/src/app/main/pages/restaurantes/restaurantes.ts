import { Component, inject, OnInit } from '@angular/core';
import { IRestaurante } from '../../api/models/i-restaurante';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-restaurantes',
  imports: [],
  templateUrl: './restaurantes.html',
  styleUrls: ['./restaurantes.scss'],
})
export class RestaurantesPage implements OnInit {

  restaurantesLista: IRestaurante[] = [];

  private _route = inject(ActivatedRoute);

  ngOnInit(): void {
    // Leer restaurantes resueltos por el RestaurantesListResolver
    this._route.data.subscribe(data => {
      if (data && data['restaurantes']) {
        this.restaurantesLista = data['restaurantes'];
      } else {
        // Fallback: si no hay datos resueltos, podríamos cargar directamente (opcional)
        this.restaurantesLista = [];
      }
    });
  }
  
}
