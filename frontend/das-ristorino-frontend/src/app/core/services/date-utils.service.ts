import { Injectable } from '@angular/core';

/**
 * Servicio para formateo y manejo de fechas.
 * Centraliza el formato de fechas a dd/MM/yyyy y maneja timezone UTC-3 (Buenos Aires).
 */
@Injectable({
  providedIn: 'root'
})
export class DateUtilsService {

  /**
   * Timezone de Buenos Aires (UTC-3)
   */
  private readonly TIMEZONE_BUENOS_AIRES = 'America/Argentina/Buenos_Aires';

  /**
   * Formato de fecha unificado: dd/MM/yyyy
   */
  private readonly DATE_FORMAT: Intl.DateTimeFormatOptions = {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    timeZone: this.TIMEZONE_BUENOS_AIRES
  };

  /**
   * Formato de fecha legible: día completo, mes completo, año
   */
  private readonly DATE_LEGIBLE_FORMAT: Intl.DateTimeFormatOptions = {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    timeZone: this.TIMEZONE_BUENOS_AIRES
  };

  /**
   * Formato de fecha y hora: dd/MM/yyyy HH:mm
   */
  private readonly DATETIME_FORMAT: Intl.DateTimeFormatOptions = {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    timeZone: this.TIMEZONE_BUENOS_AIRES
  };

  /**
   * Convierte una fecha a String con formato dd/MM/yyyy
   * 
   * @param fecha Fecha a formatear (Date, string ISO, o LocalDate)
   * @return String con formato dd/MM/yyyy o string vacío si la fecha es inválida
   */
  formatearFecha(fecha: Date | string | null | undefined): string {
    if (!fecha) {
      return '';
    }

    try {
      const date = typeof fecha === 'string' ? new Date(fecha) : fecha;
      
      if (isNaN(date.getTime())) {
        return '';
      }

      return date.toLocaleDateString('es-AR', this.DATE_FORMAT);
    } catch {
      return '';
    }
  }

  /**
   * Convierte una fecha a String legible (ej: "lunes, 20 de enero de 2025")
   * 
   * @param fecha Fecha a formatear
   * @return String legible o string vacío si la fecha es inválida
   */
  formatearFechaLegible(fecha: Date | string | null | undefined): string {
    if (!fecha) {
      return '';
    }

    try {
      const date = typeof fecha === 'string' ? new Date(fecha) : fecha;
      
      if (isNaN(date.getTime())) {
        return '';
      }

      return date.toLocaleDateString('es-AR', this.DATE_LEGIBLE_FORMAT);
    } catch {
      return '';
    }
  }

  /**
   * Convierte una fecha y hora a String con formato dd/MM/yyyy HH:mm
   * 
   * @param fechaHora Fecha y hora a formatear
   * @return String con formato dd/MM/yyyy HH:mm o string vacío si es inválida
   */
  formatearFechaHora(fechaHora: Date | string | null | undefined): string {
    if (!fechaHora) {
      return '';
    }

    try {
      const date = typeof fechaHora === 'string' ? new Date(fechaHora) : fechaHora;
      
      if (isNaN(date.getTime())) {
        return '';
      }

      return date.toLocaleString('es-AR', this.DATETIME_FORMAT);
    } catch {
      return '';
    }
  }

  /**
   * Convierte una fecha a formato ISO (yyyy-MM-dd) para APIs
   * 
   * @param fecha Fecha a formatear
   * @return String con formato yyyy-MM-dd o string vacío si es inválida
   */
  formatearFechaISO(fecha: Date | string | null | undefined): string {
    if (!fecha) {
      return '';
    }

    try {
      const date = typeof fecha === 'string' ? new Date(fecha) : fecha;
      
      if (isNaN(date.getTime())) {
        return '';
      }

      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      
      return `${year}-${month}-${day}`;
    } catch {
      return '';
    }
  }

  /**
   * Obtiene la fecha actual en timezone de Buenos Aires
   * 
   * @return Date en timezone de Buenos Aires
   */
  fechaActual(): Date {
    return new Date();
  }

  /**
   * Convierte una fecha ISO (yyyy-MM-dd) a Date
   * 
   * @param fechaISO Fecha en formato ISO (yyyy-MM-dd)
   * @return Date o null si la fecha es inválida
   */
  parsearFechaISO(fechaISO: string | null | undefined): Date | null {
    if (!fechaISO || fechaISO.trim() === '') {
      return null;
    }

    try {
      const date = new Date(fechaISO);
      if (isNaN(date.getTime())) {
        return null;
      }
      return date;
    } catch {
      return null;
    }
  }

  /**
   * Convierte una fecha en formato dd/MM/yyyy a Date
   * 
   * @param fecha Fecha en formato dd/MM/yyyy
   * @return Date o null si la fecha es inválida
   */
  parsearFecha(fecha: string | null | undefined): Date | null {
    if (!fecha || fecha.trim() === '') {
      return null;
    }

    try {
      const partes = fecha.split('/');
      if (partes.length !== 3) {
        return null;
      }

      const dia = parseInt(partes[0], 10);
      const mes = parseInt(partes[1], 10) - 1; // Los meses en JS son 0-indexed
      const anio = parseInt(partes[2], 10);

      const date = new Date(anio, mes, dia);
      if (isNaN(date.getTime())) {
        return null;
      }
      return date;
    } catch {
      return null;
    }
  }
}

