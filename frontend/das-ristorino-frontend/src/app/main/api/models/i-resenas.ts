/**
 * Interfaz para representar una reserva
 */
export interface IResena {
    nroreserva: string;
    nroRestaurante: string;
    nroSucursal: string;
    nroCliente: number;
    comentario: string;
    valoracion: number;  
    nombreCliente: string;
}
