import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PromocionResource } from '../api/resources/promocion-resource';
import { IPromocion } from '../api/models/i-promocion';

@Injectable()
export class PromocionesListResolver implements Resolve<IPromocion[] | undefined> {
  constructor(private _service: PromocionResource, private router: Router) { }

  resolve(_route: ActivatedRouteSnapshot): Observable<IPromocion[] | undefined> {
    return this._service.obtenerPromociones().pipe(
      catchError(err => {
        // En caso de error, redirigir o devolver undefined para que la página maneje el fallback
        console.error('Error al resolver promociones:', err);
        this.router.navigate(['/']);
        return of(undefined);
      })
    );
  }
}
