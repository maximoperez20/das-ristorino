import { inject } from '@angular/core';
import { ResolveFn, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PreferenciaResource } from '../api/resources/preferencia-resource';
import { IPreferenciaCliente } from '../api/models/i-preferencia-cliente';
import { AuthService } from '../../core/services/auth-service';

export const misPreferenciasResolver: ResolveFn<IPreferenciaCliente[] | undefined> = (_route, _state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const preferenciaResource = inject(PreferenciaResource);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return of(undefined);
  }

  return preferenciaResource.obtenerMisPreferencias().pipe(
    catchError(err => {
      console.error('Error al resolver mis preferencias:', err);
      // Si hay un error, retornar array vacío para que la página se cargue sin preferencias
      return of([]);
    })
  );
};

