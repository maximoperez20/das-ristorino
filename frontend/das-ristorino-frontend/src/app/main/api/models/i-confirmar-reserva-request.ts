export interface IConfirmarReservaRequest {
  nroRestaurante: string;
  nroSucursal: string;
  codZona: string;
  fechaReserva: string; // ISO date format: YYYY-MM-DD
  horaDesde: string; // Time format: HH:mm
  cantAdultos: number;
  cantMenores: number;
  observaciones: string | null;
  preferenciasReserva: number[];
}

