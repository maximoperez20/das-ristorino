import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRestaurante } from '../../api/models/i-restaurante';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-detalle-restaurante',
  imports: [NgClass],
  templateUrl: './detalle-restaurante.html',
  styleUrls: ['./detalle-restaurante.scss'],
})
export class DetalleRestauranteComponent implements OnInit {

  restaurante?: IRestaurante | undefined;

  constructor(private _route: ActivatedRoute) { }

  ngOnInit(): void {
    // Obtener el restaurante resuelto por el RestauranteResolver
    this._route.data.subscribe(data => {
      this.restaurante = data['restaurante'];
      console.log('Restaurante resuelto:', this.restaurante);
    });
  }

}
