import { inject, Injectable } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Observable, of } from 'rxjs';
import { PromocionResource } from '../api/resources/promocion-resource';
import { IPromocion } from '../api/models/i-promocion';

export const promocionesListResolver: ResolveFn<IPromocion[]> = (route, state) =>{
  return inject(PromocionResource).obtenerPromociones();
}
 

// implements Resolve<IPromocion[] | undefined> {
//   constructor(private _service: PromocionResource, private router: Router) { }

//   resolve(_route: ActivatedRouteSnapshot): Observable<IPromocion[] | undefined> {
//     return this._service.obtenerPromociones().pipe(
//       catchError(err => {
//         // En caso de error, redirigir o devolver undefined para que la página maneje el fallback
//         console.error('Error al resolver promociones:', err);
//         this.router.navigate(['/']);
//         return of(undefined);
//       })
//     );
//   }
// }