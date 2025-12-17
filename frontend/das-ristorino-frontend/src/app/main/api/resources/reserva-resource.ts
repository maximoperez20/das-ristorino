import { Injectable } from '@angular/core';
import { Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { IReserva } from '../models/i-reserva';
import { IConfirmarReservaRequest } from '../models/i-confirmar-reserva-request';
import { IConfirmarReservaResponse } from '../models/i-confirmar-reserva-response';
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

  @ResourceAction({
    path: '/confirmar',
    method: ResourceRequestMethod.Post,
  })
  declare confirmarReserva: IResourceMethodObservable<IConfirmarReservaRequest, IConfirmarReservaResponse>;

  @ResourceAction({
    path: '/cancelar/{!nroReserva}',
    method: ResourceRequestMethod.Put,
  })
  declare cancelarReserva: IResourceMethodObservable<{ nroReserva: string, motivoCancelacion: string }, void>;
}
