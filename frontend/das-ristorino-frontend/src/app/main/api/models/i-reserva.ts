/**
 * Interfaz para representar una reserva
 */
export interface IReserva {
  id: string;
  nombre_cliente: string;
  email: string;
  telefono: string;
  fecha_hora: string; // ISO 8601 format
  cant_adultos?: number | null;
  cant_menores?: number | null;
  estado: string;
  observaciones: string | null;
  fecha_creacion: string | null;
  fecha_actualizacion: string | null;
  nombre_restaurante?: string | null;
  nombre_sucursal?: string | null;
  nombre_zona?: string | null;
  preferencias?: string | null;
  nro_restaurante?: string | null;
  nro_sucursal?: string | null;
}
