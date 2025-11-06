import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestauranteService } from '../services/restaurante-service';
import { IRestaurante } from '../api/models/i-restaurante';

@Injectable({ providedIn: 'root' })
export class RestauranteResolver implements Resolve<IRestaurante | undefined> {
  constructor(private service: RestauranteService, private router: Router) { }

  resolve(route: ActivatedRouteSnapshot): Observable<IRestaurante | undefined> {
    const id = route.paramMap.get('nroRestaurante') || '';
    return this.service.obtenerRestaurantePorId(id).pipe(
      catchError(err => {
        // En caso de error redirigimos a la home y retornamos undefined
        this.router.navigate(['/']);
        return of(undefined);
      })
    );
  }

  
}
