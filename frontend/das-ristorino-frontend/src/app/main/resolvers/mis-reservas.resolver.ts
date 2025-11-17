import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { ReservaResource } from '../api/resources/reserva-resource';
import { IReserva } from '../api/models/i-reserva';

export const misReservasResolver: ResolveFn<IReserva[]> = (_route, _state) => {
  return inject(ReservaResource).obtenerMisReservas();
};
