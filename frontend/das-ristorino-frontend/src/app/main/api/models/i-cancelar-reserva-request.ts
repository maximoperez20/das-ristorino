/** Request para cancelar una reserva */
export interface ICancelarReservaRequest {
  id: string;
  codMotivoCancelacion: string;
  notas?: string;
}
