import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestauranteResource } from '../api/resources/restaurante-resource';
import { IRestaurante } from '../api/models/i-restaurante';

@Injectable({ providedIn: 'root' })
export class RestauranteResolver implements Resolve<IRestaurante | undefined> {
  constructor(private _service: RestauranteResource, private router: Router) { }

  resolve(route: ActivatedRouteSnapshot): Observable<IRestaurante | undefined> {
    const id = route.paramMap.get('nroRestaurante') || '';
    return this._service.obtenerRestaurantePorId().pipe( //falta agregar el id
      catchError(err => {
        // En caso de error redirigimos a la home y retornamos undefined
        this.router.navigate(['/']);
        return of(undefined);
      })
    );
  }

  
}
