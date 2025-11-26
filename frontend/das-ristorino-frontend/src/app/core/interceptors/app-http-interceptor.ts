import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LoaderService } from '../services/loader-service';
import { AuthService } from '../services/auth-service';
import { LanguageService } from '../services/language-service';
import { finalize } from 'rxjs';

/**
 * Interceptor HTTP que:
 * 1. Muestra/oculta el loader en todas las peticiones
 * 2. Agrega el token JWT a las peticiones que lo requieren
 * 3. Agrega el header X-Nro-Idioma a todas las peticiones /api/*
 * 
 * ESTRATEGIA: Whitelist (lista de endpoints públicos)
 * - Por defecto, TODOS los endpoints de /api/ reciben token si está disponible
 * - Solo los endpoints explícitamente públicos NO reciben token
 * - Esto es más seguro: si olvidas agregar un endpoint privado, el backend lo rechazará
 */
export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const _loader = inject(LoaderService);
  const _auth = inject(AuthService);
  const _language = inject(LanguageService);
  
  _loader.start();
  
  // ============================================
  // ENDPOINTS PÚBLICOS (NO requieren token)
  // ============================================
  // Estos endpoints NO deben recibir el token JWT, incluso si está disponible
  const publicEndpoints: string[] = [
    '/api/clientes/register',      // Registro de nuevos usuarios
    '/api/clientes/login',          // Login de usuarios
    '/api/promociones',             // GET: Lista de promociones (público)
    '/api/localidades',             // GET: Lista de localidades (público)
    '/api/preferencias/categorias', // GET: Categorías de preferencias (público)
    // Endpoints de restaurantes (públicos)
    '/api/restaurantes',            // GET: Lista de restaurantes
    // Nota: /api/restaurantes/{id} y /api/restaurantes/{id}/sucursales/{id}/horarios-disponibles
    // también son públicos pero coinciden con el patrón '/api/restaurantes'
  ];
  
  // ============================================
  // ENDPOINTS CON AUTENTICACIÓN OPCIONAL
  // ============================================
  // Estos endpoints son públicos pero pueden beneficiarse del token si está disponible
  // (ej: para personalizar resultados según el usuario autenticado)
  const optionalAuthEndpoints: string[] = [
    '/api/restaurantes/buscar-nlp', // Búsqueda NLP: usa localidad del usuario si está autenticado
  ];
  
  // ============================================
  // LÓGICA DE DECISIÓN
  // ============================================
  const isApiRequest = req.url.includes('/api/');
  const isPublicEndpoint = publicEndpoints.some(endpoint => req.url.includes(endpoint));
  const isOptionalAuthEndpoint = optionalAuthEndpoints.some(endpoint => req.url.includes(endpoint));
  const token = _auth.getToken();
  
  // Agregar token JWT si:
  // 1. Es una petición a la API
  // 2. Hay un token disponible
  // 3. NO es un endpoint público estricto (o es un endpoint con auth opcional)
  const shouldAddToken = isApiRequest 
    && token 
    && (!isPublicEndpoint || isOptionalAuthEndpoint);
  
  // Agregar header X-Nro-Idioma a todas las peticiones /api/*
  const nroIdioma = _language.getNroIdioma();
  
  // Construir headers
  const headers: { [key: string]: string } = {};
  
  if (shouldAddToken) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  // Agregar header de idioma a todas las peticiones de la API
  if (isApiRequest) {
    headers['X-Nro-Idioma'] = nroIdioma.toString();
  }
  
  // Clonar request con los headers si hay alguno para agregar
  if (Object.keys(headers).length > 0) {
    req = req.clone({
      setHeaders: headers
    });
  }
        
  return next(req).pipe(
    finalize(() => _loader.complete())
  );
};
