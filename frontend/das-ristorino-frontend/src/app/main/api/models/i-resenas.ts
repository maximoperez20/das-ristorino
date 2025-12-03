/**
 * Interfaz para representar una reserva
 */
export interface IResena {
    nroreserva: string;
    nroRestaurante: string;
    nroSucursal: string;
    clienteId: number;
    comentario: string;
    valoracion: number;  
}
