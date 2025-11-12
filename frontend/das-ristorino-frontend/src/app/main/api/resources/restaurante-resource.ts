import { Injectable } from '@angular/core';
import {Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { IRestaurante } from '../models/i-restaurante';
import { environment } from '../../../../environments/environment';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/restaurantes` // 👈 base de la API
})
export class RestauranteResource extends Resource{
  
  constructor(handler: ResourceHandler) {
    super(handler);
  }

  //GET /api/restaurantes
  @ResourceAction({
    method: ResourceRequestMethod.Get,
  })
  declare obtenerRestaurantes: IResourceMethodObservable<void, IRestaurante[]>;

  //GET /api/restaurantes/:id
  @ResourceAction({
    path: '/{!id}',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerRestaurantePorId: IResourceMethodObservable<{ id: string }, IRestaurante>;

}
