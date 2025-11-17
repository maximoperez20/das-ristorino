import { Injectable } from '@angular/core';
import { Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { IReserva } from '../models/i-reserva';
import { environment } from '../../../../environments/environment';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/reservas`
})
export class ReservaResource extends Resource {

  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
    path: '/mis-reservas',
    method: ResourceRequestMethod.Get,
  })
  declare obtenerMisReservas: IResourceMethodObservable<void, IReserva[]>;

}
