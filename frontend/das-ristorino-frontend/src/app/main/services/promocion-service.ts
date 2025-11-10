import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { IPromocion } from '../api/models/i-promocion';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PromocionService {

  private apiUrl = `${environment.apiUrl}/promociones`;

  constructor(private http: HttpClient) { }

  obtenerPromociones(): Observable<IPromocion[]> {
    return this.http.get<IPromocion[]>(this.apiUrl);
  }

  /** Registra un click sobre una promoción */
  registrarClickPromocion(
    nroRestaurante: string,
    nroIdioma: string,
    nroContenido: string
  ): Observable<any> {
    const url = `${this.apiUrl}/click`;
    const body = {
      nroRestaurante,
      nroIdioma,
      nroContenido
    };
    return this.http.post(url, body);
  }

}
