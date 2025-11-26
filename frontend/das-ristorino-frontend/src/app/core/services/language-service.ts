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
   * Prioridad: localStorage > LOCALE_ID > default (es-AR)
   */
  getCurrentLanguage(): string {
    // Intentar obtener de localStorage primero
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (stored && (stored === 'es-AR' || stored === 'en')) {
      return stored;
    }

    // Si no hay en localStorage, usar LOCALE_ID
    // LOCALE_ID puede ser 'es-AR' o 'en' según la configuración de build
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
   * Nota: Para cambiar realmente el idioma de la aplicación, se debe recargar la página
   * con el baseHref correspondiente (/ para es-AR, /en/ para en).
   * 
   * @param codIdioma Código de idioma ('es-AR' o 'en')
   */
  setLanguage(codIdioma: string): void {
    if (codIdioma !== 'es-AR' && codIdioma !== 'en') {
      console.warn(`Idioma no soportado: ${codIdioma}. Usando es-AR como default.`);
      codIdioma = 'es-AR';
    }

    localStorage.setItem(this.STORAGE_KEY, codIdioma);

    // Recargar la página con el nuevo idioma
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

