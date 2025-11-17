import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { RestauranteResource } from '../api/resources/restaurante-resource';
import { IRestaurante } from '../api/models/i-restaurante';

export const restauranteListResolver : ResolveFn<IRestaurante[]> = (_route, _state) => {
  return inject(RestauranteResource).obtenerRestaurantes();
}
