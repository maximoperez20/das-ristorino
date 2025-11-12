import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRestaurante } from '../../api/models/i-restaurante';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-detalle-restaurante',
  standalone: true,
  imports: [NgClass],
  templateUrl: './detalle-restaurante.html',
  styleUrls: ['./detalle-restaurante.scss'],
})
export class DetalleRestauranteComponent implements OnInit {

  restaurante?: IRestaurante | undefined;
  private _route = inject(ActivatedRoute);

  ngOnInit(): void {
    // Leer el restaurante resuelto por el RestauranteResolver (route.data.restaurante)
    this._route.data.subscribe(data => {
      this.restaurante = data?.['restaurante'];
      console.log('Restaurante resuelto en componente:', this.restaurante);
    });
  }

}

