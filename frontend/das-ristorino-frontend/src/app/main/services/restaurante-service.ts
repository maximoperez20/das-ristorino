import { Injectable } from '@angular/core';
import { IRestaurante } from '../api/models/i-restaurante';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private apiUrl = `${environment.apiUrl}/restaurantes`;

  constructor(private http: HttpClient) { }

  obtenerRestaurantes(): Observable<IRestaurante[]> {
    return this.http.get<IRestaurante[]>(this.apiUrl);
  }

  obtenerRestaurantePorId(id: string): Observable<IRestaurante | undefined> {
    return this.http.get<IRestaurante | undefined>(`${this.apiUrl}/${id}`);
  }
  
  
}
