export interface IHorario {
  horaDesde: string;
  horaHasta: string;
  disponibilidad: number;
  yaReservados: number;
}

export interface IZona {
  codZona: string;
  nomZona: string;
  capacidadZona: number;
  permiteMenores: boolean;
  horarios: IHorario[];
}

export interface IHorariosDisponiblesResponse {
  fecha: string;
  totalZonas: number;
  zonas: IZona[];
}
