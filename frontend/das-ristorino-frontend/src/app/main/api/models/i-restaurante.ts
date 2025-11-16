export interface IRestaurante {
  nroRestaurante: string;
  nombre: string;
  direccion: string;
  telefono: string | null;
  email: string | null;
  capacidad: number;
  horarioApertura: string;
  horarioCierre: string;
  descripcion: string | null;
  categoria: string | null;
  calificacion: number;
  activo: boolean;
  imagenUrl: string | null;
  diasAtencion: string | null;
}
