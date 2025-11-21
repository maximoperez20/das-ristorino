import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { LocalidadResource } from '../api/resources/localidad-resource';
import { ILocalidad } from '../api/models/i-localidad';

export const localidadesResolver: ResolveFn<ILocalidad[]> = (_route, _state) => {
  return inject(LocalidadResource).obtenerLocalidades();
};
