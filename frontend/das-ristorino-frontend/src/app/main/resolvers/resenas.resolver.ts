import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ResenasResource } from '../api/resources/resenas-resource';
import { IResena } from '../api/models/i-resenas';

export const resenasResolver: ResolveFn<IResena[] | undefined> = (_route, _state) => {
  const nroRestaurante = _route.paramMap.get('nroRestaurante') || '';   
  const nroSucursal = _route.paramMap.get('nroSucursal') || '';
  const resenasResource = inject(ResenasResource);

  return resenasResource.obtenerResenasPorSucursal({ nroRestaurante: nroRestaurante, nroSucursal: nroSucursal }).pipe(
    catchError(err => {
      console.error('Error al resolver reseñas:', err);
      return of(undefined);
    })
  );
}