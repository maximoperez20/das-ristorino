import { Injectable } from '@angular/core';
import { Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { environment } from '../../../../environments/environment';
import { IMotivoCancelacion } from '../models/i-motivo-cancelacion';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/motivos-cancelacion`
})
export class MotivoCancelacionResource extends Resource {
  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
    method: ResourceRequestMethod.Get,
  })
  declare obtenerMotivos: IResourceMethodObservable<void, IMotivoCancelacion[]>;
}
