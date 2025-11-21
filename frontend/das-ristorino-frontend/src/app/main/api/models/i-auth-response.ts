/**
 * Interfaz para la respuesta de autenticación (login/registro)
 */
export interface IAuthResponse {
  token: string;
  nroCliente: string;
  nombre: string;
  apellido: string;
  correo: string;
}
