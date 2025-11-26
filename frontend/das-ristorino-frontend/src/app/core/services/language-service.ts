import { Injectable, inject } from '@angular/core';
import { LOCALE_ID } from '@angular/core';

/**
 * Servicio para manejar el idioma actual de la aplicación.
 * Mapea cod_idioma (es-AR, en) a nro_idioma (0, 1) para enviar al backend.
 */
@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  private readonly locale = inject(LOCALE_ID);
  private readonly STORAGE_KEY = 'preferred_language';

  /**
   * Obtiene el código de idioma actual.
   * En desarrollo: detecta por puerto (4200 = es-AR, 4201 = en)
   * En producción: detecta por baseHref (/ = es-AR, /en/ = en) o LOCALE_ID
   */
  getCurrentLanguage(): string {
    // Detectar si estamos en desarrollo
    const currentUrl = typeof window !== 'undefined' ? new URL(window.location.href) : null;
    const isDevelopment = currentUrl && (currentUrl.hostname === 'localhost' || currentUrl.hostname === '127.0.0.1');
    
    if (isDevelopment && currentUrl) {
      // En desarrollo: detectar por puerto
      const currentPort = parseInt(currentUrl.port) || (currentUrl.protocol === 'https:' ? 443 : 80);
      if (currentPort === 4201) {
        return 'en';
      }
      // Puerto 4200 o cualquier otro → es-AR
      return 'es-AR';
    }
    
    // En producción: detectar por baseHref o LOCALE_ID
    if (typeof window !== 'undefined') {
      const pathname = window.location.pathname;
      if (pathname.startsWith('/en/') || pathname === '/en') {
        return 'en';
      }
    }
    
    // Si no hay baseHref, usar LOCALE_ID
    if (this.locale === 'en' || this.locale.startsWith('en')) {
      return 'en';
    }

    // Default: es-AR
    return 'es-AR';
  }

  /**
   * Obtiene el nro_idioma correspondiente al idioma actual.
   * Mapeo: es-AR → 0, en → 1
   */
  getNroIdioma(): number {
    const codIdioma = this.getCurrentLanguage();
    return this.mapCodIdiomaToNroIdioma(codIdioma);
  }

  /**
   * Mapea cod_idioma a nro_idioma.
   * @param codIdioma Código de idioma ('es-AR' o 'en')
   * @returns nro_idioma (0 para es-AR, 1 para en)
   */
  mapCodIdiomaToNroIdioma(codIdioma: string): number {
    if (codIdioma === 'en' || codIdioma.startsWith('en')) {
      return 1; // en-US
    }
    return 0; // es-AR (default)
  }

  /**
   * Establece el idioma preferido y lo guarda en localStorage.
   * En desarrollo: redirige al puerto correspondiente (4200 para es-AR, 4201 para en).
   * En producción: recarga la página con el baseHref correspondiente (/ para es-AR, /en/ para en).
   * 
   * @param codIdioma Código de idioma ('es-AR' o 'en')
   */
  setLanguage(codIdioma: string): void {
    if (codIdioma !== 'es-AR' && codIdioma !== 'en') {
      console.warn(`Idioma no soportado: ${codIdioma}. Usando es-AR como default.`);
      codIdioma = 'es-AR';
    }

    localStorage.setItem(this.STORAGE_KEY, codIdioma);

    const currentUrl = new URL(window.location.href);
    const isDevelopment = currentUrl.hostname === 'localhost' || currentUrl.hostname === '127.0.0.1';
    
    if (isDevelopment) {
      // En desarrollo: cambiar de puerto
      const currentPort = parseInt(currentUrl.port) || (currentUrl.protocol === 'https:' ? 443 : 80);
      const targetPort = codIdioma === 'en' ? 4201 : 4200;
      
      // Si ya estamos en el puerto correcto, no hacer nada
      if (currentPort === targetPort) {
        return;
      }
      
      // Construir nueva URL con el puerto correcto
      const newUrl = `${currentUrl.protocol}//${currentUrl.hostname}:${targetPort}${currentUrl.pathname}${currentUrl.search}`;
      window.location.href = newUrl;
    } else {
      // En producción: cambiar baseHref
      const baseHref = codIdioma === 'en' ? '/en/' : '/';
      const currentPath = window.location.pathname;
      
      // Remover el baseHref actual si existe
      let newPath = currentPath;
      if (currentPath.startsWith('/en/')) {
        newPath = currentPath.substring(4); // Remover '/en/'
      } else if (currentPath === '/en') {
        newPath = '/';
      }

      // Construir la nueva URL
      const newUrl = baseHref + (newPath === '/' ? '' : newPath.substring(1));
      window.location.href = newUrl;
    }
  }

  /**
   * Obtiene el nombre legible del idioma actual.
   */
  getLanguageName(): string {
    const codIdioma = this.getCurrentLanguage();
    return codIdioma === 'en' ? 'English' : 'Español';
  }

  /**
   * Obtiene todos los idiomas disponibles.
   */
  getAvailableLanguages(): Array<{ codIdioma: string; nombre: string; nroIdioma: number }> {
    return [
      { codIdioma: 'es-AR', nombre: 'Español', nroIdioma: 0 },
      { codIdioma: 'en', nombre: 'English', nroIdioma: 1 }
    ];
  }
}

