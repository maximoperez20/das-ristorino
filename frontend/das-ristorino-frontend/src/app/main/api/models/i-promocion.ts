export interface IPromocion {
  nroRestaurante: string;
  nroIdioma: string;
  nroContenido: string;
  titulo: string;
  descripcion: string;
  descuentoPorcentaje: number | null;
  descuentoFijo: number | null;
  fechaInicio: string;        // formato ISO "YYYY-MM-DDTHH:mm:ss"
  fechaFin: string;           // formato ISO "YYYY-MM-DDTHH:mm:ss"
  estado: string;
  imagenUrl: string | null;
  minPersonas: number;
  maxPersonas: number;
  codigoPromocion: string;
  requiereCodigo: boolean;
  propositoCorto: string | null;
}