import { ISucursal } from './i-sucursal';

/**
 * Interfaz para representar un restaurante.
 * Los campos opcionales pueden no venir dependiendo del endpoint:
 * - Lista de restaurantes: no incluye sucursales, promociones, tipoCocina
 * - Detalle de restaurante: incluye todos los campos
 */
export interface IRestaurante {
  // Campos básicos (siempre presentes)
  nroRestaurante: string;
  nombre: string;
  direccion: string;
  telefono: string | null;
  email: string | null;
  capacidad: number;
  horarioApertura: string;
  horarioCierre: string;
  calificacion: number;
  activo: boolean;
  diasAtencion: string | null;

  // Campos que pueden venir o no
  descripcion?: string | null;
  categoria?: string | null;
  tipoCocina?: string[]; // Solo en detalle
  imagenes?: string[]; // Array de imágenes (nuevo formato)
  imagenUrl?: string | null; // Mantener para compatibilidad con endpoints antiguos
  sucursales?: ISucursal[]; // Solo en detalle
  promociones?: any[]; // Solo en detalle
}
