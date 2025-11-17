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
  const publicEndpoints = [
    '/api/restaurantes',
    '/api/promociones',
    '/api/clientes/register',
    '/api/clientes/login'
  ];
  
  // Verificar si la URL es un endpoint público
  const isPublicEndpoint = publicEndpoints.some(endpoint => req.url.includes(endpoint));
  
  // Agregar token JWT solo si:
  // 1. Hay un token disponible
  // 2. La petición es a la API
  // 3. NO es un endpoint público
  const token = _auth.getToken();
  if (token && req.url.includes('/api/') && !isPublicEndpoint) {
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
