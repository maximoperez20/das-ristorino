import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PreferenciaResource } from '../api/resources/preferencia-resource';
import { ICategoriaConDominios } from '../api/models/i-categoria-con-dominios';

export const preferenciasResolver: ResolveFn<ICategoriaConDominios[] | undefined> = (_route, _state) => {
  const preferenciaResource = inject(PreferenciaResource);

  return preferenciaResource.obtenerCategorias().pipe(
    catchError(err => {
      console.error('Error al resolver preferencias:', err);
      return of([]); // Retornar un array vacío en caso de error
    })
  );
};

