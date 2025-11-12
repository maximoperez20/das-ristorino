import { Injectable } from '@angular/core';
import {Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { IPromocion } from '../models/i-promocion';
import { environment } from '../../../../environments/environment';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/promociones` // 👈 base de la API
})
export class PromocionResource extends Resource{

  constructor(handler: ResourceHandler) {
    super(handler);
  }

  //GET /api/promociones
  @ResourceAction({
    method: ResourceRequestMethod.Get,
  })
  declare obtenerPromociones: IResourceMethodObservable<void, IPromocion[]>;

  //POST /api/promociones/click
  @ResourceAction({
    path: '/click',
    method: ResourceRequestMethod.Post,
  })
  declare registrarClick: IResourceMethodObservable<
  {nroRestaurante: string; nroIdioma: string; nroContenido: string}, void>;

}
