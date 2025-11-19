import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LoaderService } from '../services/loader-service';
import { AuthService } from '../services/auth-service';
import { finalize } from 'rxjs';

export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const _loader = inject(LoaderService);
  const _auth = inject(AuthService);
  
  _loader.start();
  
  // Lista de endpoints públicos que NO requieren token
  // NOTA: /api/restaurantes/buscar-nlp es público pero puede beneficiarse del token si el usuario está autenticado
  const publicEndpoints = [
    '/api/promociones',
    '/api/clientes/register',
    '/api/clientes/login'
  ];
  
  // Endpoints que son públicos pero pueden usar el token si está disponible (para personalización)
  const optionalAuthEndpoints = [
    '/api/restaurantes/buscar-nlp'
  ];
  
  // Verificar si la URL es un endpoint público (que no debe recibir token)
  const isPublicEndpoint = publicEndpoints.some(endpoint => req.url.includes(endpoint));
  
  // Verificar si es un endpoint que puede usar token opcionalmente
  const isOptionalAuthEndpoint = optionalAuthEndpoints.some(endpoint => req.url.includes(endpoint));
  
  // Agregar token JWT si:
  // 1. Hay un token disponible
  // 2. La petición es a la API
  // 3. NO es un endpoint público (que no debe recibir token)
  // 4. O es un endpoint que puede usar token opcionalmente
  const token = _auth.getToken();
  if (token && req.url.includes('/api/') && (!isPublicEndpoint || isOptionalAuthEndpoint)) {
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
