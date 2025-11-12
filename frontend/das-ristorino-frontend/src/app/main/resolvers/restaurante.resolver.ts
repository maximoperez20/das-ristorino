import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { RestauranteResource } from '../api/resources/restaurante-resource';
import { IRestaurante } from '../api/models/i-restaurante';

// @Injectable()
// export class RestauranteResolver implements Resolve<IRestaurante | undefined> {
//   constructor(private _service: RestauranteResource, private router: Router) { }

//   resolve(route: ActivatedRouteSnapshot): Observable<IRestaurante | undefined> {
//     const id = route.paramMap.get('nroRestaurante') || '';
//     // RestauranteResource.obtenerRestaurantePorId espera un objeto { id }
//     return this._service.obtenerRestaurantePorId({ id }).pipe(
//       catchError(err => {
//         // En caso de error redirigimos a la home y retornamos undefined
//         this.router.navigate(['/']);
//         return of(undefined);
//       })
//     );
//   }

// }

export const restauranteResolver : ResolveFn<IRestaurante> = (route, state) => {
  const id = route.paramMap.get('nroRestaurante') || '';
  return inject(RestauranteResource).obtenerRestaurantePorId({ id });
}
