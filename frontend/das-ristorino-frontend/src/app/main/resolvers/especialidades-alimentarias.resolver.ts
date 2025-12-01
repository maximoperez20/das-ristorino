import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PreferenciaResource } from '../api/resources/preferencia-resource';
import { IDominioPreferencia } from '../api/models/i-dominio-preferencia';

export const especialidadesAlimentariasResolver: ResolveFn<IDominioPreferencia[] | undefined> = (_route, _state) => {
  const nroRestaurante = _route.paramMap.get('nroRestaurante') || '';
  const preferenciaResource = inject(PreferenciaResource);

  return preferenciaResource.obtenerEspecialidadesAlimentariasPorRestaurante({ nroRestaurante: nroRestaurante }).pipe(
    catchError(err => {
      console.error('Error al resolver preferencias:', err);
      return of([]); // Retornar un array vacío en caso de error
    })
  );
};
