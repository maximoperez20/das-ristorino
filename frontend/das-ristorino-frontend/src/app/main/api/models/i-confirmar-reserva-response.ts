export interface IConfirmarReservaResponse {
  codigoReserva: string;
  nroRestaurante: string;
  nroSucursal: string;
  codZona: string;
  fechaReserva: string; // ISO date format
  horaDesde: string; // Time format
  cantAdultos: number;
  cantMenores: number;
  costoReserva: number;
  mensaje: string;
  urlMapa: string | null;
}

