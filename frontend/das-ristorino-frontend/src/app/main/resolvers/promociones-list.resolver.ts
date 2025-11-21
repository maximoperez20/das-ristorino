import { inject, Injectable } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Observable, of } from 'rxjs';
import { PromocionResource } from '../api/resources/promocion-resource';
import { IPromocion } from '../api/models/i-promocion';

export const promocionesListResolver: ResolveFn<IPromocion[]> = (route, state) =>{
  return inject(PromocionResource).obtenerPromociones();
}
 