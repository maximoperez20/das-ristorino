import { IHorariosDisponiblesResponse } from './i-horario-disponible';

/**
 * Respuesta de error que incluye horarios disponibles alternativos agrupados por zona.
 * Usado cuando una reserva falla por falta de disponibilidad.
 * Mantiene la misma estructura que la respuesta normal de horarios disponibles.
 */
export interface IErrorWithHorarios {
  error: string;
  horarios: IHorariosDisponiblesResponse;
}

