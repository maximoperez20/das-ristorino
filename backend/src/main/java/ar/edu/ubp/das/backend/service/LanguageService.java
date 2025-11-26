package ar.edu.ubp.das.backend.service;

import org.springframework.stereotype.Service;

/**
 * Servicio para manejar el idioma (nro_idioma) en las requests HTTP.
 * Valida y proporciona valores por defecto para nro_idioma.
 * 
 * IMPORTANTE: Este servicio SOLO debe usarse en la capa Resource (Controllers).
 * Los Services y Repositories NO deben usar este servicio, sino recibir nro_idioma
 * como parámetro directamente.
 * 
 * Ver PATRON_I18N.md para más detalles sobre el patrón de internacionalización.
 */
@Service
public class LanguageService {

    /**
     * Idioma por defecto: es-AR (nro_idioma = 0)
     */
    private static final int DEFAULT_NRO_IDIOMA = 0;

    /**
     * Obtiene el nro_idioma desde el header de la request.
     * Si el valor es null o inválido, retorna el valor por defecto (0 = es-AR).
     * 
     * @param nroIdiomaHeader Valor del header X-Nro-Idioma (puede ser null)
     * @return nro_idioma válido (0 o 1), o 0 si es null/inválido
     */
    public Integer getNroIdiomaFromRequest(Integer nroIdiomaHeader) {
        if (nroIdiomaHeader == null) {
            return getDefaultNroIdioma();
        }

        // Validar que sea 0 o 1
        if (nroIdiomaHeader == 0 || nroIdiomaHeader == 1) {
            return nroIdiomaHeader;
        }

        // Si es otro valor, usar default
        return getDefaultNroIdioma();
    }

    /**
     * Obtiene el nro_idioma por defecto (es-AR).
     * 
     * @return nro_idioma = 0 (es-AR)
     */
    public Integer getDefaultNroIdioma() {
        return DEFAULT_NRO_IDIOMA;
    }

    /**
     * Valida si un nro_idioma es válido.
     * 
     * @param nroIdioma Número de idioma a validar
     * @return true si es 0 o 1, false en caso contrario
     */
    public boolean isValidNroIdioma(Integer nroIdioma) {
        return nroIdioma != null && (nroIdioma == 0 || nroIdioma == 1);
    }
}

