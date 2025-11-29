import { Injectable } from '@angular/core';
import {Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { IRestaurante } from '../models/i-restaurante';
import { IHorariosDisponiblesResponse } from '../models/i-horario-disponible';
import { IBusquedaNLPRequest } from '../models/i-busqueda-nlp-request';
import { IBusquedaNLPResultado } from '../models/i-busqueda-nlp-resultado';
import { environment } from '../../../../environments/environment';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/restaurantes`
})
export class RestauranteResource extends Resource{
  
  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
    method: ResourceRequestMethod.Get,
  })
  declare obtenerRestaurantes: IResourceMethodObservable<void, IRestaurante[]>;

  @ResourceAction({
    path: '/{!id}',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerRestaurantePorId: IResourceMethodObservable<{ id: string }, IRestaurante>;

  @ResourceAction({
    path: '/{!nroRestaurante}/sucursales/{!nroSucursal}/horarios-disponibles',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerHorariosDisponibles: IResourceMethodObservable<{ 
    nroRestaurante: string; 
    nroSucursal: string; 
    fecha: string 
  }, IHorariosDisponiblesResponse>;

  @ResourceAction({
    path: '/buscar-nlp',
    method: ResourceRequestMethod.Post,
  })
  declare buscarRestaurantesPorNLP: IResourceMethodObservable<IBusquedaNLPRequest, IBusquedaNLPResultado>;

}
