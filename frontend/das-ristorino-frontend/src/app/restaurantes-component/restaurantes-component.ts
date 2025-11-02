import { Component } from '@angular/core';
import { restaurantsList} from '../data/restaurantes.mock';
@Component({
  selector: 'app-restaurantes-component',
  imports: [],
  templateUrl: './restaurantes-component.html',
  styleUrl: './restaurantes-component.scss',
})
export class RestaurantesComponent {
  restaurantsList = restaurantsList;

}
