import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LoaderService } from '../services/loader-service';
import { AuthService } from '../services/auth-service';
import { finalize } from 'rxjs';

/**
 * Interceptor HTTP que:
 * 1. Muestra/oculta el loader en todas las peticiones
 * 2. Agrega el token JWT a las peticiones que lo requieren
 * 
 * ESTRATEGIA: Whitelist (lista de endpoints públicos)
 * - Por defecto, TODOS los endpoints de /api/ reciben token si está disponible
 * - Solo los endpoints explícitamente públicos NO reciben token
 * - Esto es más seguro: si olvidas agregar un endpoint privado, el backend lo rechazará
 */
export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const _loader = inject(LoaderService);
  const _auth = inject(AuthService);
  
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
  
  if (shouldAddToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
        
  return next(req).pipe(
    finalize(() => _loader.complete())
  );
};
