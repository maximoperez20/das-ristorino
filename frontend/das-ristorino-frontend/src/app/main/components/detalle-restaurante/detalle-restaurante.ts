import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRestaurante } from '../../api/models/i-restaurante';
import { NgClass } from '@angular/common';
import { RestauranteResource } from '../../api/resources/restaurante-resource';

@Component({
  selector: 'app-detalle-restaurante',
  imports: [NgClass],
  templateUrl: './detalle-restaurante.html',
  styleUrls: ['./detalle-restaurante.scss'],
})
export class DetalleRestauranteComponent implements OnInit {

restaurante?: IRestaurante;
  private _restauranteResource = inject(RestauranteResource);
  private _route = inject(ActivatedRoute);

  ngOnInit(): void {
    // 🔹 Tomamos el parámetro directamente de la URL
    const id = this._route.snapshot.paramMap.get('nroRestaurante');
    this.cargarRestaurante();
  }

  cargarRestaurante(): void {
    if (!this.restaurante?.id) return;

    this._restauranteResource.obtenerRestaurantePorId({ id: this.restaurante.id }).subscribe({
      next: (data) => {
        this.restaurante = data;
        console.log('Restaurante actualizado:', data);
      },
      error: (err) => console.error('Error al cargar restaurante', err),
    });
  }
}

