export interface IConfirmarReservaRequest {
  nroRestaurante: string;
  nroSucursal: string;
  codZona: string;
  fechaReserva: string;
  horaDesde: string; 
  cantAdultos: number;
  cantMenores: number;
  preferenciasReserva: number[];
  observacionesReserva: string | null;
}

