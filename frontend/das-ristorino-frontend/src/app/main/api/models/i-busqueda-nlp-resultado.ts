import { IRestaurante } from './i-restaurante';

/**
 * Interfaz para la respuesta de búsqueda NLP con resultados exactos y sugerencias.
 */
export interface IBusquedaNLPResultado {
  resultadosExactos: IRestaurante[];
  sugerencias: IRestaurante[];
}

