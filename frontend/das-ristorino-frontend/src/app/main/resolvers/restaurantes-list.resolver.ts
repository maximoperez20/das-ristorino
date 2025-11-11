import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { RestauranteResource } from '../api/resources/restaurante-resource';
import { IRestaurante } from '../api/models/i-restaurante';

// @Injectable()
// export class RestaurantesListResolver implements Resolve<IRestaurante[] | undefined> {
//   constructor(private _service: RestauranteResource, private router: Router) { }

//   resolve(_route: ActivatedRouteSnapshot): Observable<IRestaurante[] | undefined> {
//     return this._service.obtenerRestaurantes().pipe(
//       catchError(err => {
//         // En caso de error redirigimos a la home y retornamos undefined
//         this.router.navigate(['/']);
//         return of(undefined);
//       })
//     );
//   }

// }

export const restauranteListResolver : ResolveFn<IRestaurante[]> = (_route, _state) => {
  return inject(RestauranteResource).obtenerRestaurantes();
}
