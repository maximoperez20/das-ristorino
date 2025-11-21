/**
 * Interfaz para representar una reserva
 */
export interface IReserva {
  id: string;
  nombre_cliente: string;
  email: string;
  telefono: string;
  fecha_hora: string; // ISO 8601 format
  cantidad_personas: number;
  estado: string;
  observaciones: string | null;
  fecha_creacion: string | null;
  fecha_actualizacion: string | null;
  nombre_restaurante?: string | null;
  nombre_sucursal?: string | null;
  nombre_zona?: string | null;
}
