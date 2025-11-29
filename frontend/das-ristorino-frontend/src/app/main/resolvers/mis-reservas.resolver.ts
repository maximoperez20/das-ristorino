import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { ReservaResource } from '../api/resources/reserva-resource';
import { IReserva } from '../api/models/i-reserva';
import { catchError, of } from 'rxjs';

export const misReservasResolver: ResolveFn<IReserva[]> = (_route, _state) => {
  return inject(ReservaResource).obtenerMisReservas().pipe(
    catchError(err => {
      console.error('Error al resolver mis reservas:', err);
      // Si hay un error, retornar array vacío para que la página se cargue sin reservas  
      return of([]);
    })
  );
};
