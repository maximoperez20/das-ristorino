import { Injectable } from '@angular/core';
import { Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { IResena } from '../models/i-resena';
import { environment } from '../../../../environments/environment';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/resenas`
})
export class ResenaResource extends Resource {

  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
    path: '/{nroRestaurante}/{nroSucursal}',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerResenasPorRestaurante: IResourceMethodObservable<{nroRestaurante: string, nroSucursal: string}, IResena[]>;

//   @ResourceAction({
//     path: '/{nroRestaurante}/especialidades-alimentarias',
//     method: ResourceRequestMethod.Get,
//   })
//   declare obtenerEspecialidadesAlimentariasPorRestaurante: IResourceMethodObservable<{nroRestaurante: string}, IDominioPreferencia[]>;
}
