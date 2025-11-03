import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Promocion } from './data/promociones.mock';

@Injectable({
  providedIn: 'root'
})
export class PromocionService {

  private apiUrl = 'http://localhost:8080/api/promociones';

  constructor(private http: HttpClient) { }

  obtenerPromociones(): Observable<Promocion[]> {
    return this.http.get<Promocion[]>(this.apiUrl);
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
