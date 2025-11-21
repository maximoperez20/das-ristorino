import { Injectable } from '@angular/core';
import { Resource, ResourceAction, ResourceHandler, ResourceParams, ResourceRequestMethod } from '@ngx-resource/core';
import type { IResourceMethodObservable } from '@ngx-resource/core';
import { ILoginRequest } from '../models/i-login-request';
import { IRegisterRequest } from '../models/i-register-request';
import { IAuthResponse } from '../models/i-auth-response';
import { environment } from '../../../../environments/environment';

@Injectable()
@ResourceParams({
  pathPrefix: `${environment.apiUrl}/clientes`
})
export class ClienteResource extends Resource {

  constructor(handler: ResourceHandler) {
    super(handler);
  }

  @ResourceAction({
    path: '/login',
    method: ResourceRequestMethod.Post,
  })
  declare login: IResourceMethodObservable<ILoginRequest, IAuthResponse>;

  @ResourceAction({
    path: '/register',
    method: ResourceRequestMethod.Post,
  })
  declare register: IResourceMethodObservable<IRegisterRequest, IAuthResponse>;

}
