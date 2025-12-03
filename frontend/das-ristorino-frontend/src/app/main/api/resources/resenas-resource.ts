import { Injectable } from '@angular/core';
import { Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { environment } from '../../../../environments/environment';
import { IResena } from '../models/i-resenas';
import { IConfirmarResenaRequest } from '../models/i-confirmar-resena-request';



@Injectable({ providedIn: 'root' })
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/resenas` // 👈 base de la API
})
export class ResenasResource extends Resource{

  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
      path: '/{nroRestaurante}/resenas-sucursales/{nroSucursal}',
      method: ResourceRequestMethod.Get,
    })
    declare obtenerResenasPorSucursal: IResourceMethodObservable<{nroRestaurante: string, nroSucursal: string}, IResena[]>;

    @ResourceAction({
        path: '/insertar-resena-sucursal',
        method: ResourceRequestMethod.Post,
    })
    declare agregarResenaASucursal: IResourceMethodObservable<IConfirmarResenaRequest, IResena>;
     

}
