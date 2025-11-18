import { IDominioPreferencia } from './i-dominio-preferencia';

export interface ICategoriaConDominios {
  codCategoria: string;
  nombre: string;
  dominios: IDominioPreferencia[];
}

