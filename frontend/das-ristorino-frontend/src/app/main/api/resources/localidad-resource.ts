import { Injectable } from '@angular/core';
import { Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { ILocalidad } from '../models/i-localidad';
import { environment } from '../../../../environments/environment';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/localidades`
})
export class LocalidadResource extends Resource {

  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
    path: '',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerLocalidades: IResourceMethodObservable<void, ILocalidad[]>;

}
