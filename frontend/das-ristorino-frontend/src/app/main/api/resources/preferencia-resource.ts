import { Injectable } from '@angular/core';
import { Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { ICategoriaConDominios } from '../models/i-categoria-con-dominios';
import { IGuardarPreferencias } from '../models/i-guardar-preferencias';
import { IGuardarPreferenciasResponse } from '../models/i-guardar-preferencias-response';
import { IPreferenciaCliente } from '../models/i-preferencia-cliente';
import { environment } from '../../../../environments/environment';
import { IDominioPreferencia } from '../models/i-dominio-preferencia';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/preferencias`
})
export class PreferenciaResource extends Resource {

  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
    path: '/categorias',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerCategorias: IResourceMethodObservable<void, ICategoriaConDominios[]>;

  @ResourceAction({
    path: '/guardar',
    method: ResourceRequestMethod.Post,
  })
  declare guardarPreferencias: IResourceMethodObservable<IGuardarPreferencias, IGuardarPreferenciasResponse>;

  @ResourceAction({
    path: '/mis-preferencias',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerMisPreferencias: IResourceMethodObservable<void, IPreferenciaCliente[]>;

  @ResourceAction({
    path: '/{nroRestaurante}/especialidades-alimentarias',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerEspecialidadesAlimentariasPorRestaurante: IResourceMethodObservable<{nroRestaurante: string}, IDominioPreferencia[]>;
}
