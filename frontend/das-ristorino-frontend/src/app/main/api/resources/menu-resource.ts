import { Injectable } from '@angular/core';
import {Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { IMenu } from '../models/i-menu';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/menus` // 👈 base de la API
})
export class MenuResource extends Resource{
    constructor(handler: ResourceHandler) {
      super(handler);
    }

    @ResourceAction({
      path: '/{nroRestaurante}/sucursales/{nroSucursal}',
      method: ResourceRequestMethod.Get,
    })
    declare obtenerMenu: IResourceMethodObservable<{nroRestaurante: string, nroSucursal: string}, IMenu>;
    
}