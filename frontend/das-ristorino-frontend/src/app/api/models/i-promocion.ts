export const promocionesLista: IPromocion[] = [
  // {
  //   "nroRestaurante": "16DEE5C3-9355-4F64-9355-FC79BD28DA63",
  //   "nroIdioma": "2219E244-8413-474D-ABFC-8EDDD06ED2EB",
  //   "nroContenido": "047B879B-A8DD-4162-8F91-509505A58AEB",
  //   "titulo": "Vení a Los Aroza SRL, en Los Aroza - Centro. Ubicados en Av. Colón 950, Centro, Córdoba, descubrí sa",
  //   "descripcion": "Vení a Los Aroza SRL, en Los Aroza - Centro. Ubicados en Av. Colón 950, Centro, Córdoba, descubrí sabores para compartir en un espacio moderno y cercano. ¡Te esperamos!",
  //   "descuentoPorcentaje": null,
  //   "descuentoFijo": null,
  //   "fechaInicio": "2025-10-31T00:00:00",
  //   "fechaFin": "2025-11-30T00:00:00",
  //   "estado": "ACTIVA",
  //   "imagenUrl": null,
  //   "minPersonas": 0,
  //   "maxPersonas": 0,
  //   "codigoPromocion": "AI_DFB4584B-5D78-4BD2-9E93-8764B87264BB",
  //   "requiereCodigo": false
  // },
  // {
  //   "nroRestaurante": "16DEE5C3-9355-4F64-9355-FC79BD28DA63",
  //   "nroIdioma": "2219E244-8413-474D-ABFC-8EDDD06ED2EB",
  //   "nroContenido": "1B966BAD-97F7-486B-B9F6-4E05A5330BFE",
  //   "titulo": "Descubrí el sabor de Los Aroza SRL en nuestra sucursal Los Aroza - Centro. Ubicados en Av. Colón 950",
  //   "descripcion": "Descubrí el sabor de Los Aroza SRL en nuestra sucursal Los Aroza - Centro. Ubicados en Av. Colón 950, Centro, Córdoba, te esperamos con platos irresistibles, ambiente único y servicio de primera. Reserva tu mesa y vení a vivir una experiencia gastronómica inolvidable.",
  //   "descuentoPorcentaje": null,
  //   "descuentoFijo": null,
  //   "fechaInicio": "2025-10-31T00:00:00",
  //   "fechaFin": "2025-11-30T00:00:00",
  //   "estado": "ACTIVA",
  //   "imagenUrl": null,
  //   "minPersonas": 0,
  //   "maxPersonas": 0,
  //   "codigoPromocion": "B56F867D-F130-4D63-A794-80E4DED780C3",
  //   "requiereCodigo": false
  // },
  // {
  //   "nroRestaurante": "16DEE5C3-9355-4F64-9355-FC79BD28DA63",
  //   "nroIdioma": "2219E244-8413-474D-ABFC-8EDDD06ED2EB",
  //   "nroContenido": "1C4C8ACC-7E03-4D89-9182-74C8D5592A7F",
  //   "titulo": "Los Aroza SRL te invita a su sucursal Los Aroza - Centro, en Av. Colón 950, Centro, Córdoba. Vení a ",
  //   "descripcion": "Los Aroza SRL te invita a su sucursal Los Aroza - Centro, en Av. Colón 950, Centro, Córdoba. Vení a vivir una experiencia gastronómica única: sabores auténticos, ambiente cálido y servicio cercano. Porque en el corazón de Córdoba, cada visita se convierte en un momento especial. Te esperamos.",
  //   "descuentoPorcentaje": null,
  //   "descuentoFijo": null,
  //   "fechaInicio": "2025-10-31T00:00:00",
  //   "fechaFin": "2025-11-30T00:00:00",
  //   "estado": "ACTIVA",
  //   "imagenUrl": null,
  //   "minPersonas": 0,
  //   "maxPersonas": 0,
  //   "codigoPromocion": "AI_4E2ADEAF-FD0D-4451-8BF2-C1A3A220D9A0",
  //   "requiereCodigo": false
  // }
];

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
}