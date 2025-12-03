import { Injectable } from '@angular/core';
import {Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { IResena } from '../models/i-resena';
import { environment } from '../../../../environments/environment';
import { IResenaInsertar } from '../models/i-resena-insertar';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/resenas`
})
export class ResenaResource extends Resource{
  
  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
    method: ResourceRequestMethod.Get,
    path: '/{!nroRestaurante}/{!nroSucursal}'
  })
  declare obtenerResenas: IResourceMethodObservable<{ nroRestaurante: string; nroSucursal: string }, IResena[]>;

  @ResourceAction({
    method: ResourceRequestMethod.Post,
  })
  declare insertarResena: IResourceMethodObservable<IResenaInsertar, void>;

}
