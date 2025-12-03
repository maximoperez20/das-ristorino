import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { IResena } from '../api/models/i-resena';
import { ResenaResource } from '../api/resources/resena-resource';



export const resenasResolver : ResolveFn<IResena[]> = (route, state) => {
  const nroRestaurante = route.paramMap.get('nroRestaurante') || '';
  const nroSucursal = route.paramMap.get('nroSucursal') || '';
  // El resource espera { id: string }, pero usamos nroRestaurante como id
  return inject(ResenaResource).obtenerResenas({ nroRestaurante, nroSucursal });
}
