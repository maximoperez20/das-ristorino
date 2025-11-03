import { Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Restaurante, restaurantesLista } from '../data/restaurantes.mock';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-detalle-restaurante-component',
  imports: [NgClass],
  templateUrl: './detalle-restaurante-component.html',
  styleUrl: './detalle-restaurante-component.scss',
})
export class DetalleRestauranteComponent implements OnInit{

  restaurante?: Restaurante;
  restaurantesLista = restaurantesLista;

  restauranteId: string ="";

  constructor(private _route: ActivatedRoute) { }

  ngOnInit(): void {
    this._route.params.subscribe(params => {
      this.restaurante = this.restaurantesLista.find(r => r.id == params['nroRestaurante']);
      console.log(this.restaurante);
    });
  }
    


}
