/**
 * Interfaz para la solicitud de registro
 */
export interface IRegisterRequest {
  apellido: string;
  nombre: string;
  password: string;
  correo: string;
  telefonos?: string;
  nroLocalidad: string;
}
