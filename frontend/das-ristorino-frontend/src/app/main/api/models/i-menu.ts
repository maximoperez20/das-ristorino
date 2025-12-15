export interface IMenu {
  nroMenu?: number;
  nombreArchivo?: string;
  tipoMime?: string;
  tamanoBytes?: number;
  datosArchivoBase64?: string; // base64 string desde backend
  exitoso: boolean;
  mensaje?: string;
}