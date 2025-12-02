import { IReserva } from './i-reserva';

export interface IReservaPorDia {
  fecha: Date;
  fechaKey: string; // YYYY-MM-DD para agrupar
  titulo: string; // "Hoy" o fecha formateada
  reservas: IReserva[];
}