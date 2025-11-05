import { Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRestaurante, restaurantesLista } from '../../api/models/i-restaurante';
import { NgClass } from '@angular/common';
import { RestauranteService } from '../../services/restaurante-service';

@Component({
  selector: 'app-detalle-restaurante-component',
  imports: [NgClass],
  templateUrl: './detalle-restaurante-component.html',
  styleUrl: './detalle-restaurante-component.scss',
})
export class DetalleRestauranteComponent implements OnInit{

  restaurante?: IRestaurante;
  restaurantesLista = restaurantesLista;

  restauranteId: string ="";

  constructor(private _route: ActivatedRoute, private _restauranteService: RestauranteService) { }

  ngOnInit(): void {

    this._route.params.subscribe(params => {
      this.restaurante = this.restaurantesLista.find(r => r.id == params['nroRestaurante']);
      console.log(this.restaurante);
    });

  }
    


}
