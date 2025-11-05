import { Component } from '@angular/core';
import { restaurantesLista} from '../../api/models/i-restaurante';
@Component({
  selector: 'app-restaurantes-component',
  imports: [],
  templateUrl: './restaurantes-component.html',
  styleUrl: './restaurantes-component.scss',
})
export class RestaurantesComponent {
  restaurantsList = restaurantesLista;

}
