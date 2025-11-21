import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { RestauranteResource } from '../api/resources/restaurante-resource';
import { IRestaurante } from '../api/models/i-restaurante';


export const restauranteResolver : ResolveFn<IRestaurante> = (route, state) => {
  const nroRestaurante = route.paramMap.get('nroRestaurante') || '';
  // El resource espera { id: string }, pero usamos nroRestaurante como id
  return inject(RestauranteResource).obtenerRestaurantePorId({ id: nroRestaurante });
}
